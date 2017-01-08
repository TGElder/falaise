package elder.falaise.geometry;

/**
 * Simple 2D geometry point
 * 
 */
public class Point {

	final public double x;
	final public double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getDistanceTo(Point other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
	}

	public double getSquareDistanceTo(Point other) {
		return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
