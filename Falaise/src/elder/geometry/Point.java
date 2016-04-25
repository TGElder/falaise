package elder.geometry;

import java.io.Serializable;
import java.util.Collection;

public class Point implements Geometric, Serializable
{
	private static final long serialVersionUID = 1L;
	
	final public double x;
	final public double y;
		
	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double getDistanceTo(Point other)
	{
		return Math.sqrt(Math.pow(x - other.x,2) + Math.pow(y - other.y,2));
	}
	
	public double getSquareDistanceTo(Point other)
	{
		return Math.pow(x - other.x,2) + Math.pow(y - other.y,2);
	}
		
	public Point getClosest(Collection<Point> points, Double threshold)
	{
		double closestDistance=0;
		Point closest=null;
		
		double threshold2=0;
		
		if (threshold!=null)
		{
			threshold2 = threshold*threshold;
		}
		
		for (Point point : points)
		{
			double focusDistance = getSquareDistanceTo(point);
			
			if (threshold==null||focusDistance <= threshold2)
			{
			
				if (closest==null||focusDistance<closestDistance)
				{
					closest = point;
					closestDistance = focusDistance;
				}
				
			}
				
		}
		
		return closest;
	}
	
	public Point getClosest(Collection<Point> points)
	{
		return getClosest(points,null);
	}
	
	@Override
	public String toString()
	{
		return "("+x+", "+y+")";
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Point))
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}


	@Override
	public Point getMin()
	{
		return this;
	}

	@Override
	public Point getMax()
	{
		return this;
	}

	@Override
	public Geometric getScaled(double xFactor, double yFactor)
	{
		return new Point(x*xFactor,y*yFactor);
	}

	@Override
	public Geometric getTranslated(double xTranslation, double yTranslation)
	{
		return new Point(x+xTranslation,y+yTranslation);
	}

}
