package elder.falaise;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;

import elder.falaise.geometry.Point;
import elder.falaise.geometry.Polygon;
import elder.osm.OSMNode;
import elder.osm.OSMRelation;
import elder.osm.OSMWay;

public class SkiAreaManager {
	private final Collection<SkiArea> areas = new HashSet<SkiArea>();

	SkiAreaManager(Map<Long, PisteWay> ways, Collection<OSMRelation> osmRelations) {
		build(ways, osmRelations);
	}

	/**
	 * Add areas to the list in each PisteWay object
	 * 
	 */
	void addAreasToPisteWays(Collection<PisteWay> ways) {
		for (PisteWay way : ways) {
			for (SkiArea area : areas) {
				if (area.contains(way)) {
					way.addSkiArea(area);
				}
			}
		}

		// Sorts Ski Areas attached to each PisteWay so that the largest area is
		// at index 0
		for (PisteWay way : ways) {
			Collections.sort(way.getAreas(), new Comparator<SkiArea>() {

				@Override
				public int compare(SkiArea a, SkiArea b) {

					Integer aSize = a.getWays().size();
					Integer bSize = b.getWays().size();

					return (aSize.compareTo(bSize));
				}
			});
		}
	}

	/**
	 * Creates a collection of ski areas from OSM data
	 * 
	 * This process is complicated by the fact that there are two inconsistent
	 * methods of representing Ski Areas in OpenStreetMap: 1. A relation exists
	 * that collections all the elements that belong to that ski area. 2. A way
	 * exists that defines the boundary of a ski area.
	 * 
	 * Both are handled by the code, but the second case is harder. There is a
	 * Polygon class with a contains method that is used to work out which ways
	 * are contained in the boundary.
	 * 
	 * @param ways
	 *            Can be created from OSMWays using static method in PisteWay
	 */
	private void build(Map<Long, PisteWay> ways, Collection<OSMRelation> osmRelations) {

		// Handles ski areas defined as a boundary
		for (PisteWay pisteWay : ways.values()) {
			if (pisteWay.getAttributes().containsKey("landuse")
					&& pisteWay.getAttributes().get("landuse").equals("winter_sports")) {
				String name = pisteWay.getAttributes().get("name");

				if (name != null) {
					Polygon area = new Polygon();

					for (OSMNode node : pisteWay.getNodes()) {
						area.add(new Point(node.getLatitude(), node.getLongitude()));
					}

					areas.add(new SkiArea(name, area));

				}
			}

		}

		// Handles ski areas defined as a relation
		for (OSMRelation relation : osmRelations) {
			if (relation.getAttributes().containsKey("type") && relation.getAttributes().get("type").equals("site")
					&& relation.getAttributes().containsKey("site")
					&& relation.getAttributes().get("site").equals("piste")) {
				SkiArea skiArea = new SkiArea(relation.getAttributes().get("name"), null);

				areas.add(skiArea);

				for (OSMWay osmWay : relation.getWays()) {
					skiArea.addWay(ways.get(osmWay.getID()));
				}

			}
		}

	}

	Collection<SkiArea> getAreas() {
		return areas;
	}

}
