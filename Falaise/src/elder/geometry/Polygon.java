package elder.geometry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Polygon extends ArrayList<Line> implements Geometric, Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static Polygon EMPTY = new Polygon();
	
	public void add(Point point)
	{
		if (isEmpty())
		{
			add(new Line(point,point));
		}
		else
		{
			Line previous = get(size()-1);
			Line next = new Line(point,previous.b);
			set(size()-1,new Line(previous.a,point));
	
			add(next);
		}	
	}

	public boolean containsPoint(Point point)
	{

		boolean c = false;
		
		for (Line line : this)
		{
			if (((line.a.y > point.y) != (line.b.y>point.y)) 
					&& (point.x < (line.b.x - line.a.x) * (point.y - line.a.y) / (line.b.y - line.a.y) + line.a.x))
			{
				c = !c;
			}	
		}
	
		
		return c;
	}
	

	public boolean containsAnyPoint(Collection<Point> points)
	{

		for (Point point : points)
		{
			if (containsPoint(point))
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	public List<Point> filter(Collection<Point> points)
	{
		final List<Point> out = new ArrayList<Point> ();
		
		for (Point point : points)
		{
			if (containsPoint(point))
			{
				out.add(point);
			}
		}
		
		return out;
	}
		
	public double getUnsignedArea()
	{
		double area=0;
		
		for (Line line : this)
		{
			area += (line.b.x - line.a.x)*(line.b.y + line.a.y);
		}
		
		return area/2;	
	}
	
	public double getArea()
	{
		return Math.abs(getUnsignedArea());
	}

	public Polygon getClockwise()
	{
		final double area=getUnsignedArea();
				
		if (area>0)
		{
			return this;
		}
		else
		{
			Polygon out = new Polygon();
			for (int p=size()-1; p>=0; p--)
			{
				out.add(get(p).a);
			}
						
			return out;
		}
	}


	public double getShortestDistanceTo(Polygon other)
	{
		Double out = null; 
		
		for (Line line : this)
		{
			for (Line otherLine : other)
			{
				double currentDistance = line.a.getSquareDistanceTo(otherLine.a);
				
				if (out==null)
				{
					out = currentDistance;
				}
				else
				{
					out = Math.min(out, currentDistance);
				}
			}
		}
		
		return Math.sqrt(out);
		
	}
	
	public boolean containDuplicates()
	{
		for (int p=0; p<size(); p++)
		{
			for (int q=p+1; q<size(); q++)
			{
				if (get(p).a.equals(get(q).a))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public Polygon getBox(Line line)
	{
		
		double minDistance=0;
		double maxDistance=line.length;
		double maxDepth=0;
				
		for (Line other : this)
		{
			
			if (!(line.a.equals(other.a)))
			{
				minDistance = Math.min(minDistance, line.getDistanceAlong(other.a));
				maxDistance = Math.max(maxDistance, line.getDistanceAlong(other.a));
			}
			
			if (!(line.a.equals(other.b)))
			{				
				minDistance = Math.min(minDistance, line.getDistanceAlong(other.b));
				maxDistance = Math.max(maxDistance, line.getDistanceAlong(other.b));
			}
			
			maxDepth = Math.max(maxDepth, line.getDistanceTo(other.a));
			maxDepth = Math.max(maxDepth, line.getDistanceTo(other.b));
		}
	
		Point a = line.getPoint(minDistance);
		Point b = line.getPoint(maxDistance);

		Polygon polygon = new Polygon();
		polygon.add(a);
		polygon.add(b);
		polygon.add(line.getPerpendicularLine().getProjection(b).getPoint(-maxDepth));
		polygon.add(line.getPerpendicularLine().getProjection(a).getPoint(-maxDepth));
		
		return polygon;
	}
	
	public List<Line> getDivideGrid(Line line, Line origin, double spacing, double buffer)
	{
		final Polygon box = getBox(line);
												
		final double a = origin.getDistanceTo(box.get(0).a)/spacing;
		final double b = origin.getDistanceTo(box.get(1).a)/spacing;
		
		double remainder;
		
		if (a<b)
		{
			remainder = Math.ceil(a) - a;
		}
		else
		{
			remainder = a - Math.floor(a);
		}
		
		final double length = box.get(0).length;
					
		final int lines = (int)(length/spacing - remainder);

		final List<Line> out = new ArrayList<Line> ();
		
		for (int i=0; i<=lines;i++)
		{
			double distance = (remainder+i)*spacing;
			
			if (distance>=buffer&&(length-distance)>=buffer)
			{
				Point along = box.get(0).getPoint(distance);
				out.add(box.get(1).getProjection(along));
			}
			
		}

		return out;
	}
	

	@Override
	public Point getMin()
	{
		if (isEmpty())
		{
			return null;
		}
		
		Double x = null;
		Double y = null;
		
		for (Line line : this)
		{
			if (x==null)
			{
				x = line.a.x;
				y = line.a.y;
			}
			else
			{
				x = Math.min(x,line.a.x);
				y = Math.min(y,line.a.y);
			}
		}
		
		return new Point(x,y);
	}

	@Override
	public Point getMax()
	{
		if (isEmpty())
		{
			return null;
		}
		
		Double x = null;
		Double y = null;
		
		for (Line line : this)
		{
			if (x==null)
			{
				x = line.a.x;
				y = line.a.y;
			}
			else
			{
				x = Math.max(x,line.a.x);
				y = Math.max(y,line.a.y);
			}
		}
		
		return new Point(x,y);
	}

	@Override
	public Geometric getScaled(double xFactor, double yFactor)
	{
		Polygon out = new Polygon();
		
		for (Line line : this)
		{
			out.add((Point)line.a.getScaled(xFactor, yFactor));
		}
		
		return out;
	}

	@Override
	public Geometric getTranslated(double xTranslation, double yTranslation)
	{
		Polygon out = new Polygon();
		
		for (Line line : this)
		{
			out.add((Point)line.a.getTranslated(xTranslation, yTranslation));
		}
		
		return out;
	}
}
