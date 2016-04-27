import java.util.ArrayList;
import java.util.List;

public class Route implements Comparable<Route>
{
	private double maxAngle;
	private double avgAngle;
	private List<OSMNode> osmNodes = new ArrayList<OSMNode> ();
	private double length;
	
	public Route(List<OSMNode> osmNodes)
	{
		this.osmNodes = new ArrayList<OSMNode>(osmNodes);
		
		length = 0;
		
		for (int n=0; n<osmNodes.size()-1; n++)
		{
			OSMNode a = osmNodes.get(n);
			OSMNode b = osmNodes.get(n+1);
			
			length += GreatCircleCalculator.distVincenty(a.getLatitude(),a.getLongitude(),b.getLatitude(),b.getLongitude());
		}
	}
	
	public double getLength()
	{
		return length;
	}

	@Override
	public int compareTo(Route other)
	{
		if (getLength()<other.getLength())
		{
			return 1;
		}
		else if (getLength()>other.getLength())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
	
	public void computeAngles(HeightMap heightmap)
	{
		if (osmNodes.size()>0&&length>0)
		{
			OSMNode start = osmNodes.get(0);
			OSMNode finish = osmNodes.get(osmNodes.size()-1);
			
			Double startHeight = heightmap.getHeightAt(start.getLatitude(), start.getLongitude());
			Double finishHeight = heightmap.getHeightAt(finish.getLatitude(), finish.getLongitude());
			
			if (startHeight!=null&&finishHeight!=null)
			{
				avgAngle = Math.toDegrees(Math.atan((startHeight - finishHeight)/length));
			}
			
		}
				
		if (length>100)
		{
		
			double heights[] = new double[(int)Math.ceil(length)];
			
			double cumulativeLength=0;
			double length;
			
			int metre = 0;
			
			for (int n=0; n<osmNodes.size()-1; n++)
			{
				OSMNode a = osmNodes.get(n);
				OSMNode b = osmNodes.get(n+1);
				
				length = GreatCircleCalculator.distVincenty(a.getLatitude(),a.getLongitude(),b.getLatitude(),b.getLongitude());
				
				while(metre<(cumulativeLength+length))
				{					
					double p = (metre-cumulativeLength)/length;
					double lat = a.getLatitude() + (b.getLatitude() - a.getLatitude())*p;
					double lon = a.getLongitude() + (b.getLongitude() - a.getLongitude())*p;
					
					Double height = heightmap.getHeightAt(lat, lon);
					
					if (height==null)
					{
						return;
					}
					
					heights[metre] = height;
					
					metre++;
				}
				
				cumulativeLength += length;
				
			}
			
			
			
			for (int h=0; h<heights.length - 100; h++)
			{
				double rise = heights[h] - heights[h+100];
				double angle = Math.toDegrees(Math.atan(rise/100.0));
				
				maxAngle = Math.max(angle, maxAngle);
			}
						
		}

	}
	
	
	public double getAvgAngle()
	{
		return avgAngle;
	}
	
	public double getMaxAngle()
	{
		return maxAngle;
	}

	public List<OSMNode> getNodes()
	{
		return osmNodes;
	}

}
