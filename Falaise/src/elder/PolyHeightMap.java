package elder;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * HeightMap backed by multiple sub-HeightMaps
 *
 */
public class PolyHeightMap implements HeightMap {

	private class SubHeightMap {
		HeightMap map;
		double top;
		double bottom;
		double left;
		double right;

		SubHeightMap(HeightMap map, double top, double bottom, double left, double right) {
			this.map = map;
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
		}

		boolean contains(double latitude, double longitude) {
			if (latitude >= bottom && latitude <= top && longitude >= left && longitude <= right) {
				return true;
			}

			return false;
		}
	}

	private List<SubHeightMap> maps = new ArrayList<SubHeightMap>();

	/**
	 * Add a sub-Heightmap. This sub-Heightmap will be used when the height is
	 * required for a latitude and longitude inside the box specified by top,
	 * bottom, left and right.
	 * 
	 * @param map
	 *            sub-Heightmap
	 * @param top
	 * @param bottom
	 * @param left
	 * @param right
	 */
	public void addHeightMap(HeightMap map, double top, double bottom, double left, double right) {
		maps.add(new SubHeightMap(map, top, bottom, left, right));
	}

	@Override
	public Double getHeightAt(double latitude, double longitude) {
		for (SubHeightMap map : maps) {
			if (map.contains(latitude, longitude)) {
				return map.map.getHeightAt(latitude, longitude);
			}
		}

		return null;
	}

}
