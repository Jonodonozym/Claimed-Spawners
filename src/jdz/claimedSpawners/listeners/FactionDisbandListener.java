
package jdz.claimedSpawners.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.event.FactionDisbandEvent;

import jdz.claimedSpawners.data.SpawnerDatabase;

public class FactionDisbandListener implements Listener{
	
	@EventHandler
	public void onDisband(FactionDisbandEvent e) {
		List<Block> spawners = SpawnerDatabase.getInstance().getSpawners(e.getFaction().getId());
		for (Block spawner: spawners)
			spawner.setType(Material.AIR);
		
		SpawnerDatabase.getInstance().clearSpawnerData(e.getFaction().getId());
	}

}
