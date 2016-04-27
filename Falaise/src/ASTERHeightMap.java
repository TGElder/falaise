import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class ASTERHeightMap implements HeightMap
{
	private int ncols;
	private int nrows;
	private double xllcorner;
	private double yllcorner;
	private double cellsize;
	private int[][] heights;

	public ASTERHeightMap(int ncols, int nrows, double xllcorner, double yllcorner, double cellsize, int[][]heights)
	{
		this.ncols = ncols;
		this.nrows = nrows;
		this.xllcorner = xllcorner;
		this.yllcorner = yllcorner;
		this.cellsize = cellsize;
		this.heights = heights;
	}
	
	public Double getHeightAt(double latitude, double longitude)
	{
		double dx = (longitude - xllcorner)/cellsize;
		double dy = nrows - ((latitude - yllcorner)/cellsize);
		
		int ix = (int)dx;
		int iy = (int)dy;
		
		if (ix<0||(ix+1)>=ncols||iy<0||(iy+1)>=nrows)
		{
			return null;
		}
		
		double rx = dx - ix;
		double ry = dy - iy;
	
		int topLeft = heights[ix][iy];
		int topRight = heights[ix+1][iy];
		int lowerLeft = heights[ix][iy+1];
		int lowerRight = heights[ix+1][iy+1];
		
		double cTopLeft = (1-rx)*(1-ry)*topLeft;
		double cTopRight = (rx)*(1-ry)*topRight;
		double cLowerLeft = (1-rx)*(ry)*lowerLeft;
		double cLowerRight = (rx)*(ry)*lowerRight;
		
		return cTopLeft+cTopRight+cLowerLeft+cLowerRight;
	}
	
	public static int[][] loadFromCSV(String file, int ncols, int nrows)
	{
						
		BufferedReader bufferedReader;
		String line;

		int[][] out = new int[ncols][nrows];
		
		try
		{
			bufferedReader = new BufferedReader(new FileReader(file));
		
			StringTokenizer stringTokenizer;
			
			try
			{
				int y=0;
				
				while((line = bufferedReader.readLine()) != null)
				{
					
					
					stringTokenizer = new StringTokenizer(line,",");
					
					int x=0;
					
					while (stringTokenizer.hasMoreTokens())
					{						
						out[x][y] = Integer.parseInt(stringTokenizer.nextToken());
							
						x++;
					}
					
					y++;
					
					
				}
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return out;

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			
			return null;
		}
		
	
	}
	
	public static void main(String[] args)
	{
		int[][] heights = loadFromCSV("three valleys.csv",1236,1256);
		ASTERHeightMap heightmap = new ASTERHeightMap(1236,1256,6.402246000000,45.195848611650,0.000277769417,heights);
				
		try 
		{
		    BufferedReader bufferedReader = new BufferedReader(new FileReader("cdcll.csv"));
		    String line;
		    
		    while ((line = bufferedReader.readLine()) != null)
		    {
		    	String [] fields = line.split(",");
		    	double lat = Double.parseDouble(fields[0]);
		    	double lon = Double.parseDouble(fields[1]);
		    	System.out.println(lat+","+lon+","+heightmap.getHeightAt(lat, lon));
		    }
		    bufferedReader.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
}
