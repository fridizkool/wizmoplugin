package org.wizmogaming.nerdcrusher.events;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.wizmogaming.nerdcrusher.main.WizmoPlugin;
import org.wizmogaming.nerdcrusher.util.LocSer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerProtectAsk implements Listener, CommandExecutor
{
	private WizmoPlugin pl;
	
	public PlayerProtectAsk(WizmoPlugin p)
	{
		pl = p;
	}
	
	@EventHandler
	public void onPlayerProtect(PlayerInteractEvent evt)
	{
		
		if(evt.getItem() != null && evt.getItem().equals(pl.Protect))
		{
			Player p = evt.getPlayer();
			UUID u = p.getUniqueId();	//Easy coverage
			Location lo = null;
			String[] str = null;
			
			if(pl.ProtectionNeed.get(u) == null)	//Set protection points
				 str = new String[]{null, null};
			else
				str = new String[]{pl.ProtectionNeed.get(u)[0], pl.ProtectionNeed.get(u)[1]};
			
			if(evt.getAction() == Action.LEFT_CLICK_BLOCK && !p.isSneaking())	//Left click to set first position
			{
				lo = evt.getClickedBlock().getLocation();
				p.sendMessage(ChatColor.YELLOW + "Protection spot 1 in " + lo.getWorld().getName() + "\nX: " + lo.getX() + ", Y: " + lo.getY() + ", Z: " + lo.getZ());
				str[0] = LocSer.getString(lo);
			}
			else if(evt.getAction() == Action.RIGHT_CLICK_BLOCK && !p.isSneaking())	//Right click to set second position
			{
				lo = evt.getClickedBlock().getLocation();
				p.sendMessage(ChatColor.YELLOW + "Protection spot 2 in " + lo.getWorld().getName() + "\nX: " + lo.getX() + ", Y: " + lo.getY() + ", Z: " + lo.getZ());
				str[1] = LocSer.getString(lo);
			}
			else if((evt.getAction() == Action.RIGHT_CLICK_BLOCK && p.isSneaking()) || (evt.getAction() == Action.RIGHT_CLICK_AIR && p.isSneaking())) //Sneak right click to clear
			{
				if(str[0] == null || str[1] == null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Could not remove your protection ask because you have not completed your protection area");
				else
				{
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Removed your protection area");
					p.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "Protection spot 1 in " + LocSer.getLocation(str[0]).getWorld().getName() + "\nX: "
							+ LocSer.getLocation(str[0]).getX() + ", Y: " + LocSer.getLocation(str[0]).getY() + ", Z: "
								+ LocSer.getLocation(str[0]).getZ());
					p.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "Protection spot 2 in " + LocSer.getLocation(str[1]).getWorld().getName() + "\nX: "
							+ LocSer.getLocation(str[1]).getX() + ", Y: " + LocSer.getLocation(str[1]).getY() + ", Z: "
								+ LocSer.getLocation(str[1]).getZ());
					str[0] = null;
					str[1] = null;
					str = null;
				}
			}
			else if((evt.getAction() == Action.LEFT_CLICK_BLOCK && p.isSneaking()) || (evt.getAction() == Action.LEFT_CLICK_AIR && p.isSneaking())) //Sneak left click to try to send to admins
			{
				if(str[0] == null && str[1] != null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Cannot send your protection ask to admin because you have not completed because protection 1 has not been set!");
				else if(str[1] == null && str[0] != null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Cannot send your protection ask to admin because you have not completed because protection 2 has not been set!");
				else if(str[0] == null && str[1] == null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Cannot send your protection ask to admin because you have not completed because protection 1 and 2 have not been set!");
				else
				{
					int length = Math.abs(LocSer.getLocation(str[0]).getBlockX() - LocSer.getLocation(str[1]).getBlockX());
					int width = Math.abs(LocSer.getLocation(str[0]).getBlockZ() - LocSer.getLocation(str[1]).getBlockZ());
					if(length * width < pl.getConfig().getInt("MaxProtectionArea"))
					{
						p.sendMessage(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Protection ask sent to admins");
						ArrayList<Player> ad = new ArrayList<Player>();
						Player[] players = new Player[pl.getServer().getOnlinePlayers().size()];
						players = pl.getServer().getOnlinePlayers().toArray(players);
						for(Player c : players)
						{
							if(c.hasPermission("wizmogaming.isadmin"))
								ad.add(c);
							pl.save(pl.ProtectionNeed, new File(pl.getDataFolder().getAbsolutePath() + "/protectionask.txt"));
						}
						notifyAdmins(ad, u);
					}
					else
						p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "To large of an area selected");
				}
			}
			
			pl.ProtectionNeed.put(u, str);	
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		LinkedList<OfflinePlayer> a = pl.getOfflinePlayers();
		ArrayList<UUID> need = new ArrayList<UUID>();
		for(OfflinePlayer x : a)
			if(x.getUniqueId() != null)
				if(pl.ProtectionNeed.get(x.getUniqueId()) != null)
					need.add(x.getUniqueId());
		
		if(cmd.getName().equalsIgnoreCase("protect"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				
				if(args.length == 1 && args[0].equalsIgnoreCase("help"))
				{
					TextComponent mes = new TextComponent("Perform \"/protect\" and you will get a wood sword, it will protect your builds!"
							+ " Protect by selecting two corners to engulf your build, Y level does not matter! Left click for corner 1, right click for corner 2, sneak right click to clear protection,"
								+ " sneak left click to finalize! Note: can only have one selection at a time");
					mes.setColor(ChatColor.GREEN);
					player.spigot().sendMessage(mes);
				}
				else
					player.getInventory().addItem(pl.Protect);
				return true;
			}
			else
				sender.sendMessage("Can only be ran by player");
		}
		if(cmd.getName().equalsIgnoreCase("removeask"))
		{
			if(args.length == 2)
			{
				if(args[0].equalsIgnoreCase("accept"))
				{
					if(Bukkit.getOfflinePlayer(args[1]) != null)
					{
						OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
						if(pl.ProtectionNeed.get(player.getUniqueId()) != null)
						{
							Location[] lo = new Location[]{LocSer.getLocation(pl.ProtectionNeed.get(player.getUniqueId())[0]), LocSer.getLocation(pl.ProtectionNeed.get(player.getUniqueId())[1])};
							pl.ProtectionNeed.remove(player.getUniqueId());
							if(pl.ProtectedAmount.get(player.getUniqueId()) == null)
								pl.ProtectedAmount.put(player.getUniqueId(), 0);
							pl.ProtectedAmount.put(player.getUniqueId(), pl.ProtectedAmount.get(player.getUniqueId()) + 1);
							Bukkit.dispatchCommand(sender, "/pos1 " + lo[0].getBlockX() + "," + lo[0].getBlockY() + "," + lo[0].getBlockZ());
							Bukkit.dispatchCommand(sender, "/pos2 " + lo[1].getBlockX() + "," + lo[1].getBlockY() + "," + lo[1].getBlockZ());
							Bukkit.dispatchCommand(sender, "/expand vert");
							Bukkit.dispatchCommand(sender, "region define " + player.getName() + "SELECTION" + pl.ProtectedAmount.get(player.getUniqueId()) + " " + player.getName());
							pl.save(pl.ProtectionNeed, new File(pl.getDataFolder().getAbsolutePath() + "/protectionask.txt"));
							pl.save(pl.ProtectedAmount, new File(pl.getDataFolder().getAbsolutePath() + "/protectedamount.txt"));
							sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Created protection " + player.getName() + "SELECTION" + pl.ProtectedAmount.get(player.getUniqueId()) + " " + player.getName());
						}
						else
							sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Protection was already created!");
						return true;
					}
					else
						sender.sendMessage(ChatColor.RED + "Player does not exist");
				}
				else if(args[0].equalsIgnoreCase("deny"))
				{
					if(Bukkit.getOfflinePlayer(args[1]) != null)
					{
						OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
						if(pl.ProtectionNeed.get(player.getUniqueId()) != null)
						{
							pl.ProtectionNeed.remove(player.getUniqueId());
							pl.save(pl.ProtectionNeed, new File(pl.getDataFolder().getAbsolutePath() + "/protectionask.txt"));
							pl.save(pl.ProtectedAmount, new File(pl.getDataFolder().getAbsolutePath() + "/protectedamount.txt"));
							sender.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "Did not create the protection for " + player.getName());
						}
						else
							sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Protection was already created!");
						return true;
					}
					else
						sender.sendMessage(ChatColor.RED + "Player does not exist");
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("protectlist"))
		{
			if(args.length == 1)
			{
				if(sender instanceof Player)
				{
					if(Bukkit.getOfflinePlayer(args[0]) != null && pl.ProtectionNeed.get(Bukkit.getOfflinePlayer(args[0]).getUniqueId()) != null)
					{
						ArrayList<Player> ad = new ArrayList<Player>();
						ad.add((Player)sender);
						notifyAdmins(ad, Bukkit.getOfflinePlayer(args[0]).getUniqueId());
						return true;
					}
					else
						sender.sendMessage(ChatColor.RED + "Player does not exist");
				}
				else 
					sender.sendMessage("Can only be ran by player");
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "There are " + need.size() + " players that still need a protection:");
				if(sender instanceof Player)
				{
					TextComponent mes = new TextComponent();
					for(UUID x : need)
						if(pl.ProtectionNeed.get(x) != null)
						{
							mes = new TextComponent();
							mes.setColor(ChatColor.GREEN);
							mes.setItalic(true);
							mes.setText(Bukkit.getOfflinePlayer(x).getName());
							mes.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/protectlist " + Bukkit.getOfflinePlayer(x).getName()));
							((Player)sender).spigot().sendMessage(mes);
						}
				}
				else
					for(UUID x : need)
						sender.sendMessage(ChatColor.GREEN + Bukkit.getOfflinePlayer(x).getName());
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("wizmodeletefile"))
		{
			if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("protectionask"))
				{
					pl.ProtectionNeed = new HashMap<UUID, String[]>();
					pl.save(pl.ProtectionNeed, new File(pl.getDataFolder().getAbsolutePath() + "/protectionask.txt"));
					return true;
				}
				else if(args[0].equalsIgnoreCase("ProtectedAmount"))
				{
					pl.ProtectedAmount = new HashMap<UUID, Integer>();
					pl.save(pl.ProtectedAmount, new File(pl.getDataFolder().getAbsolutePath() + "/protectedamount.txt"));
					return true;
				}
			}
		}
		if(cmd.getName().equalsIgnoreCase("protectedamount"))
		{
			if(args.length > 0)
			{
				if(args[0].equalsIgnoreCase("set"))
				{
					if(args.length == 3)
					{
						if(Bukkit.getOfflinePlayer(args[1]) != null)
						{
							if(args[2].matches("^[+-]?\\d+$"))
							{
								pl.ProtectedAmount.put(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), Integer.getInteger(args[2]));
								return true;
							}
						}
						else
							sender.sendMessage(ChatColor.RED + "Does not exist");
					}
				}
				else if(args[1].equalsIgnoreCase("get"))
				{
					if(args.length == 2)
					{
						if(Bukkit.getOfflinePlayer(args[1]) != null)
						{
							sender.sendMessage(ChatColor.GREEN + "" + pl.ProtectedAmount.get(Bukkit.getOfflinePlayer(args[1])));
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings({ "deprecation" })
	public void notifyAdmins(ArrayList<Player> ad, UUID u)
	{
		Location[] lo = new Location[]{LocSer.getLocation(pl.ProtectionNeed.get(u)[0]), LocSer.getLocation(pl.ProtectionNeed.get(u)[1])};
		OfflinePlayer player = Bukkit.getOfflinePlayer(u);
		if(pl.ProtectedAmount.get(u) == null)
			pl.ProtectedAmount.put(u, 0);
		
		//Start
		TextComponent mes = new TextComponent(player.getName() + " needs a protection at:");
		mes.setBold(true);
		mes.setColor(ChatColor.DARK_PURPLE);
		//Inspection
		mes.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/staffchat Inspecting " + player.getName() + " selection"));
		TextComponent mes1 = new TextComponent("Teleport to corner 1\nWorld: " + lo[0].getWorld().getName() + "\nX: " + lo[0].getBlockX() + ", Y: " + lo[0].getBlockY() + ", Z: " + lo[0].getBlockZ());
		mes1.setItalic(true);
		mes1.setColor(ChatColor.BLUE);
		mes1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tppos " + lo[0].getBlockX() + " " + lo[0].getBlockY() + " " + lo[0].getBlockZ()));
		TextComponent mes2 = new TextComponent("Teleport to corner 2\nWorld: " + lo[1].getWorld().getName() + "\nX: " + lo[1].getBlockX() + ", Y: " + lo[1].getBlockY() + ", Z: " + lo[1].getBlockZ());
		mes2.setItalic(true);
		mes2.setColor(ChatColor.RED);
		mes2.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tppos " + lo[1].getBlockX() + " " + lo[1].getBlockY() + " " + lo[1].getBlockZ()));
		TextComponent mes3 = new TextComponent("Accept this protection");
		mes3.setBold(true);
		mes3.setColor(ChatColor.AQUA);
		mes3.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/removeask accept " + player.getName()));
		mes3.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Create protection").create()));
		TextComponent mes4 = new TextComponent("Reject this protection");
		mes4.setBold(true);
		mes4.setColor(ChatColor.DARK_RED);
		mes4.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/removeask deny " + player.getName()));
		mes4.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Do not create protection/remove from queue").create()));
		
		
		for(Player a : ad)
		{
			a.sendBlockChange(lo[0], Material.GLOWSTONE, (byte)0);
			a.sendBlockChange(lo[1], Material.REDSTONE_LAMP_ON, (byte)0);
			a.spigot().sendMessage(mes);
			a.spigot().sendMessage(mes1);
			a.spigot().sendMessage(mes2);
			a.spigot().sendMessage(mes3);
			a.spigot().sendMessage(mes4);
		}
	}
}
