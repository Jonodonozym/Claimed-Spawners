
package jdz.claimedSpawners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.claimedSpawners.data.SpawnerManager;
import jdz.claimedSpawners.hooks.EpicSpawnersHook;
import jdz.claimedSpawners.listeners.FactionChunksChangeListener;
import jdz.claimedSpawners.listeners.FactionDisbandListener;
import jdz.claimedSpawners.listeners.SpawnerPlaceListener;
import jdz.claimedSpawners.listeners.SpawnerRemoveListener;

public class ClaimedSpawners extends JavaPlugin {
	public static FileLogger spawnerPlaceLog;
	public static FileLogger spawnerRemoveLog;
	public static ClaimedSpawners instance;
	public static boolean epicSpawnersHooked = false;
	
	@Override
	public void onEnable() {
		spawnerRemoveLog = new FileLogger(this, "RemovedSpawners");
		instance = this;
		
		SpawnerManager.getInstance();
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new FactionChunksChangeListener(), this);
		pm.registerEvents(new FactionDisbandListener(), this);
		pm.registerEvents(SpawnerPlaceListener.getInstance(), this);
		pm.registerEvents(SpawnerRemoveListener.getInstance(), this);
		
		if (pm.isPluginEnabled("EpicSpawners")) {
			pm.registerEvents(EpicSpawnersHook.getInstance(), this);
			epicSpawnersHooked = true;
		}
	}
}
