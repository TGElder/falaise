package elder.falaise;

import java.util.Collection;
import java.util.HashSet;

import elder.falaise.geometry.Point;
import elder.falaise.geometry.Polygon;
import elder.osm.OSMNode;

/**
 * 
 * Representation of ski area
 *
 */
class SkiArea {

	private String name;
	private Polygon area;
	private Collection<PisteWay> ways = new HashSet<PisteWay>();

	SkiArea(String name, Polygon area) {
		this.name = name;
		this.area = area;
	}

	void addWay(PisteWay way) {
		ways.add(way);
	}

	boolean contains(PisteWay way) {
		if (area == null) {
			return ways.contains(way);
		} else {
			for (OSMNode node : way.getNodes()) {
				if (area.getClockwise().containsPoint(new Point(node.getLatitude(), node.getLongitude()))) {
					return true;
				}
			}

			return false;
		}

	}

	Polygon getArea() {
		return area;
	}

	String getName() {
		return name;
	}

	Collection<PisteWay> getWays() {
		return ways;
	}

	@Override
	public String toString() {
		return getName();
	}

}
