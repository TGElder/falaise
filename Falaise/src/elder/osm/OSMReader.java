package elder.osm;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Reads an open street map (*.osm) file and create three OSMLibrary objects
 * containing OSMNode, OSMWay and OSMRelation representations of the elements in
 * the file.
 */
public class OSMReader {

	private final Element document;

	private final OSMLibrary<OSMNode> osmNodes = new OSMLibrary<OSMNode>("node") {

		@Override
		public OSMNode create(long id) {
			return new OSMNode(id);
		}
	};

	private final OSMLibrary<OSMWay> osmWays = new OSMLibrary<OSMWay>("way") {

		@Override
		public OSMWay create(long id) {
			return new OSMWay(id);
		}
	};

	private final OSMLibrary<OSMRelation> osmRelations = new OSMLibrary<OSMRelation>("relation") {

		@Override
		public OSMRelation create(long id) {
			return new OSMRelation(id);
		}
	};

	/**
	 * 
	 * @param file
	 *            Open Street Map file
	 */
	public OSMReader(String file) {
		document = getDocumentElement(file);
	}

	/**
	 * Creates OSMNode, OSMWay and OSMRelation objects to represent every node,
	 * way and relation referenced in the file. This includes defined elements
	 * and elements referenced by other elements. Note that this method creates
	 * objects but does not set the attributes. The run method will set the
	 * attributes.
	 */
	public void findAll() {
		NodeList nodes = document.getChildNodes();

		for (int n = 0; n < nodes.getLength(); n++) {
			if (nodes.item(n).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) nodes.item(n);

				if (element.getTagName().equals("node")) {
					long id = Long.parseLong(element.getAttribute("id"));
					osmNodes.get(id);
				}
				if (element.getTagName().equals("way")) {
					long id = Long.parseLong(element.getAttribute("id"));
					osmWays.get(id);
				}
				if (element.getTagName().equals("relation")) {
					long id = Long.parseLong(element.getAttribute("id"));
					osmRelations.get(id);
				}
			}
		}
	}

	/**
	 * Helper method to create String-String map from the attributes from an XML
	 * element in an OSM file
	 */
	Map<String, String> getAttributes(Element element) {
		Map<String, String> out = new HashMap<String, String>();

		NodeList tags = element.getElementsByTagName("tag");

		for (int t = 0; t < tags.getLength(); t++) {
			Element tag = (Element) tags.item(t);

			out.put(tag.getAttribute("k"), tag.getAttribute("v"));
		}

		return out;
	}

	private Element getDocumentElement(String path) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(path);
			return document.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public OSMLibrary<OSMNode> getNodes() {
		return osmNodes;
	}

	public OSMLibrary<OSMRelation> getRelations() {
		return osmRelations;
	}

	public OSMLibrary<OSMWay> getWays() {
		return osmWays;
	}

	/**
	 * Sets attributes on elements already in OSMLibraries, creates objects to
	 * represent referenced elements missing from OSMLibraries, then repeats
	 * until no new objects are created.
	 */
	public void run() {
		while (step())
			;
	}

	private boolean step() {
		boolean changed = false;

		NodeList nodes = document.getChildNodes();

		for (int n = 0; n < nodes.getLength(); n++) {
			if (nodes.item(n).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) nodes.item(n);

				changed = changed | osmNodes.check(element, this);
				changed = changed | osmWays.check(element, this);
				changed = changed | osmRelations.check(element, this);
			}
		}

		return changed;
	}

}
