package elder.falaise;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.micromata.opengis.kml.v_2_2_0.*;
import elder.HeightMap;
import elder.osm.OSMNode;
import elder.osm.OSMRelation;
import elder.osm.OSMWay;

/**
 * 
 * Creates ski pistes from open street map data objects
 *
 */
public class PisteManager {

	private final SkiAreaManager skiAreaManager;

	private final Map<Long, PisteWay> pisteWays;

	private Collection<Piste> pistes = new HashSet<Piste>();

	private HeightMap heightmap;

	/**
	 * 
	 * @param heightMap
	 *            Used for working out angles of pistes
	 * @param ways
	 * @param relations
	 */
	public PisteManager(HeightMap heightMap, Collection<OSMWay> ways, Collection<OSMRelation> relations) {

		this.heightmap = heightMap;

		pisteWays = createPisteWays(ways);
		skiAreaManager = new SkiAreaManager(pisteWays, relations);
		skiAreaManager.addAreasToPisteWays(pisteWays.values());
		build(pisteWays.values()); // Pistes are created after Ski Areas to
									// enable grouping of Pistes by Ski Area

	}

	/**
	 * Looks through OSMWay objects for anything that appears to be a piste.
	 * This is anything with the OSM attribute "piste:type=downhill".
	 * 
	 * Name is set from the piste:name attribute, or name attribute otherwise.
	 * Difficulty and reference are set from piste:difficulty and piste:ref
	 * attributes. Any way with the same name, difficulty, reference and in the
	 * same ski area is assumed to be part of the same Piste. Piste objects end
	 * up containing multiple OSMWays.
	 * 
	 * @param ways
	 */
	private void build(Collection<PisteWay> ways) {
		for (PisteWay way : ways) {
			if (way.getAttributes().containsKey("piste:type")
					&& way.getAttributes().get("piste:type").equals("downhill")
					&& !way.getAttributes().containsKey("area")) {

				String name = way.getAttributes().get("piste:name");

				if (name == null) {
					name = way.getAttributes().get("name");
				}

				String ref = way.getAttributes().get("piste:ref");

				if (name != null || ref != null) // Pistes with no name or
													// reference are ignored
				{

					way.orientate(heightmap);

					String difficulty = way.getAttributes().get("piste:difficulty");

					Piste piste = null;

					String area;

					if (way.getAreas().isEmpty()) {
						area = "";
					} else {
						area = way.getAreas().get(0).getName(); // The largest
																// area should
																// be at index 0
					}

					// Set missing attributes to blank strings instead of null
					if (name == null) {
						name = "";
					}
					if (ref == null) {
						ref = "";
					}
					if (area == null) {
						area = "";
					}
					if (difficulty == null) {
						difficulty = "";
					}

					// Checking if this Piste already exists
					for (Piste candidate : pistes) {
						if (candidate.getArea().equals(area)) {
							if (candidate.getName().equals(name)) {
								if (candidate.getRef().equals(ref)) {
									if (candidate.getDifficulty().equals(difficulty)) {
										piste = candidate;
									}
								}
							}
						}
					}

					// Create a new Piste if it doesn't already exist
					if (piste == null) {
						piste = new Piste(area, name, ref, difficulty);
					}

					piste.addWay(way);

					pistes.add(piste);

				}

			}

		}

		Collection<Piste> newPistes = new HashSet<Piste>();

		for (Piste piste : pistes) {

			newPistes.addAll(piste.split());

		}

		pistes = newPistes;

	}

	/**
	 * 
	 * Computes all routes for each piste, and computes angles for the longest
	 * route on each piste.
	 * 
	 */
	void computeRoutes() {
		for (Piste piste : pistes) {
			piste.computeRoutes();

			if (piste.getRoutes().size() > 0) {
				piste.getRoutes().get(0).computeAngles(heightmap); // Index 0
																	// should
																	// contain
																	// longest
																	// route
			}

		}
	}

	/**
	 * Create PisteWays (OSMWays with an additional ski piste information)
	 * 
	 * @param osmWays
	 * @return
	 */
	private Map<Long, PisteWay> createPisteWays(Collection<OSMWay> osmWays) {
		Map<Long, PisteWay> out = new HashMap<Long, PisteWay>();

		for (OSMWay way : osmWays) {
			out.put(way.getID(), new PisteWay(way));
		}

		return out;
	}

	public Collection<SkiArea> getAreas() {
		return skiAreaManager.getAreas();
	}

	/**
	 * 
	 * Writes CSV file with a row for each Piste
	 * 
	 * @param file
	 *            Output file
	 */
	void writeCSV(String file) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("Area,Piste,Ref,Difficulty,Routes,Length,Avg Angle,Max Angle");
			bufferedWriter.newLine();

			for (Piste piste : pistes) {
				String line = "";

				line += piste.getArea() + ",\"" + piste.getName() + "\"," + piste.getRef() + "," + piste.getDifficulty()
						+ "," + piste.getRoutes().size();

				// Length, Average Angle and Max Angle are for longest route in
				// piste
				if (piste.getRoutes().size() > 0) {
					line += "," + piste.getRoutes().get(0).getLength() + "," + piste.getRoutes().get(0).getAvgAngle()
							+ "," + piste.getRoutes().get(0).getMaxAngle();
				}

				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}

			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Writes KML file with a folder for each Piste containing a LineString for
	 * each Route
	 * 
	 * @param file
	 *            Output file
	 */
	public void writeKML(String file) {

		final Kml kml = KmlFactory.createKml();

		final Document document = kml.createAndSetDocument().withName(file).withOpen(false);

		final Style labelStyle = document.createAndAddStyle();

		labelStyle.createAndSetIconStyle().withColor("00FFFFFF");
		labelStyle.createAndSetLabelStyle().withScale(0.7);

		document.createAndAddStyle().withId("red").createAndSetPolyStyle().withColor("000000FFFF");

		Placemark line;
		LineString lineString;

		for (Piste piste : pistes) {
			Folder pisteFolder = document.createAndAddFolder().withName(piste.getArea() + "/" + piste.getName());

			for (int r = 0; r < piste.getRoutes().size(); r++) {
				Route route = piste.getRoutes().get(r);

				line = pisteFolder.createAndAddPlacemark().withName("Route " + r);
				lineString = line.createAndSetLineString();

				for (OSMNode osmNode : route.getNodes()) {
					lineString.addToCoordinates(osmNode.getLongitude(), osmNode.getLatitude());
				}

			}

			for (OSMWay way : piste.getWays()) {
				line = pisteFolder.createAndAddPlacemark().withName(way.getID() + "");
				lineString = line.createAndSetLineString();

				for (OSMNode osmNode : way.getNodes()) {
					lineString.addToCoordinates(osmNode.getLongitude(), osmNode.getLatitude());
				}

			}

		}

		try {
			kml.marshal(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
