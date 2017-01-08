Main.java in the elder.falaise packages contains a main method with a documented example of how to use this code to extract piste information from an Open Street Map file.

The input data is not provided. You will need an OSM file and height maps.

To generate my OSM file I started with the latest database extract from http://www.opensnowmap.org/iframes/data.html. I then used osmosis (https://wiki.openstreetmap.org/wiki/Osmosis) and the following command to generate a reduced version that only covered the Alps: "osmosis --read-xml file="C:\Programs\Osmosis\planet_pistes.osm" --bounding-box top=48 left=5 bottom=44 right=17 --write-xml file=alps.osm"

To generate and load height maps you can:
1. Download ASTER Global DEM v2 data using the interface at http://gdex.cr.usgs.gov/gdex/. You need an EarthData login to access this, but this login can be created for free. Data was downloaded in ArcASCII format.
2. Create a CSV file from the table of numbers in the downloaded *.asc file.
3. Load the height map into code with two lines:

int[][] heights1 = ASTERHeightMap.loadFromCSV("alps1.csv", 7919, 14398);
new ASTERHeightMap(4.900138888889, 44.000138888881, 0.000277777778, heights1);

The values come from the header of the downloaded *.asc file:
ncols        7919
nrows        14398
xllcorner    4.900138888889
yllcorner    44.000138888881
cellsize     0.000277777778

4. For larger regions you will need to create a PolyHeightMap and load multiple height maps into it, e.g:

PolyHeightMap heightmap = new PolyHeightMap();
heightmap.addHeightMap(alps1, 48, 44, 5, 7);
heightmap.addHeightMap(alps2, 48, 44, 7, 9);