import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OSMReader
{
	
	public Element document;
	
	public OSMLibrary<OSMNode> osmNodes = new OSMLibrary<OSMNode>("node") {

		@Override
		public OSMNode create(long id)
		{
			return new OSMNode(id);
		}};
		
	public OSMLibrary<OSMWay> osmWays = new OSMLibrary<OSMWay>("way") {

		@Override
		public OSMWay create(long id)
		{
			return new OSMWay(id);
		}};

	public OSMLibrary<OSMRelation> osmRelations = new OSMLibrary<OSMRelation>("relation") {

		@Override
		public OSMRelation create(long id)
		{
			return new OSMRelation(id);
		}};

	
	public OSMReader(String file)
	{
		document = getDocumentElement(file);
		
	}
	
	public void run()
	{
		while(step());
	}
	
	private boolean step()
	{
		boolean changed=false;
		
		NodeList nodes = document.getChildNodes();
		
		for (int n=0; n<nodes.getLength(); n++)
		{
			if (nodes.item(n).getNodeType()==Node.ELEMENT_NODE)
			{
				Element element = (Element)nodes.item(n);
				
				changed = changed|osmNodes.check(element,this);
				changed = changed|osmWays.check(element,this);
				changed = changed|osmRelations.check(element,this);
			}
		}
		
		System.out.println("nodes "+osmNodes.howMany()+" ("+osmNodes.howManyMissing()+")");
		System.out.println("ways "+osmWays.howMany()+" ("+osmWays.howManyMissing()+")");
		System.out.println("relations "+osmRelations.howMany()+" ("+osmRelations.howManyMissing()+")");
		
		return changed;
	}
	
	public void readAll()
	{
		NodeList nodes = document.getChildNodes();
		
		for (int n=0; n<nodes.getLength(); n++)
		{
			if (nodes.item(n).getNodeType()==Node.ELEMENT_NODE)
			{
				Element element = (Element)nodes.item(n);
				
				if (element.getTagName().equals("node"))
				{
					long id = Long.parseLong(element.getAttribute("id"));
					osmNodes.get(id);
				}
				if (element.getTagName().equals("way"))
				{
					long id = Long.parseLong(element.getAttribute("id"));
					osmWays.get(id);
				}
				if (element.getTagName().equals("relation"))
				{
					long id = Long.parseLong(element.getAttribute("id"));
					osmRelations.get(id);
				}
			}
		}
	}
	
	public Map<Long,String> getSkiAreas()
	{
		Map<Long,String> out = new HashMap<Long,String> ();
		
		NodeList nodes = document.getChildNodes();
		
		for (int n=0; n<nodes.getLength(); n++)
		{
			if (nodes.item(n).getNodeType()==Node.ELEMENT_NODE)
			{
				Element element = (Element)nodes.item(n);
				
				if (element.getTagName().equals("relation"))
				{
					long id = Long.parseLong(element.getAttribute("id"));
					
					Map<String,String> attributes = getAttributes(element);
					
					if (attributes.containsKey("type")&&attributes.get("type").equals("site")&&attributes.containsKey("site")&&attributes.get("site").equals("piste"))
					{
						out.put(id, attributes.get("name"));
					}
					
//					NodeList members = element.getElementsByTagName("member");
					
//					for (int m=0; m<members.getLength(); m++)
//					{
//						Element member = (Element)members.item(m);
//						
//						String type = member.getAttribute("type");
//						
//						if (type.equals("relation"))
//						{
//						
//							long memberID = Long.parseLong(member.getAttribute("ref"));
//					
//						}
//						
//					}
			
				}
			}
		}
		
		return out;
	}
	
	Map<String,String> getAttributes(Element element)
	{
		Map<String,String> out = new HashMap<String,String> ();
		
		NodeList tags = element.getElementsByTagName("tag");
		
		for (int t=0; t<tags.getLength(); t++)
		{						
			Element tag = (Element)tags.item(t);
			
			out.put(tag.getAttribute("k"),tag.getAttribute("v"));
		}

		return out;
	}
	
	private Element getDocumentElement(String path)
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try
		{
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(path);
			return document.getDocumentElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args)
	{
		OSMReader reader = new OSMReader("alps.osm");
//		System.out.println(reader.getSkiAreas());
//		
//		for (Long id : reader.getSkiAreas().keySet())
//		{
//			reader.getRelations().get(id);
//		}
		
//		reader.getRelations().get(3545276l); //Means reader will start by loading Three Valleys relation
//		reader.getRelations().get(5994227l); //Means reader will start by loading Three Valleys relation

		reader.readAll();
		reader.run();
		
		
		AreaBuilder areaBuilder = new AreaBuilder();
		areaBuilder.build(reader.getWays().getValues(),reader.getRelations().getValues());
		areaBuilder.attachToWays(reader.getWays().getValues());
		
		
		int[][] heights = Heightmap.loadFromCSV("french_alps.csv",5003,6730);
		Heightmap heightmap = new Heightmap(5003,6730,6.140138888889,44.560138888883,0.000277777778,heights);
		
		PisteBuilder pisteBuilder = new PisteBuilder(heightmap);
		pisteBuilder.build(reader.getWays().getValues());
		
		
		
		pisteBuilder.write("out.kml");
	}
	
	public OSMLibrary<OSMNode> getNodes()
	{
		return osmNodes;
	}
	
	public OSMLibrary<OSMWay> getWays()
	{
		return osmWays;
	}
	
	public OSMLibrary<OSMRelation> getRelations()
	{
		return osmRelations;
	}

}
