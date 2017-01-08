package elder.osm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * 
 * Manages a collection of Open Street Map elements
 *
 * @param Type
 *            of element managed by this library
 */
public abstract class OSMLibrary<T extends OSMElement> {

	private final String tag;
	private final Map<Long, T> library = new HashMap<Long, T>();
	private final Map<Long, T> missing = new HashMap<Long, T>();

	/**
	 * 
	 * @param tag
	 *            XML tag corresponding to elements managed in this library
	 */
	OSMLibrary(String tag) {
		this.tag = tag;
	}

	/**
	 * 
	 * Check if this XML element contains information that can be used to set
	 * the attributes an object tracked by this library. The attributes of the
	 * object are set if so.
	 * 
	 * @param element
	 * @param reader
	 * @return
	 */
	public boolean check(Element element, OSMReader reader) {
		if (element.getTagName().equals(getTag())) {
			long id = Long.parseLong(element.getAttribute("id"));

			T t = getMissing(id);

			if (t != null) {
				t.init(element, reader);
				missing.remove(id);
				return true;
			}

		}

		return false;
	}

	public abstract T create(long id);

	/**
	 * Gets object representing open street map element with the given ID.
	 *
	 * Creates an object if one doesn't already exists. Object attributes are
	 * not set.
	 * 
	 * @param id
	 *            Open street map ID
	 * 
	 */
	public T get(Long id) {
		T out = library.get(id);

		if (out == null) {
			out = create(id);

			library.put(id, out);
			missing.put(id, out);
		}

		return out;
	}

	T getMissing(Long id) {
		return missing.get(id);
	}

	String getTag() {
		return tag;
	}

	/**
	 * 
	 * @return All object managed by this library.
	 */
	public Collection<T> getValues() {
		return library.values();
	}

	/**
	 * 
	 * @return How many objects are tracked by this library
	 */
	int howMany() {
		return library.size();
	}

	/**
	 * 
	 * @return How many objects tracked by this library have not had their
	 *         attributes set
	 */
	int howManyMissing() {
		return missing.size();
	}

}
