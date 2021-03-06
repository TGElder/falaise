package elder.osm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Representation of Open Street Map Way
 * 
 */
public class OSMWay implements OSMElement {
	private long id;
	private List<OSMNode> osmNodes = new ArrayList<OSMNode>();
	private Map<String, String> attributes = new HashMap<String, String>();

	public OSMWay(long id) {
		this.id = id;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public long getID() {
		return id;
	}

	public List<OSMNode> getNodes() {
		return osmNodes;
	}

	@Override
	public void init(Element element, OSMReader reader) {
		assert (Long.parseLong(element.getAttribute("id")) == id);

		NodeList nodes = element.getElementsByTagName("nd");

		for (int n = 0; n < nodes.getLength(); n++) {
			Element node = (Element) nodes.item(n);

			long id = Long.parseLong(node.getAttribute("ref"));

			osmNodes.add(reader.getNodes().get(id));
		}

		attributes = reader.getAttributes(element);
	}
}
