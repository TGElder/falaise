import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;


public abstract class OSMLibrary<T extends OSMElement>
{
	
	private final String tag;
	private final Map<Long,T> library = new HashMap<Long,T> ();
	private final Map<Long,T> missing = new HashMap<Long,T> ();
	
	public OSMLibrary(String tag)
	{
		this.tag = tag;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public T get(Long id)
	{
		T out = library.get(id);
		
		if (out==null)
		{
			out = create(id);
			
			library.put(id, out);
			missing.put(id, out);
		}
		
		return out;
	}
		
	public T getMissing(Long id)
	{
		return missing.get(id);
	}
	
	public int howMany()
	{
		return library.size();
	}
	
	public int howManyMissing()
	{
		return missing.size();
	}
	
	public void setFound(Long id)
	{
		missing.remove(id);
	}
	
	public boolean check(Element element, OSMReader reader)
	{
		if (element.getTagName().equals(getTag()))
		{
			long id = Long.parseLong(element.getAttribute("id"));
			
			T t = getMissing(id);
			
			if (t!=null)
			{
				t.init(element,reader);
				setFound(id);
				return true;
			}
	
		}
		
		return false;
	}
	
	public abstract T create(long id);

	public Collection<T> getValues()
	{
		return library.values();
	}

}
