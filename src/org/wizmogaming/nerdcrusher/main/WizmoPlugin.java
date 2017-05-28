package org.wizmogaming.nerdcrusher.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.wizmogaming.nerdcrusher.commands.IsAdmin;
import org.wizmogaming.nerdcrusher.commands.Trusted;
import org.wizmogaming.nerdcrusher.events.PlayerJoin;
import org.wizmogaming.nerdcrusher.events.PlayerProtectAsk;
import org.wizmogaming.nerdcrusher.util.PlayerLocations;

import net.md_5.bungee.api.ChatColor;

public class WizmoPlugin extends JavaPlugin
{
	public ItemStack Protect;
	public LinkedList<PlayerLocations> Protection = new LinkedList<PlayerLocations>();
	
	public void onEnable()
	{
		Protect = new ItemStack(Material.WOOD_SWORD, 1);
		ItemMeta im = Protect.getItemMeta();
		im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Protect");
		Protect.setItemMeta(im);
		
		registerCommands();
		registerEvents();
	}
	
	public void onDisable()
	{
		save(Protection, new File(getDataFolder().getAbsolutePath() + "/protection.txt"));
	}
	
	@SuppressWarnings("unchecked")
	public void registerCommands()
	{
		if(open(new File(getDataFolder().getAbsolutePath() + "/protection.txt")) != null)
			Protection = (LinkedList<PlayerLocations>) open(new File(getDataFolder().getAbsolutePath() + "/protection.txt"));
		else
			Protection = new LinkedList<PlayerLocations>();
		this.getCommand("protect").setExecutor(new PlayerProtectAsk(this));
		this.getCommand("removeask").setExecutor(new PlayerProtectAsk(this));
		this.getCommand("protectlist").setExecutor(new PlayerProtectAsk(this));
		this.getCommand("wizmodeletefile").setExecutor(new PlayerProtectAsk(this));
		this.getCommand("isadmin").setExecutor(new IsAdmin());
		this.getCommand("trusted").setExecutor(new Trusted());
	}
	
	public void registerEvents()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerProtectAsk(this), this);
		pm.registerEvents(new PlayerJoin(), this);
	}
	
	public Plugin getInstance()
	{
		return this;
	}
	
	public void save(Object o, File f)
	{
		try
		{
			if (!f.getParentFile().exists())
			    f.getParentFile().mkdirs();
			if(!f.exists())
				f.createNewFile();
			
			FileOutputStream saver = new FileOutputStream(f);
			ObjectOutputStream objSave = new ObjectOutputStream(saver);
			objSave.writeObject(o);
			objSave.close();
			objSave.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Object open(File f)
	{
		try
		{
			FileInputStream opener = new FileInputStream(f);
			ObjectInputStream objOpen = new ObjectInputStream(opener);
			Object obj = objOpen.readObject();
			objOpen.close();
			return obj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveStr(String s, File f)
	{
		BufferedWriter bw = null;
		FileWriter fw = null;
		
		try
		{
			if (!f.getParentFile().exists())
			    f.getParentFile().mkdirs();
			if(!f.exists())
				f.createNewFile();
			
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			bw.write(s);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(bw != null)
					bw.close();
				if(fw != null)
					fw.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String openStr(File f)
	{
		String p = new String();
		return p;
	}
	
	public LinkedList<OfflinePlayer> getOfflinePlayers()
	{
		LinkedList<OfflinePlayer> n = new LinkedList<OfflinePlayer>();
		for(Player a : getServer().getOnlinePlayers())
			n.add(a);
		for(OfflinePlayer a : getServer().getOfflinePlayers())
			if(!n.contains(Bukkit.getOfflinePlayer(a.getUniqueId())))
				n.add(Bukkit.getOfflinePlayer(a.getUniqueId()));
		return n;
	}
}
