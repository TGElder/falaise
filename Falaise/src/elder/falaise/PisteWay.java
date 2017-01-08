package elder.falaise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import elder.HeightMap;
import elder.osm.OSMNode;
import elder.osm.OSMWay;

/**
 * 
 * OSMWay with some fields and methods specific to ski pistes: a list of ski
 * areas and a method for orientating the way in a downhill direction
 *
 */
class PisteWay extends OSMWay {

	private OSMWay way;

	private List<SkiArea> areas = new ArrayList<SkiArea>();

	PisteWay(OSMWay way) {
		super(way.getID());
		this.way = way;
	}

	void addSkiArea(SkiArea area) {
		areas.add(area);
	}

	List<SkiArea> getAreas() {
		return areas;
	}

	@Override
	public Map<String, String> getAttributes() {
		return way.getAttributes();
	}

	@Override
	public long getID() {
		return way.getID();
	}

	@Override
	public List<OSMNode> getNodes() {
		return way.getNodes();
	}

	/**
	 * Ensures that the altitude of the first node in the OSMWay is higher than
	 * the last node (i.e. the OSMWay is orientated in a downhill direction)
	 * 
	 * @param heightmap
	 *            Used to determine altitude of nodes
	 */
	void orientate(HeightMap heightmap) {
		if (!way.getNodes().isEmpty()) {

			OSMNode start = way.getNodes().get(0);
			OSMNode finish = way.getNodes().get(way.getNodes().size() - 1);

			Double startHeight = heightmap.getHeightAt(start.getLatitude(), start.getLongitude());
			Double finishHeight = heightmap.getHeightAt(finish.getLatitude(), finish.getLongitude());

			if (finishHeight != null && startHeight != null && finishHeight > startHeight) {
				Collections.reverse(way.getNodes());
			}

		}
	}

}
