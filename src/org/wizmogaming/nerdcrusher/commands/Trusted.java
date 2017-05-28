package org.wizmogaming.nerdcrusher.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Trusted implements CommandExecutor
{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("trusted") && args.length == 1)
		{	
			if(Bukkit.getPlayer(args[0]) != null)
			{
				TextComponent message = new TextComponent("Get trusted by signing up here!");
				message.setBold(true);
				message.setColor(ChatColor.GREEN);
				message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://bit.ly/1ljOeb9"));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join our website, then link you minecraft account to your website account!").create()));
				Bukkit.getPlayer(args[0]).spigot().sendMessage(message);
				return true;
			}
			else
				sender.sendMessage("Player does not exist!");
		}
		else
			sender.sendMessage("/trusted <player>");
		return false;
	}
	
}
