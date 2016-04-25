package elder.geometry;

public interface Geometric
{
	public Point getMin();
	
	public Point getMax();
	
	public Geometric getScaled(double xFactor, double yFactor);
	
	public Geometric getTranslated(double xTranslation, double yTranslation);
	
}
