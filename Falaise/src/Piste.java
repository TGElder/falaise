import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Piste
{
	
	private final String name;
	private final String ref;
	private final String difficulty;
	
	private Map<OSMNode,Node> nodes = new HashMap<OSMNode,Node> ();
	
	private List<Route> routes;
	
	public Piste(String name, String ref, String difficulty)
	{
		this.name = name;
		this.ref = ref;
		this.difficulty = difficulty;
	}
	
	
	
	public void addWay(OSMWay way)
	{
		for (int n=0; n<way.getNodes().size()-1; n++)
		{
			OSMNode a = way.getNodes().get(n);
			OSMNode b = way.getNodes().get(n+1); 
			
			Node nodeA = getNode(a);
			Node nodeB = getNode(b);
			
			nodeA.getDownhill().add(nodeB);
			nodeB.getUphill().add(nodeA);
		}
	}
	
	public void addNode(Node node)
	{
		nodes.put(node.getOSMNode(), node);
	}
	
	public void addNodes(Collection<Node> nodes)
	{
		for (Node node : nodes)
		{
			addNode(node);
		}
	}
	
	private Node getNode(OSMNode osmNode)
	{
		Node node = nodes.get(osmNode);
		
		if (node==null)
		{
			node = new Node(osmNode);
			nodes.put(osmNode, node);
		}
		
		return node;
	}

	public String getName()
	{
		return name;
	}

	public String getRef()
	{
		return ref;
	}

	public String getDifficulty()
	{
		return difficulty;
	}
	
	public String toString()
	{
		return name+"/"+ref+"/"+difficulty;
	}
	
	public Collection<Piste> split()
	{
		Collection<Collection<Node>> nodeGroups = new HashSet<Collection<Node>> ();
		
		List<Node> nodes = new ArrayList<Node>(this.nodes.values()); 
		
		while (!nodes.isEmpty())
		{
			Node start = nodes.get(0);
			nodes.remove(0);
			
			List<Node> open = new ArrayList<Node> ();
			Collection<Node> closed = new HashSet<Node> ();
			
			open.add(start);
			
			while(!open.isEmpty())
			{
				Node focus = open.get(0);
				open.remove(0);
				
				for (Node node : focus.getDownhill())
				{
					if (!open.contains(node)&&!closed.contains(node))
					{
						open.add(node);
					}
				}
				
				for (Node node : focus.getUphill())
				{
					if (!open.contains(node)&&!closed.contains(node))
					{
						open.add(node);
					}
				}
				
				closed.add(focus);
			}
			
			nodeGroups.add(closed);
			
		}
		
		Collection<Piste> out = new HashSet<Piste> ();
		
		if (nodeGroups.size()==1)
		{
			out.add(this);
		}
		else
		{
			for (Collection<Node> nodeGroup : nodeGroups)
			{
				Piste piste = new Piste(name,ref,difficulty);
				piste.addNodes(nodeGroup);
				out.add(piste);
			}
		}
		
		return out;
	}
	
	
	public void computeRoutes()
	{

		List<Node> startNodes = new ArrayList<Node> ();
		
		for (Node node : nodes.values())
		{
			if (node.getUphill().isEmpty())
			{
				startNodes.add(node);
			}
		}
		
		routes = new ArrayList<Route> ();
		List<OSMNode> route = new ArrayList<OSMNode> ();
		
		for (Node start : startNodes)
		{
			computeRoutesFrom(start,route);
		}
		
		Collections.sort(routes);

	}
	
	private void computeRoutesFrom(Node node, List<OSMNode> route)
	{
		if (!route.contains(node.getOSMNode()))
		{
			route.add(node.getOSMNode());

			if (node.getDownhill().isEmpty())
			{
				routes.add(new Route(route));
			}
			else
			{
				for (Node downhill : node.getDownhill())
				{
					computeRoutesFrom(downhill,route);
				}
			}
			
			route.remove(node.getOSMNode());
		}
		
	}
	
	public List<Route> getRoutes()
	{
		return routes;
	}


	public class Node
	{
		private OSMNode osmNode;
		private List<Node> downhill = new ArrayList<Node> ();
		private List<Node> uphill = new ArrayList<Node> ();
		
		public Node(OSMNode osmNode)
		{
			this.osmNode = osmNode;
		}
		
		public OSMNode getOSMNode()
		{
			return osmNode;
		}
		
		public List<Node> getDownhill()
		{
			return downhill;
		}
		
		public List<Node> getUphill()
		{
			return uphill;
		}
	}
	
	 

}
