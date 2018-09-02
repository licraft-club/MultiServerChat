package com.gmail.shellljx.MultiServerChat;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiServerChat extends JavaPlugin {
	
	//private ConfigUtil configUtil = new ConfigUtil(this);
	private PluginManager pluginManager;
	private Logger log = Logger.getLogger("MultiServerChat");
	private FileConfiguration config;
	private SocketUtil socketUtil = new SocketUtil(this);
	
	@Override
	public void onEnable() {
		config = getConfig();
		if(!new File(this.getDataFolder()+File.separator+"config.yml").exists()){
			config.createSection("setting.Port");
			config.options().copyDefaults(true);
			this.saveConfig();
		}
		if(socketUtil.startServer(config.getInt("setting.Port"))){
			pluginManager = Bukkit.getServer().getPluginManager();
			log.info("=====================================================");
			log.info(ChatColor.translateAlternateColorCodes('&',"&5auther shell"));
			log.info("======================================================");
		}
	}

	public static byte[] decode(String str){
		byte[] bt = null;
		try {
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			bt = decoder.decodeBuffer( str );
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bt;
	}

	@Override
	public void onDisable() {
		reloadConfig();
		saveConfig();
		socketUtil.closeServer();
	}
}
