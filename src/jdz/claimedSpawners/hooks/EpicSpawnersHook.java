
package jdz.claimedSpawners.hooks;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.songoda.epicspawners.Spawners.SpawnerChangeEvent;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.bukkitUtils.misc.WorldUtils;
import jdz.claimedSpawners.data.SpawnerManager;
import jdz.claimedSpawners.listeners.SpawnerPlaceListener;
import jdz.claimedSpawners.listeners.SpawnerRemoveListener;
import lombok.Getter;

public class EpicSpawnersHook implements Listener{
	@Getter private static final EpicSpawnersHook instance = new EpicSpawnersHook();
	
	private final FileLogger spawnerPlaceLog, spawnerBreakLog;
	
	public EpicSpawnersHook() {
		this.spawnerBreakLog = SpawnerRemoveListener.getInstance().getSpawnerBreakLog();
		this.spawnerPlaceLog = SpawnerPlaceListener.getInstance().getSpawnerPlaceLog();
	}
	
	@EventHandler
	public void onUpgrade(SpawnerChangeEvent event) {
		logUpgrade(event);
	}
	
	private void logUpgrade(SpawnerChangeEvent event) {
		Block block = event.getSpawner();
		
		CreatureSpawner cs = (CreatureSpawner) block.getState();
		
		String playerName = event.getPlayer().getName();
		String location = WorldUtils.locationToLegibleString(block.getLocation());
		String entityName = cs.getSpawnedType().name().toLowerCase().replaceAll("_", " ");
		
		spawnerPlaceLog.log(playerName+" upgraded a"+(StringUtils.isVowel(entityName.charAt(0))?"":"n")+" "+entityName+" spawner at "+location+" to level "+event.getCurrentMulti());
	}
	
	@EventHandler
	public void onDowngrade(SpawnerChangeEvent event) {
		if (event.getCurrentMulti() < event.getOldMulti()) {
			if (event.getCurrentMulti() == 0)
				SpawnerManager.getInstance().removeSpawner(event.getSpawner().getLocation());
			logDowngrade(event);
		}
	}
	
	private void logDowngrade(SpawnerChangeEvent event) {
		Block block = event.getSpawner();

		CreatureSpawner cs = (CreatureSpawner) block.getState();

		String playerName = event.getPlayer().getName();
		String location = WorldUtils.locationToLegibleString(block.getLocation());
		String entityName = cs.getSpawnedType().name().toLowerCase().replaceAll("_", " ");

		spawnerBreakLog.log(playerName + " downgraded a" + (StringUtils.isVowel(entityName.charAt(0)) ? "" : "n") + " "
				+ entityName + " spawner at " + location + " to level " + event.getCurrentMulti());
	}

}
