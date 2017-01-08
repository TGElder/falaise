package elder.falaise.geometry;

/**
 * Simple 2D geometry line
 * 
 */
public class Line {

	public final Point a;
	public final Point b;
	public final double length;

	public Line(Point a, Point b) {
		this.a = a;
		this.b = b;
		length = a.getDistanceTo(b);
	}

	public double getLength() {
		return length;
	}

	@Override
	public String toString() {
		return "(" + a + ", " + b + ")";
	}

}
