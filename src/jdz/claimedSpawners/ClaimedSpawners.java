
package jdz.claimedSpawners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.claimedSpawners.data.SpawnerDatabase;
import jdz.claimedSpawners.listeners.FactionChunksChangeListener;
import jdz.claimedSpawners.listeners.FactionDisbandListener;
import jdz.claimedSpawners.listeners.SpawnerPlaceListener;
import jdz.claimedSpawners.listeners.SpawnerRemoveListener;

public class ClaimedSpawners extends JavaPlugin {
	public static FileLogger spawnerPlaceLog;
	public static FileLogger spawnerRemoveLog;
	
	@Override
	public void onEnable() {
		spawnerRemoveLog = new FileLogger(this, "RemovedSpawners");
		
		SpawnerDatabase.init(this);
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new FactionChunksChangeListener(), this);
		pm.registerEvents(new FactionDisbandListener(), this);
		pm.registerEvents(new SpawnerPlaceListener(this), this);
		pm.registerEvents(new SpawnerRemoveListener(this), this);
	}
}
