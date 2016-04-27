import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class OSMWay implements OSMElement
{
	private long id;
	private List<OSMNode> osmNodes = new ArrayList<OSMNode> ();
	private Map<String,String> attributes = new HashMap<String,String> ();
	
	private List<SkiArea> areas = new ArrayList<SkiArea> ();

	public OSMWay(long id)
	{
		this.id = id;
	}
	
	public void addSkiArea(SkiArea area)
	{
		areas.add(area);
	}
	
	public List<SkiArea> getAreas()
	{
		return areas;
	}
	
	public void orientate(HeightMap heightmap)
	{
		if (!osmNodes.isEmpty())
		{
		
			OSMNode start = osmNodes.get(0);
			OSMNode finish = osmNodes.get(osmNodes.size()-1);
			
			Double startHeight = heightmap.getHeightAt(start.getLatitude(), start.getLongitude());
			Double finishHeight = heightmap.getHeightAt(finish.getLatitude(), finish.getLongitude());
		
			if (finishHeight!=null&&startHeight!=null&&finishHeight > startHeight)
			{
				Collections.reverse(osmNodes);
				System.out.println("Reversing way "+id+" "+attributes+" in "+areas);
			}
				
			
		}
	}

	@Override
	public void init(Element element, OSMReader reader)
	{
		assert(Long.parseLong(element.getAttribute("id"))==id);
		
		NodeList nodes = element.getElementsByTagName("nd");
		
		for (int n=0; n<nodes.getLength(); n++)
		{
			Element node = (Element)nodes.item(n);
			
			long id = Long.parseLong(node.getAttribute("ref"));
			
			osmNodes.add(reader.getNodes().get(id));
		}
		
		attributes = reader.getAttributes(element);
	}
	
	public long getID()
	{
		return id;
	}
	
	public List<OSMNode> getNodes()
	{
		return osmNodes;
	}
	
	public Map<String,String> getAttributes()
	{
		return attributes;
	}
}
