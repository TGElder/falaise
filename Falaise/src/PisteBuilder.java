import java.util.Collection;
import java.util.HashSet;

public class PisteBuilder
{
	private Heightmap heightmap;
	private Collection<Piste> pistes = new HashSet<Piste> ();
	
	public PisteBuilder(Heightmap heightmap)
	{
		this.heightmap = heightmap;
	}
	
	public void build(Collection<OSMWay> osmWays)
	{
		for (OSMWay osmWay : osmWays)
		{
			if (osmWay.getAttributes().get("piste:type")!=null&&osmWay.getAttributes().get("piste:type").equals("downhill"))
			{
			
				String name = osmWay.getAttributes().get("piste:name");
				
				if (name==null)
				{
					name = osmWay.getAttributes().get("name");
				}
				
				String ref = osmWay.getAttributes().get("piste:ref");
				
				String difficulty = osmWay.getAttributes().get("piste:difficulty");
				
				Piste piste=null;
				
				if (name!=null)
				{
				
					for (Piste candidate : pistes)
					{
						if (equals(candidate.getName(),name))
						{
							if (equals(candidate.getRef(),ref))
							{
								if (equals(candidate.getDifficulty(),difficulty))
								{
									piste = candidate;
								}
							}
						}
					}
					
					if (piste==null)
					{
						piste = new Piste(name,ref,difficulty);
					}
					
					piste.addWay(osmWay);
					
					pistes.add(piste);
					
				}
				
			}
			
		}
		
		Collection<Piste> newPistes = new HashSet<Piste> ();
		
		for (Piste piste : pistes)
		{
			
			newPistes.addAll(piste.split());
			
		}
		
		pistes = newPistes;
		
		for (Piste piste : pistes)
		{
			piste.computeRoutes();
			System.out.print(piste.getName()+","+piste.getRef()+","+piste.getDifficulty()+","+piste.getRoutes().size());
			
			if (piste.getRoutes().size()>0)
			{
				piste.getRoutes().get(0).computeAngles(heightmap);
				System.out.print(","+piste.getRoutes().get(0).getLength()+","+piste.getRoutes().get(0).getAvgAngle()+","+piste.getRoutes().get(0).getMaxAngle());
			}

			System.out.println();

		}
	}
	
	private boolean equals(String a, String b)
	{
		if (a==null&&b==null)
		{
			return true;
		}
		else if (a==null||b==null)
		{
			return false;
		}
		else
		{
			return a.equals(b);
		}
	}

}
