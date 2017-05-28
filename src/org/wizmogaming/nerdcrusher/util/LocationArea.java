package org.wizmogaming.nerdcrusher.util;

import org.bukkit.Location;

public class LocationArea
{
	private Location Loc1;
	private double X1;
	private double Y1;
	private double Z1;
	
	private Location Loc2;
	private double X2;
	private double Y2;
	private double Z2;
	
	private String WORLD;
	
	public LocationArea(Location one, Location two)
	{
		defaultLocations();
		update(one, two);
	}
	
	public LocationArea(String str)
	{
		String[] s = str.split("=+=");
		//Has to size of eight
		if(s.length != 8)
		{
			defaultLocations();
			return;
		}
		
		//Check if worlds are neither 0000, null, or if they aren't the same
		if(!s[3].equals(s[7]) || s[3].equals("0000") || s[7].equals("0000") || s[3].equals("null") || s[7].equals("null"))
		{
			defaultLocations();
			return;
		}
		
		try
		{
			X1 = Double.parseDouble(s[0]);
			Y1 = Double.parseDouble(s[1]);
			Z1 = Double.parseDouble(s[2]);
			
			X1 = Double.parseDouble(s[4]);
			Y2 = Double.parseDouble(s[5]);
			Z2 = Double.parseDouble(s[6]);
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
			defaultLocations();
			return;
		}
	}
	
	public boolean update(Location one, Location two)
	{
		X1 = one.getX();
		Y1 = one.getY();
		Z1 = one.getZ();
		
		X2 = two.getX();
		Y2 = two.getY();
		Z2 = two.getZ();
		
		if(one.getWorld() != null && two.getWorld() != null && one.getWorld().equals(two.getWorld()))
		{
			WORLD = one.getWorld().getUID().toString();
			Loc1 = new Location(one.getWorld(), X1, Y1, Z1);
			Loc2 = new Location(two.getWorld(), X2, Y2, Z2);
			return true;
		}
		WORLD = "0000";
		return false;
	}
	
	public Location getOne()
	{
		return Loc1;
	}
	
	public Location getTwo()
	{
		return Loc2;
	}
	
	public void defaultLocations()
	{
		Loc1 = new Location(null, 0, 0, 0);
		Loc2 = new Location(null, 0, 0, 0);
		update(Loc1, Loc2);
	}
	
	public String toString()
	{
		if(WORLD == null)
			return null;
		
		return X1 + "=+=" + Y1 + "=+=" + Z1 + "=+=" + WORLD + "=+=" + X2 + "=+=" + Y2 + "=+=" + Z2 + "=+=" + WORLD;
	}
}
