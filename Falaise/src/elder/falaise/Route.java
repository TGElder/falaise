package elder.falaise;

import java.util.ArrayList;
import java.util.List;

import elder.HeightMap;
import elder.falaise.geometry.GreatCircleCalculator;
import elder.osm.OSMNode;

/**
 * 
 * Route through a piste
 *
 */
public class Route implements Comparable<Route> {
	private static int INTERVAL_FOR_MAXIMUM_ANGLE = 100;
	private double maxAngle;
	private double avgAngle;
	private List<OSMNode> osmNodes = new ArrayList<OSMNode>();

	private double length;

	/**
	 * Computes average angle of descent on this route
	 * 
	 * @param osmNodes
	 *            List of OSMNodes ordered from highest to lowest altitude
	 */
	Route(List<OSMNode> osmNodes) {
		this.osmNodes = new ArrayList<OSMNode>(osmNodes);

		length = 0;

		// Computing length of the route
		for (int n = 0; n < osmNodes.size() - 1; n++) {
			OSMNode a = osmNodes.get(n);
			OSMNode b = osmNodes.get(n + 1);

			length += GreatCircleCalculator.distVincenty(a.getLatitude(), a.getLongitude(), b.getLatitude(),
					b.getLongitude());
		}
	}

	@Override
	public int compareTo(Route other) {
		if (getLength() < other.getLength()) {
			return 1;
		} else if (getLength() > other.getLength()) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * Computes average and maximum angle for this route
	 * 
	 * @param heightmap
	 *            For getting heights at each latitude and longitude
	 */
	void computeAngles(HeightMap heightmap) {
		computeAverageAngle(heightmap);
		computeMaximumAngle(heightmap);
	}

	/**
	 * 
	 * @param heightmap
	 *            For getting heights at each latitude and longitude
	 */
	private void computeAverageAngle(HeightMap heightmap) {

		if (osmNodes.size() > 0 && length > 0) {
			OSMNode start = osmNodes.get(0);
			OSMNode finish = osmNodes.get(osmNodes.size() - 1);

			Double startHeight = heightmap.getHeightAt(start.getLatitude(), start.getLongitude());
			Double finishHeight = heightmap.getHeightAt(finish.getLatitude(), finish.getLongitude());

			if (startHeight != null && finishHeight != null) // If either is
																// null then the
																// heightmap
																// does not
																// cover the
																// latitude and
																// longitude, an
																// average angle
																// cannot be
																// computed
			{
				avgAngle = Math.toDegrees(Math.atan((startHeight - finishHeight) / length));
			}

		}
	}

	/**
	 * Computes maximum angle of descent on this route. This is done by walking
	 * through the route one metre at the time, finding the angle across the
	 * next i metres (controlled by static parameter) and taking the maximum
	 * angle found.
	 * 
	 * @param heightMap
	 *            For getting heights at each latitude and longitude
	 */
	void computeMaximumAngle(HeightMap heightmap) {

		if (length > INTERVAL_FOR_MAXIMUM_ANGLE) // Cannot compute a maximum
													// height if the route is
													// shorter than the interval
		{

			// Creating a table with the height at each metre of the route

			double heights[] = new double[(int) Math.ceil(length)];

			double cumulativeLength = 0;
			double length;

			int metre = 0;

			for (int n = 0; n < osmNodes.size() - 1; n++) {
				OSMNode a = osmNodes.get(n);
				OSMNode b = osmNodes.get(n + 1);

				length = GreatCircleCalculator.distVincenty(a.getLatitude(), a.getLongitude(), b.getLatitude(),
						b.getLongitude());

				while (metre < (cumulativeLength + length)) {

					// Getting interpolated latitude and longitude this many
					// metres into the route
					double p = (metre - cumulativeLength) / length;
					double lat = a.getLatitude() + (b.getLatitude() - a.getLatitude()) * p;
					double lon = a.getLongitude() + (b.getLongitude() - a.getLongitude()) * p;

					Double height = heightmap.getHeightAt(lat, lon);

					if (height == null) // Heightmap does not cover this
										// latitude and longitude, maximum angle
										// cannot be computed
					{
						return;
					}

					heights[metre] = height;

					metre++;
				}

				cumulativeLength += length;

			}

			// Calculating across each interval in the route, finding the
			// maximum
			for (int h = 0; h < heights.length - INTERVAL_FOR_MAXIMUM_ANGLE; h++) {
				double rise = heights[h] - heights[h + INTERVAL_FOR_MAXIMUM_ANGLE];
				double angle = Math.toDegrees(Math.atan(rise / (INTERVAL_FOR_MAXIMUM_ANGLE * 1.0)));

				maxAngle = Math.max(angle, maxAngle);
			}

		}

	}

	/**
	 * 
	 * @return Average angle of descent for this route through the piste
	 */
	public double getAvgAngle() {
		return avgAngle;
	}

	public double getLength() {
		return length;
	}

	/**
	 * 
	 * @return Maximum angle of descent on this route
	 */
	public double getMaxAngle() {
		return maxAngle;
	}

	public List<OSMNode> getNodes() {
		return osmNodes;
	}

}
