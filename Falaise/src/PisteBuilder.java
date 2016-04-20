import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;

import de.micromata.opengis.kml.v_2_2_0.*;


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
			if (osmWay.getAttributes().containsKey("piste:type")
					&&osmWay.getAttributes().get("piste:type").equals("downhill")
					&&!osmWay.getAttributes().containsKey("area"))
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
		
		write("ways.kml");
		
		Collection<Piste> newPistes = new HashSet<Piste> ();
		
		for (Piste piste : pistes)
		{
			
			newPistes.addAll(piste.split());
			
		}
		
		pistes = newPistes;
		
		for (Piste piste : pistes)
		{
			piste.computeRoutes();
			System.out.print("Les Trois Vall�es,"+piste.getName()+","+piste.getRef()+","+piste.getDifficulty()+","+piste.getRoutes().size());
			
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
	
	public void write(String file)
	{
	
		final Kml kml = KmlFactory.createKml();
				
		final Document document = kml.createAndSetDocument().withName(file).withOpen(false);
						
		final Style labelStyle = document.createAndAddStyle();
		
		
		
		labelStyle.createAndSetIconStyle().withColor("00FFFFFF");
		labelStyle.createAndSetLabelStyle().withScale(0.7);
		
		document.createAndAddStyle().withId("red").createAndSetPolyStyle().withColor("000000FFFF");	

		Placemark line;
		LineString lineString; 
		
		for (Piste piste : pistes)
		{
			Folder pisteFolder = document.createAndAddFolder().withName(piste.getName());
			
			for (int r=0 ; r<piste.getRoutes().size(); r++)
			{
				Route route = piste.getRoutes().get(r);
				
				line = pisteFolder.createAndAddPlacemark().withName("Route "+r);
				lineString = line.createAndSetLineString();
			
				for (OSMNode osmNode : route.getNodes())
				{
					lineString.addToCoordinates(osmNode.getLongitude(), osmNode.getLatitude());
				}

			}
			
			for (OSMWay way : piste.getWays())
			{
				line = pisteFolder.createAndAddPlacemark().withName(way.getID()+"");
				lineString = line.createAndSetLineString();
			
				for (OSMNode osmNode : way.getNodes())
				{
					lineString.addToCoordinates(osmNode.getLongitude(), osmNode.getLatitude());
				}

			}
			
		}
		
		

		
		try 
		{
			kml.marshal(new File(file));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
	}

}
