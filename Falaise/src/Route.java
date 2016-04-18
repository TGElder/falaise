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
	
	public void computeAngles(Heightmap heightmap)
	{
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
					
					heights[metre] = heightmap.getHeightAt(lat, lon);
					
					metre++;
				}
				
				cumulativeLength += length;
				
			}
			
			avgAngle = 0;
			
			for (int h=0; h<heights.length - 100; h++)
			{
				double rise = heights[h] - heights[h+100];
				double angle = Math.toDegrees(Math.atan(rise/100.0));
				
				maxAngle = Math.max(angle, maxAngle);
				
				avgAngle += angle;
			}
			
			avgAngle = avgAngle / (heights.length - 100);
			
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

}
