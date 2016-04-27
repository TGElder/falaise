import java.util.ArrayList;
import java.util.List;

public class PolyHeightMap implements HeightMap
{
	
	private List<SubHeightMap> maps = new ArrayList<SubHeightMap> ();


	public void addHeightMap(HeightMap map, double top, double bottom, double left, double right)
	{
		maps.add(new SubHeightMap(map,top,bottom,left,right));
	}

	@Override
	public Double getHeightAt(double latitude, double longitude)
	{
		for (SubHeightMap map : maps)
		{
			if (map.contains(latitude, longitude))
			{
				return map.map.getHeightAt(latitude, longitude);
			}
		}
		
		return null;
	}
	
	private class SubHeightMap
	{
		HeightMap map;
		double top;
		double bottom;
		double left;
		double right;
		
		public SubHeightMap(HeightMap map, double top, double bottom, double left, double right)
		{
			this.map = map;
			this.top = top;
			this.bottom = bottom;
			this.left = left;
			this.right = right;
		}
		
		public boolean contains(double latitude, double longitude)
		{
			if (latitude>=bottom&&latitude<=top&&longitude>=left&&longitude<=right)
			{
				return true;
			}
			
			return false;
		}
	}

}
