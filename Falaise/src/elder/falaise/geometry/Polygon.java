package elder.falaise.geometry;

import java.util.ArrayList;

/**
 * Simple 2D geometry Polygon
 *
 */
public class Polygon extends ArrayList<Line> {

	public static Polygon EMPTY = new Polygon();

	public void add(Point point) {
		if (isEmpty()) {
			add(new Line(point, point));
		} else {
			Line previous = get(size() - 1);
			Line next = new Line(point, previous.b);
			set(size() - 1, new Line(previous.a, point));

			add(next);
		}
	}

	public boolean containsPoint(Point point) {

		boolean c = false;

		for (Line line : this) {
			if (((line.a.y > point.y) != (line.b.y > point.y))
					&& (point.x < (line.b.x - line.a.x) * (point.y - line.a.y) / (line.b.y - line.a.y) + line.a.x)) {
				c = !c;
			}
		}

		return c;
	}

	public Polygon getClockwise() {
		final double area = getUnsignedArea();

		if (area > 0) {
			return this;
		} else {
			Polygon out = new Polygon();
			for (int p = size() - 1; p >= 0; p--) {
				out.add(get(p).a);
			}

			return out;
		}
	}

	public double getUnsignedArea() {
		double area = 0;

		for (Line line : this) {
			area += (line.b.x - line.a.x) * (line.b.y + line.a.y);
		}

		return area / 2;
	}

}
