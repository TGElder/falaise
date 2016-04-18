import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

public class OSMNode implements OSMElement
{
	private long id;
	private double latitude;
	private double longitude;
	private Map<String,String> attributes = new HashMap<String,String> ();

	public OSMNode(long id)
	{
		this.id = id;
	}

	@Override
	public void init(Element element, OSMReader reader)
	{
		assert(Long.parseLong(element.getAttribute("id"))==id);
		latitude = Double.parseDouble(element.getAttribute("lat"));
		longitude = Double.parseDouble(element.getAttribute("lon"));
		attributes = reader.getAttributes(element);
	}
	
	public long getID()
	{
		return id;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public Map<String,String> getAttributes()
	{
		return attributes;
	}
	
	public String toString()
	{
		return latitude+","+longitude;
	}

}
