package elder.geometry;

import java.io.Serializable;

public class Line implements Geometric, Serializable
{

	private static final long serialVersionUID = 1L;
	
	public final Point a;
	public final Point b;
	public final double length;
	
	public Line (Point a, Point b)
	{
		this.a = a;
		this.b = b;
		length = a.getDistanceTo(b);
	}
	
	public double getLength()
	{
		return length;
	}
	
	public String toString()
	{
		return "("+a+", "+b+")";
	}
	
	@Override
	public int hashCode()
	{
		return a.hashCode()+b.hashCode();
	}
	
	public boolean equals(Object other)
	{
		if (!(other instanceof Line))
		{
			return false;
		}
		else if (other==this)
		{
			return true;
		}
		else
		{
			return (a.equals(((Line)other).a)&&b.equals(((Line)other).b))||(a.equals(((Line)other).b)&&b.equals(((Line)other).a));
		}
            
	}
		
	public Point getPoint(double distance)
	{
		final double length = getLength();
		
		final double x = (a.x + (distance/length) * (b.x - a.x));
		final double y = (a.y + (distance/length) * (b.y - a.y));
		
		return new Point(x,y);
	}
	
	public double getDistanceTo(Point point)
	{
		return ((b.y - a.y)*point.x - (b.x -  a.x)*point.y + b.x*a.y - b.y*a.x)/Math.sqrt(Math.pow(b.y - a.y,2) + Math.pow(b.x - a.x,2));
	}
	
	public double getDistanceAlong(Point point)
	{
		//http://www.sunshine2k.de/coding/java/PointOnLine/PointOnLine.html
		
	  // get dot product of e1, e2
	  final Point e1 = new Point(b.x - a.x, b.y - a.y);
	  final Point e2 = new Point(point.x - a.x, point.y - a.y);

	  double dotProduct = e1.x*e2.x + e1.y*e2.y;

	  // get length of vectors
	  final double lengthE1 = Math.sqrt(e1.x * e1.x + e1.y * e1.y);
	  final double lengthE2 = Math.sqrt(e2.x * e2.x + e2.y * e2.y);
	  final double cos = dotProduct/(lengthE1*lengthE2);
	  
	  final double distance = cos * lengthE2;
	
	  return distance;
	}
	
	private Line getCanonical() // Consistent representation for consistent results in intersection procedures
	{
		if (a.x > b.x)
		{
			return this;
		}
		else if (a.x < b.x)
		{
			return new Line(b,a);
		}
		else // equal
		{
			if (a.y > b.y)
			{
				return this;
			}
			else
			{
				return new Line(b,a);
			}
		}
	}
	
	public Point getIntersection(Line other, boolean segmentOnly, boolean otherSegmentOnly, boolean endsCount)
	{
		final Line c = getCanonical();
		final Line cOther = other.getCanonical();
		
		final double ud = ((c.b.y - c.a.y)*(cOther.b.x - cOther.a.x)) - ((c.b.x - c.a.x)*(cOther.b.y - cOther.a.y));		
		double ua = ((c.b.x - c.a.x)*(cOther.a.y - c.a.y)) - ((c.b.y - c.a.y)*(cOther.a.x - c.a.x));
		double ub = ((cOther.b.x - cOther.a.x)*(cOther.a.y - c.a.y)) - ((cOther.b.y - cOther.a.y)*(cOther.a.x - c.a.x));	
			
		if (ud==0)
		{
			if ((ua==0)&&(ub==0))
			{
				// Coincident lines
				return null;
			}
			else
			{
				// Parallel lines
				return null;
			}
		}
		
		ua = ua/ud;
		ub = ub/ud;
		
		boolean returnIntersect=true;
		
		if (segmentOnly)
		{
			if (endsCount)
			{
				returnIntersect = returnIntersect&&(ub>=0)&&(ub<=1);
			}
			else
			{
				returnIntersect = returnIntersect&&(ub>0)&&(ub<1);
			}
		}
		
		if (returnIntersect&&otherSegmentOnly)
		{
			if (endsCount)
			{
				returnIntersect = returnIntersect&&(ua>=0)&&(ua<=1);
			}
			else
			{
				returnIntersect = returnIntersect&&(ua>0)&&(ua<1);
			}
		}
		
	
		if (returnIntersect)
		{
			double nx = (c.a.x + ub*(c.b.x - c.a.x));
			double ny = (c.a.y + ub*(c.b.y - c.a.y));
			return new Point(nx, ny);

		}
		else
		{
			return null;
		}
	}
	
	public Point getIntersectInSegment(Line segment)
	{
		return getIntersection(segment,false,true,true);
	}
	
	public Point getIntersectionInBothSegments(Line segment)
	{
		return getIntersection(segment,true,true,true);		
	}
	
	public Line getProjection(Point point)
	{
		return new Line(point,new Point(point.x + (b.x - a.x),point.y + (b.y - a.y)));
	}
	
	public Line getParallelLine(double distance)
	{
		Point rayStart = getPoint(distance);
		Point rayEnd = getPoint(getLength()-distance);
		
		Point newA = new Point((rayStart.y - a.y) + a.x, - (rayStart.x - a.x) + a.y);
		Point newB = new Point(- (rayEnd.y - b.y) + b.x, (rayEnd.x - b.x) + b.y);
		
		return new Line(newA,newB);
	}

	public Line getPerpendicularLine()
	{
		Point bp = new Point(- (b.y - a.y) + a.x, (b.x - a.x) + a.y);
		return new Line(a,bp);
	}
	
	@Override
	public Point getMin()
	{
		return new Point(Math.min(a.x, b.x),Math.min(a.y, b.y));
	}
	
	@Override
	public Point getMax()
	{
		return new Point(Math.max(a.x, b.x),Math.max(a.y, b.y));
	}

	@Override
	public Geometric getScaled(double xFactor, double yFactor)
	{
		return new Line((Point)a.getScaled(xFactor, yFactor),(Point)b.getScaled(xFactor, yFactor));
	}

	@Override
	public Geometric getTranslated(double xTranslation, double yTranslation)
	{
		return new Line((Point)a.getTranslated(xTranslation, yTranslation),(Point)b.getTranslated(xTranslation, yTranslation));
	}
	
}
