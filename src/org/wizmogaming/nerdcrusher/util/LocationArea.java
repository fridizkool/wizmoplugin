package org.wizmogaming.nerdcrusher.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationArea
{
	private String Loc1;
	private double X1;
	private double Y1;
	private double Z1;
	
	private String Loc2;
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
		
		WORLD = s[3];
		
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
		}
	}
	
	public boolean update(Location one, Location two)
	{
		if(one != null)
		{
			X1 = one.getX();
			Y1 = one.getY();
			Z1 = one.getZ();
			Loc1 = locToStr(new Location(one.getWorld(), X2, Y2, Z2));
		}
		Bukkit.broadcastMessage(X1 +" " + Y1 + " " + Z1);
		if(two != null)
		{
			X2 = two.getX();
			Y2 = two.getY();
			Z2 = two.getZ();
			Loc2 = locToStr(new Location(two.getWorld(), X2, Y2, Z2));
		}
		Bukkit.broadcastMessage(X2 +" " + Y2 + " " + Z2);

		if(one != null && two != null)
		{
			WORLD = one.getWorld().getUID().toString();
			return true;
		}
		return false;
	}
	
	public Location get1()
	{
		return strToLoc(Loc1);
	}
	
	public Location get2()
	{
		return strToLoc(Loc1);
	}
	
	public void set1(Location a)
	{
		update(a, strToLoc(Loc2));
	}
	
	public void set2(Location b)
	{
		update(strToLoc(Loc1), b);
	}
	
	public void defaultLocations()
	{
		Loc1 = "0.0=+=0.0=+=0.0=+=" + Bukkit.getWorlds().get(0) + "=+=0.0=+=0.0=+=0.0";
		Loc2 = "0.0=+=0.0=+=0.0=+=" + Bukkit.getWorlds().get(0) + "=+=0.0=+=0.0=+=0.0";
	}
	
	public String saveString(String loc1, String loc2)
	{		
		return loc1 + "=+=" + loc2;
	}
	
	public static Location strToLoc(String str)
	{
		String[] s = str.split("=+=");
		
		if(s.length != 4)
			return null;
		double x, y, z;
		try
		{
			x = Double.parseDouble(s[0]);
			y = Double.parseDouble(s[1]);
			z = Double.parseDouble(s[2]);
			return new Location(Bukkit.getWorld(s[3]), x, y, z);
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static String locToStr(Location loc)
	{
		return loc.getBlockX() + "=+=" + loc.getBlockY() + "=+=" + loc.getBlockZ() + loc.getWorld().getUID().toString();
	}
	
	public String toString()
	{
		return "Pos1\nX:" + X1 + ", Y:" + Y1 + ", Z: " + Z1 + "\nPos2\nX: " + X2 + ", Y: " + Y2 + ", Z: " + Z2 + "\nIn world: " + WORLD;
	}
}
