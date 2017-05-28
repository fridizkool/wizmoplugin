package org.wizmogaming.nerdcrusher.util;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class PlayerLocations
{
	private OfflinePlayer ThisPlayer;
	private UUID PlayerUUID;
	public LocationArea[] Locations;
	private int Current;
	private boolean New;
	public int Protections;
	
	private static int MaxLocations = 1;
	
	public PlayerLocations(OfflinePlayer p)
	{
		ThisPlayer = p.getPlayer();
		PlayerUUID = ThisPlayer.getUniqueId();
		Current = 0;
		New = true;
		Protections = 0;
		
		Locations = new LocationArea[MaxLocations];
		for(int x = 0; x < Locations.length; x++)
			Locations[x] = null;
	}
	
	public void add(LocationArea a)
	{
		New = false;
		if(Current >= MaxLocations)
			Current = 0;
		Locations[Current] = new LocationArea(a.get1(), a.get2());
		Current++;
	}
	
	public LocationArea get()
	{
		return Locations[Current];
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
		Locations[Current] = new LocationArea(x.get1(), x.get2());
	}
	
	public boolean getNew()
	{
		return New;
	}
}
