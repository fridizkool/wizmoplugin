package org.wizmogaming.nerdcrusher.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class IsAdmin implements CommandExecutor
{
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(sender.hasPermission("wizmogaming.isadmin"))
		{
			sender.sendMessage("you admin");
			return true;
		}
		return false;
	}
}
