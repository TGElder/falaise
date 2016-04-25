import java.util.Collection;
import java.util.HashSet;

import elder.geometry.Point;
import elder.geometry.Polygon;

public class AreaBuilder
{
	private final Collection<SkiArea> areas = new HashSet<SkiArea> ();
	
	public void build(Collection<OSMWay> osmWays, Collection<OSMRelation> osmRelations)
	{		
		for (OSMWay osmWay : osmWays)
		{
			if (osmWay.getAttributes().containsKey("landuse")&&osmWay.getAttributes().get("landuse").equals("winter_sports"))
			{
				String name = osmWay.getAttributes().get("name");
				
				if (name.contains("Huez"))
				{
					System.out.println("BREAK POINT EVEN");
				}
				
				if (name!=null)
				{
					Polygon area = new Polygon();
					
					for (OSMNode node : osmWay.getNodes())
					{
						area.add(new Point(node.getLatitude(),node.getLongitude()));
					}
					
					areas.add(new SkiArea(name,area));
					
					
				}
			}
			
			
			
			
		}
		
		for (OSMRelation relation : osmRelations)
		{
			if (relation.getAttributes().containsKey("type")&&relation.getAttributes().get("type").equals("site")&&relation.getAttributes().containsKey("site")&&relation.getAttributes().get("site").equals("piste"))
			{
				SkiArea skiArea = new SkiArea(relation.getAttributes().get("name"),null);
				
				areas.add(skiArea);
				
				for (OSMWay osmWay : relation.getWays())
				{
					skiArea.addWay(osmWay);
				}
		
			}
		}
			
	}
	
	public void attachToWays(Collection<OSMWay> osmWays)
	{
		for (OSMWay osmWay : osmWays)
		{
			for (SkiArea area : areas)
			{
				if (area.contains(osmWay))
				{
					osmWay.addSkiArea(area);
				}
			}
		}
	}
	
	
	public Collection<SkiArea> getAreas()
	{
		return areas;
	}

}
