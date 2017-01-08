package elder.falaise;

import elder.ASTERHeightMap;
import elder.PolyHeightMap;
import elder.osm.OSMReader;

public class Main {

	/**
	 * Example of how to load open street map data, process it and return piste
	 * data in CSV and KML format
	 */
	public static void main(String[] args) {

		System.out.println("Opening OSM file");

		/*
		 * Piste data is built using Open Street Map data.
		 * 
		 * OpenStreetMap data comprises three different types of element: 1.
		 * Nodes: Points with latitude and longitude 2. Ways: A"path": An
		 * ordered list of points 3. Relations: Grouping of nodes, ways and
		 * other relations. Each element has a unique ID.
		 * 
		 * These elements are represented by the classes OSMNode, OSMWay and
		 * OSMRelation.
		 * 
		 * OSMReader reads an OSM files and creates OSMNode, OSMWay or
		 * OSMRelation objects for the elements described in the file. OSMReader
		 * maintains three OSMLibrary objects, for each type of element. These
		 * manage the creation of objects and are able to return an object for a
		 * given element ID.
		 */
		OSMReader reader = new OSMReader("alps.osm");

		System.out.println("Finding OSM objects");
		/*
		 * The findAll method from OSMReader goes through the OSM file, creating
		 * placeholder objects for every element in the file.
		 * 
		 * Alternatively you can directly call get on a specific OSM relation
		 * like on the commented out line. 3545276l is the ID of the OSM Three
		 * Valleys Ski Area relation
		 */
		reader.findAll();
		// reader.getRelations().get(3545276l);

		System.out.println("Setting up OSM objects");
		/*
		 * The run method of the OSMReader class sets attributes on placeholder
		 * objects, creates placeholder objects for any new elements found, and
		 * repeats until no new objects are created.
		 * 
		 * If findAll was used previously no new elements will be found.
		 * Alternatively, if you started by providing a specific relation, it
		 * will find all the elements in that relation on the first iteration
		 * and continue from there.
		 */
		reader.run();

		/*
		 * Height maps are created from CSV files. Separate height maps are
		 * required because Open Street Map nodes do not have altitudes.
		 */
		System.out.println("Loading alps 1.csv");
		int[][] heights1 = ASTERHeightMap.loadFromCSV("alps1.csv", 7919, 14398);
		ASTERHeightMap alps1 = new ASTERHeightMap(4.900138888889, 44.000138888881, 0.000277777778, heights1);

		System.out.println("Loading alps 2.csv");
		int[][] heights2 = ASTERHeightMap.loadFromCSV("alps2.csv", 7919, 14398);
		ASTERHeightMap alps2 = new ASTERHeightMap(6.900138888889, 44.000138888881, 0.000277777778, heights2);

		System.out.println("Loading alps 3.csv");
		int[][] heights3 = ASTERHeightMap.loadFromCSV("alps3.csv", 7919, 10798);
		ASTERHeightMap alps3 = new ASTERHeightMap(8.900138888889, 45.000138888882, 0.000277777778, heights3);

		System.out.println("Loading alps 4.csv");
		int[][] heights4 = ASTERHeightMap.loadFromCSV("alps4.csv", 7919, 10798);
		ASTERHeightMap alps4 = new ASTERHeightMap(10.900138888889, 45.000138888882, 0.000277777778, heights4);

		System.out.println("Loading alps 5.csv");
		int[][] heights5 = ASTERHeightMap.loadFromCSV("alps5.csv", 7919, 10798);
		ASTERHeightMap alps5 = new ASTERHeightMap(12.900138888889, 45.000138888882, 0.000277777778, heights5);

		System.out.println("Loading alps 6.csv");
		int[][] heights6 = ASTERHeightMap.loadFromCSV("alps6.csv", 7919, 10798);
		ASTERHeightMap alps6 = new ASTERHeightMap(14.900138888889, 45.000138888882, 0.000277777778, heights6);

		/*
		 * A PolyHeightMap is created from all the previously created
		 * HeightMaps. The parameters passed with each HeightMap form a box; the
		 * HeightMap is used when the requested longitude and latitude are
		 * inside its box.
		 */
		System.out.println("Creating heightmap");
		PolyHeightMap heightmap = new PolyHeightMap();
		heightmap.addHeightMap(alps1, 48, 44, 5, 7);
		heightmap.addHeightMap(alps2, 48, 44, 7, 9);
		heightmap.addHeightMap(alps3, 48, 45, 9, 11);
		heightmap.addHeightMap(alps4, 48, 45, 11, 13);
		heightmap.addHeightMap(alps5, 48, 45, 13, 15);
		heightmap.addHeightMap(alps6, 48, 45, 15, 17);

		System.out.println("Building pistes");
		/*
		 * The PisteManager creates Piste objects from OSMWay objects. This is
		 * complicated because OSM data may split a single piste into multiple
		 * OSMWays. The build method of PisteManager attempts to work out which
		 * OSMWays are part of the same piste.
		 * 
		 * Before grouping the OSMWays, a SkiAreaManager object is used to work
		 * out which OSMWays are in which Ski Area. This is so similarly named
		 * pistes in different ski areas are not grouped.
		 */
		PisteManager pisteManager = new PisteManager(heightmap, reader.getWays().getValues(),
				reader.getRelations().getValues());

		System.out.println("Computing routes");
		/*
		 * The OSMWays within a Piste will not necessarily result in a single
		 * path. A Piste can have multiple start points, multiple end points and
		 * multiple routes from top to bottom. Piste objects have a collection
		 * of Routes objects to capture this information. The computeRoutes
		 * method of the PisteManager computes these routes.
		 */
		pisteManager.computeRoutes();

		System.out.println("Writing output");
		/*
		 * Information about each Piste can be written to CSV or KML. Statistics
		 * for each piste are included in the CSV. This includes average angle
		 * of descent, maximum angle of descent and length. These statistics are
		 * for the longest route through the piste.
		 */
		pisteManager.writeCSV("out.csv");
		pisteManager.writeKML("out.kml");
	}

}
