
import java.util.Collection;
import java.util.HashSet;

import elder.geometry.Point;
import elder.geometry.Polygon;

public class SkiArea
{
	
	private String name;
	private Polygon area;
	private Collection<OSMWay> ways = new HashSet<OSMWay> ();
	
	public SkiArea(String name, Polygon area)
	{
		this.name = name;
		this.area = area;
	}
	
	public void addWay(OSMWay osmWay)
	{
		ways.add(osmWay);
	}

	public String getName()
	{
		return name;
	}
	
	public Polygon getArea()
	{
		return area;
	}
	
	public boolean contains(OSMWay osmWay)
	{
		if (area==null)
		{
			return ways.contains(osmWay);
		}
		else
		{
			for (OSMNode node : osmWay.getNodes())
			{
				if (area.getClockwise().containsPoint(new Point(node.getLatitude(),node.getLongitude())))
				{
					return true;
				}
			}
			
			return false;
		}

	}

}
