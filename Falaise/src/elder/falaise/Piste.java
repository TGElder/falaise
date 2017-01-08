package elder.falaise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import elder.osm.OSMNode;
import elder.osm.OSMWay;

/**
 * 
 * Representation of ski piste
 *
 */
public class Piste {

	private class Node {
		private OSMNode osmNode;
		private List<Node> downhill = new ArrayList<Node>();
		private List<Node> uphill = new ArrayList<Node>();

		Node(OSMNode osmNode) {
			this.osmNode = osmNode;
		}

		List<Node> getDownhill() {
			return downhill;
		}

		OSMNode getOSMNode() {
			return osmNode;
		}

		List<Node> getUphill() {
			return uphill;
		}
	}

	private final String area;
	private final String name;
	private final String reference;

	private final String difficulty;

	private final Map<OSMNode, Node> nodes = new HashMap<OSMNode, Node>();

	private final List<Route> routes = new ArrayList<Route>();

	private final Collection<OSMWay> ways = new HashSet<OSMWay>();

	/**
	 * 
	 * @param area
	 *            Ski area where this piste is found
	 * @param name
	 *            Name of this piste
	 * @param reference
	 *            Reference of this piste (for example a piste may have a number
	 *            in addition to its name)
	 * @param difficulty
	 *            Difficulty of the piste
	 */
	Piste(String area, String name, String reference, String difficulty) {
		this.area = area;
		this.name = name;
		this.reference = reference;
		this.difficulty = difficulty;
	}

	private void addNode(Node node) {
		nodes.put(node.getOSMNode(), node);
	}

	private void addNodes(Collection<Node> nodes) {
		for (Node node : nodes) {
			addNode(node);
		}
	}

	/**
	 * Add an OSMWay to the Piste. Internal node objects are created for each
	 * OSMNode in the OSMWay. These Nodes link to other Nodes according to this
	 * OSMWay and OSMWays added previously.
	 * 
	 * @param way
	 */
	void addWay(OSMWay way) {
		for (int n = 0; n < way.getNodes().size() - 1; n++) {
			OSMNode a = way.getNodes().get(n);
			OSMNode b = way.getNodes().get(n + 1);

			Node nodeA = getNode(a);
			Node nodeB = getNode(b);

			if (!nodeA.getDownhill().contains(nodeB)) {
				nodeA.getDownhill().add(nodeB);
			}
			if (!nodeB.getUphill().contains(nodeA)) {
				nodeB.getUphill().add(nodeA);
			}
		}

		ways.add(way);
	}

	/**
	 * Computes possible ways of traversing the piste and stores these as route
	 * objects within the routes property
	 */
	void computeRoutes() {

		// Works out which nodes have no uphill node, and are therefore valid
		// start points for traversing the piste
		List<Node> startNodes = new ArrayList<Node>();

		for (Node node : nodes.values()) {
			if (node.getUphill().isEmpty()) {
				startNodes.add(node);
			}
		}

		List<OSMNode> route = new ArrayList<OSMNode>();

		for (Node start : startNodes) {
			computeRoutesFrom(start, route);
		}

		Collections.sort(routes);

	}

	/**
	 * Recursively works through all possible paths downhill from a node. When a
	 * node is encountered with no downhill nodes, a new route is added to
	 * routes.
	 * 
	 * @param route
	 *            Nodes already traversed to reach this node
	 */
	private void computeRoutesFrom(Node node, List<OSMNode> route) {
		if (!route.contains(node.getOSMNode())) {
			route.add(node.getOSMNode());

			if (node.getDownhill().isEmpty()) {
				routes.add(new Route(route));
			} else {
				for (Node downhill : node.getDownhill()) {
					computeRoutesFrom(downhill, route);
				}
			}

			route.remove(node.getOSMNode());
		}

	}

	public String getArea() {
		return area;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public String getName() {
		return name;
	}

	/**
	 * 
	 * 
	 * @return The internal Node corresponding to an OSMNode. An internal Node
	 *         is created if one doesn't already exist.
	 */
	private Node getNode(OSMNode osmNode) {
		Node node = nodes.get(osmNode);

		if (node == null) {
			node = new Node(osmNode);
			nodes.put(osmNode, node);
		}

		return node;
	}

	public String getRef() {
		return reference;
	}

	/**
	 * 
	 * @return All possible ways of traversing this piste
	 */
	public List<Route> getRoutes() {
		return routes;
	}

	public Collection<OSMWay> getWays() {
		return ways;
	}

	/**
	 * Groups nodes. Nodes are in the same group if there is a downhill path
	 * from one to the other. A new Piste object is created for each group,
	 * unless there is a single group in which case the existing Piste object is
	 * returned.
	 *
	 */
	Collection<Piste> split() {
		Collection<Collection<Node>> nodeGroups = new HashSet<Collection<Node>>();

		List<Node> nodes = new ArrayList<Node>(this.nodes.values());

		while (!nodes.isEmpty()) {
			Node start = nodes.get(0);
			nodes.remove(0);

			List<Node> open = new ArrayList<Node>();
			Collection<Node> closed = new HashSet<Node>();

			open.add(start);

			while (!open.isEmpty()) {
				Node focus = open.get(0);
				open.remove(0);

				for (Node node : focus.getDownhill()) {
					if (!open.contains(node) && !closed.contains(node)) {
						open.add(node);
					}
				}

				for (Node node : focus.getUphill()) {
					if (!open.contains(node) && !closed.contains(node)) {
						open.add(node);
					}
				}

				closed.add(focus);
			}

			nodeGroups.add(closed);

		}

		Collection<Piste> out = new HashSet<Piste>();

		if (nodeGroups.size() == 1) {
			out.add(this);
		} else {
			for (Collection<Node> nodeGroup : nodeGroups) {
				Piste piste = new Piste(area, name, reference, difficulty);
				piste.addNodes(nodeGroup);
				out.add(piste);
			}
		}

		return out;
	}

	@Override
	public String toString() {
		return area + "/" + name + "/" + reference + "/" + difficulty;
	}

}
