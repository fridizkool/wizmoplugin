package org.wizmogaming.nerdcrusher.events;

import java.io.File;
import java.util.ArrayList;
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
import org.wizmogaming.nerdcrusher.util.LocationArea;
import org.wizmogaming.nerdcrusher.util.PlayerLocations;

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
			PlayerLocations playloc;
			LocationArea locarea;
			int pos = check(u, 0);
			
			pl.Protection.get(pos).add(new LocationArea(null, null));
			playloc = pl.Protection.get(pos);
			locarea = playloc.get();
			
			if(evt.getAction() == Action.LEFT_CLICK_BLOCK && !p.isSneaking())	//Left click to set first position
			{
				lo = evt.getClickedBlock().getLocation();
				p.sendMessage(ChatColor.YELLOW + "Protection spot 1 in " + lo.getWorld().getName() + "\nX: " + lo.getX() + ", Y: " + lo.getY() + ", Z: " + lo.getZ());
				locarea.set1(lo);
			}
			else if(evt.getAction() == Action.RIGHT_CLICK_BLOCK && !p.isSneaking())	//Right click to set second position
			{
				lo = evt.getClickedBlock().getLocation();
				p.sendMessage(ChatColor.YELLOW + "Protection spot 2 in " + lo.getWorld().getName() + "\nX: " + lo.getX() + ", Y: " + lo.getY() + ", Z: " + lo.getZ());
				locarea.set2(lo);
			}
			else if((evt.getAction() == Action.RIGHT_CLICK_BLOCK && p.isSneaking()) || (evt.getAction() == Action.RIGHT_CLICK_AIR && p.isSneaking())) //Sneak right click to clear
			{
				if(locarea.get1() == null || locarea.get2() == null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Could not remove your protection ask because you have not completed your protection area");
				else
				{
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Removed your protection area");
					p.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + locarea);
					locarea.set1(null);
					locarea.set2(null);
					locarea = null;
				}
			}
			else if((evt.getAction() == Action.LEFT_CLICK_BLOCK && p.isSneaking()) || (evt.getAction() == Action.LEFT_CLICK_AIR && p.isSneaking())) //Sneak left click to try to send to admins
			{
				if(locarea.get1() == null && locarea.get2() != null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Cannot send your protection ask to admin because you have not completed because protection 1 has not been set!");
				else if(locarea.get2() == null && locarea.get1() != null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Cannot send your protection ask to admin because you have not completed because protection 2 has not been set!");
				else if(locarea.get1() == null && locarea.get2() == null)
					p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Cannot send your protection ask to admin because you have not completed because protection 1 and 2 have not been set!");
				else
				{
					int length = Math.abs(locarea.get1().getBlockX() - locarea.get2().getBlockX());
					int width = Math.abs(locarea.get1().getBlockZ() - locarea.get2().getBlockZ());
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
							pl.save(pl.Protection, new File(pl.getDataFolder().getAbsolutePath() + "/protectionask.txt"));
						}
						notifyAdmins(ad, u);
					}
					else
						p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "To large of an area selected");
				}
			}
			pos = check(u, pos);
			pl.Protection.get(pos).set(locarea);
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
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
						UUID u = player.getUniqueId();
						int pos = check(u, 0);
						if(pl.Protection.get(pos).get() != null)
						{
							PlayerLocations playloc = pl.Protection.get(pos);
							LocationArea locarea = playloc.get();
							Bukkit.dispatchCommand(sender, "/pos1 " + locarea.get1().getBlockX() + "," + locarea.get1().getBlockY() + "," + locarea.get1().getBlockZ());
							Bukkit.dispatchCommand(sender, "/pos2 " + locarea.get2().getBlockX() + "," + locarea.get2().getBlockY() + "," + locarea.get2().getBlockZ());
							Bukkit.dispatchCommand(sender, "/expand vert");
							Bukkit.dispatchCommand(sender, "region define " + player.getName() + "SELECTION" + playloc.Protections + " " + player.getName());
							pl.save(pl.Protection, new File(pl.getDataFolder().getAbsolutePath() + "/protectionask.txt"));
							sender.sendMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Created protection " + player.getName() + "SELECTION" + playloc.Protections + " " + player.getName());
							pl.Protection.get(pos).Protections++;
							pl.Protection.get(pos).set(null);
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
						UUID u = player.getUniqueId();
						int pos = check(u, 0);
						if(pl.Protection.get(pos).get() != null)
						{
							PlayerLocations playloc = pl.Protection.get(pos);
							pl.Protection.remove(playloc);
							pl.save(pl.Protection, new File(pl.getDataFolder().getAbsolutePath() + "/protectionask.txt"));
							sender.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "Did not create the protection for " + player.getName());
							playloc.set(null);
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
					if(Bukkit.getOfflinePlayer(args[0]) != null && pl.Protection.get(check(Bukkit.getOfflinePlayer(args[0]).getUniqueId(), 0)).get() != null)
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
				LinkedList<PlayerLocations> need = new LinkedList<PlayerLocations>();
				for(PlayerLocations p : pl.Protection)
					if(p.get() != null)
						need.add(p);
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "There are " + need.size() + " player(s) that still need(s) a protection:");
				if(sender instanceof Player)
				{
					TextComponent mes = new TextComponent();
					for(PlayerLocations x : need)
						if(x.get() != null)
						{
							mes = new TextComponent();
							mes.setColor(ChatColor.GREEN);
							mes.setItalic(true);
							mes.setText(x.getPlayer().getName());
							mes.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/protectlist " + x.getPlayer().getName()));
							((Player)sender).spigot().sendMessage(mes);
						}
				}
				else
					for(PlayerLocations x : need)
						if(x.get() != null)
							sender.sendMessage(ChatColor.GREEN + x.getPlayer().getName());
				return true;
			}
		}
		if(cmd.getName().equalsIgnoreCase("wizmodeletefile"))
		{
			if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("protection"))
				{
					pl.Protection = new LinkedList<PlayerLocations>();
					pl.save(pl.Protection, new File(pl.getDataFolder().getAbsolutePath() + "/protection.txt"));
					return true;
				}
			}
		}
		
		return false;
	}
	
	@SuppressWarnings({ "deprecation" })
	public void notifyAdmins(ArrayList<Player> ad, UUID u)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(u);
		int pos = check(u, 0);
		PlayerLocations playloc = pl.Protection.get(pos);
		LocationArea locarea = playloc.get();
		
		//Start
		TextComponent mes = new TextComponent(player.getName() + " needs a protection at:");
		mes.setBold(true);
		mes.setColor(ChatColor.DARK_PURPLE);
		//Inspection
		mes.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/staffchat Inspecting " + player.getName() + " selection"));
		TextComponent mes1 = new TextComponent("Teleport to corner 1\nWorld: " + locarea.get1().getWorld().getName() + "\nX: " + locarea.get1().getBlockX() + ", Y: " + locarea.get1().getBlockY() + ", Z: " + locarea.get1().getBlockZ());
		mes1.setItalic(true);
		mes1.setColor(ChatColor.BLUE);
		mes1.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tppos " + locarea.get1().getBlockX() + " " + locarea.get1().getBlockY() + " " + locarea.get1().getBlockZ()));
		TextComponent mes2 = new TextComponent("Teleport to corner 2\nWorld: " + locarea.get2().getWorld().getName() + "\nX: " + locarea.get2().getBlockX() + ", Y: " + locarea.get2().getBlockY() + ", Z: " + locarea.get2().getBlockZ());
		mes2.setItalic(true);
		mes2.setColor(ChatColor.RED);
		mes2.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tppos " + locarea.get2().getBlockX() + " " + locarea.get2().getBlockY() + " " + locarea.get2().getBlockZ()));
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
			a.sendBlockChange(locarea.get1(), Material.GLOWSTONE, (byte)0);
			a.sendBlockChange(locarea.get2(), Material.REDSTONE_LAMP_ON, (byte)0);
			a.spigot().sendMessage(mes);
			a.spigot().sendMessage(mes1);
			a.spigot().sendMessage(mes2);
			a.spigot().sendMessage(mes3);
			a.spigot().sendMessage(mes4);
		}
	}
	
	private int check(UUID a, int x)
	{
		if(a.equals(pl.Protection.get(x).getUUID()))
			return x;
		else
			for(int pos = 0; pos < pl.Protection.size(); x++)
				if(a.equals(pl.Protection.get(pos)))
					return pos;
		pl.Protection.add(new PlayerLocations(pl.getServer().getPlayer(a)));
		return pl.Protection.size();
	}
}
