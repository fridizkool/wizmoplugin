package org.wizmogaming.nerdcrusher.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocSer
{
	public static String getString(Location loc)
	{
		return loc.getX() + ">" + loc.getY() + ">" + loc.getZ() + ">" + loc.getWorld().getUID();
	}
	
	public static Location getLocation(String s)
	{
		String [] parts = s.split(">"); //If you changed the semicolon you must change it here too
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        UUID u = UUID.fromString(parts[3]);
        World w = Bukkit.getServer().getWorld(u);
        return new Location(w, x, y, z); //can return null if the world no longer exists
    }
}
