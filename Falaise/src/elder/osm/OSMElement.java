package elder.osm;

import org.w3c.dom.Element;

public interface OSMElement {
	/**
	 * Set the attributes of this object from an XML element
	 * 
	 * @param element
	 *            XML element
	 * @param reader
	 *            OSMReader tracking other open street map elements that may be
	 *            referenced by this element
	 */
	void init(Element element, OSMReader reader);

}
