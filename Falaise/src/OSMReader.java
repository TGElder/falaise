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
		OSMReader reader = new OSMReader("vt2.osm");
		reader.getRelations().get(3545276l); //Means reader will start by loading Three Valleys relation
		reader.run();
		
		int[][] heights = Heightmap.loadFromCSV("three valleys.csv",1236,1256);
		Heightmap heightmap = new Heightmap(1236,1256,6.402246000000,45.195848611650,0.000277769417,heights);
		
		PisteBuilder pisteBuilder = new PisteBuilder(heightmap);
		pisteBuilder.build(reader.getWays().getValues());
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
