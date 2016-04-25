import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OSMRelation implements OSMElement
{
	private long id;
	private List<OSMNode> osmNodes = new ArrayList<OSMNode> ();
	private List<OSMWay> osmWays = new ArrayList<OSMWay> ();
	private List<OSMRelation> osmRelations = new ArrayList<OSMRelation> ();
	private Map<String,String> attributes = new HashMap<String,String> ();

	public OSMRelation(long id)
	{
		this.id = id;
	}

	@Override
	public void init(Element element, OSMReader reader)
	{
		assert(Long.parseLong(element.getAttribute("id"))==id);
		
		NodeList nodes = element.getElementsByTagName("member");
		
		for (int n=0; n<nodes.getLength(); n++)
		{
			Element node = (Element)nodes.item(n);
			
			String type = node.getAttribute("type");
			long id = Long.parseLong(node.getAttribute("ref"));
			
			if (type.equals("node"))
			{
				osmNodes.add(reader.getNodes().get(id));
			}
			else if (type.equals("way"))
			{
				OSMWay way = reader.getWays().get(id);
				osmWays.add(way);
				
			}
			else if (type.equals("relation"))
			{
				osmRelations.add(reader.getRelations().get(id));
			}
			
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
	
	public List<OSMWay> getWays()
	{
		return osmWays;
	}
	
	public List<OSMRelation> getRelations()
	{
		return osmRelations;
	}
	
	public Map<String,String> getAttributes()
	{
		return attributes;
	}
}
