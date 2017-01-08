package elder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * 
 * Creates a height map object backed with data in the ASTER ASCII format
 *
 */
public class ASTERHeightMap implements HeightMap {
	/**
	 * Helper method to create data table from CSV file
	 * 
	 * @param file
	 *            Description Path of CSV file
	 * @param ncols
	 *            Description Columns in the file
	 * @param nrows
	 *            Description Rows in the file
	 * @return Table of heights
	 */
	public static int[][] loadFromCSV(String file, int ncols, int nrows) {

		BufferedReader bufferedReader;
		String line;

		int[][] out = new int[ncols][nrows];

		try {
			bufferedReader = new BufferedReader(new FileReader(file));

			StringTokenizer stringTokenizer;

			try {
				int y = 0;

				while ((line = bufferedReader.readLine()) != null) {

					stringTokenizer = new StringTokenizer(line, ",");

					int x = 0;

					while (stringTokenizer.hasMoreTokens()) {
						out[x][y] = Integer.parseInt(stringTokenizer.nextToken());

						x++;
					}

					y++;

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return out;

		} catch (FileNotFoundException e) {
			e.printStackTrace();

			return null;
		}

	}

	private int ncols;
	private int nrows;
	private double xllcorner;
	private double yllcorner;
	private double cellsize;

	private int[][] heights;

	/**
	 * 
	 * @param xllcorner
	 *            Description Longitude of lowest left cell
	 * @param yllcorner
	 *            Description Latitude of lowest left cell
	 * @param cellsize
	 *            Description Width/height of each cell (in degrees)
	 * @param heights
	 *            Description Table of heights
	 */
	public ASTERHeightMap(double xllcorner, double yllcorner, double cellsize, int[][] heights) {
		this.ncols = heights.length;
		this.nrows = heights[0].length;
		this.xllcorner = xllcorner;
		this.yllcorner = yllcorner;
		this.cellsize = cellsize;
		this.heights = heights;
	}

	@Override
	public Double getHeightAt(double latitude, double longitude) {
		double dx = (longitude - xllcorner) / cellsize;
		double dy = nrows - ((latitude - yllcorner) / cellsize);

		int ix = (int) dx;
		int iy = (int) dy;

		if (ix < 0 || (ix + 1) >= ncols || iy < 0 || (iy + 1) >= nrows) {
			return null;
		}

		double rx = dx - ix;
		double ry = dy - iy;

		int topLeft = heights[ix][iy];
		int topRight = heights[ix + 1][iy];
		int lowerLeft = heights[ix][iy + 1];
		int lowerRight = heights[ix + 1][iy + 1];

		double cTopLeft = (1 - rx) * (1 - ry) * topLeft;
		double cTopRight = (rx) * (1 - ry) * topRight;
		double cLowerLeft = (1 - rx) * (ry) * lowerLeft;
		double cLowerRight = (rx) * (ry) * lowerRight;

		return cTopLeft + cTopRight + cLowerLeft + cLowerRight;
	}

}
