

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public abstract class XMLReader<T>
{
	private String tag;
	
	public XMLReader(String tag)
	{
		this.tag = tag;
	}
	
	public Element getDocumentElement(String path)
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
	
	public abstract T loadOne(Element element);
	
	public Map<String,T> loadMany(Element element)
	{
		Map<String,T> out = new HashMap<String,T> ();
		String id;
		T t;
		
		for (int n=0; n<element.getElementsByTagName(tag).getLength(); n++)
		{
			id = element.getElementsByTagName("id").item(n).getTextContent();
			t = loadOne((Element)element.getElementsByTagName(tag).item(n));
			out.put(id, t);
		}
		return out;
	}
	
	
	
}
