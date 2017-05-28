package org.wizmogaming.nerdcrusher.util;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class PlayerLocations
{
	private OfflinePlayer ThisPlayer;
	private UUID PlayerUUID;
	public LocationArea Locations;
//	private int Current;
	private boolean New;
	public int Protections;
	
//	private static int MaxLocations = 1;
	
	public PlayerLocations(OfflinePlayer p)
	{
		ThisPlayer = p.getPlayer();
		PlayerUUID = ThisPlayer.getUniqueId();
//		Current = -1;
		New = true;
		Protections = 0;
		
		Locations = new LocationArea(null, null);
	}
	
	public void add(LocationArea a)
	{
		New = false;
		Locations = new LocationArea(a.get1(), a.get2());
	}
	
	public LocationArea get()
	{
		return Locations;
	}
	
	public OfflinePlayer getPlayer()
	{
		return ThisPlayer;
	}
	
	public UUID getUUID()
	{
		return PlayerUUID;
	}
	
	public void set(LocationArea x)
	{
		if(x.get1() != null)
			Locations.set1(x.get1());
		if(x.get2() != null)
			Locations.set2(x.get2());
	}
	
	public boolean getNew()
	{
		return New;
	}
}
