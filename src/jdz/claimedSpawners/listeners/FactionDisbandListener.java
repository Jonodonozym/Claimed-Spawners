
package jdz.claimedSpawners.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import jdz.claimedSpawners.data.SpawnerDatabase;
import me.markeh.factionsframework.event.EventFactionsDisband;

public class FactionDisbandListener implements Listener{
	
	@EventHandler
	public void onDisband(EventFactionsDisband e) {
		List<Block> spawners = SpawnerDatabase.getInstance().getSpawners(e.getFaction());
		for (Block spawner: spawners)
			spawner.setType(Material.AIR);
		
		SpawnerDatabase.getInstance().clearSpawnerData(e.getFaction());
	}

}
