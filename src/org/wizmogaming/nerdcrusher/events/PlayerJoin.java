package org.wizmogaming.nerdcrusher.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener
{	
	@EventHandler
	public void adminJoin(PlayerJoinEvent evt)
	{
		if(evt.getPlayer().hasPermission("wizmogaming.isadmin"))
		{
			Bukkit.dispatchCommand(evt.getPlayer(), "protectlist");
		}
	}
}
