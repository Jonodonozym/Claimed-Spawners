
package jdz.claimedSpawners.listeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.event.FactionDisbandEvent;

import jdz.claimedSpawners.data.ClaimedSpawner;
import jdz.claimedSpawners.data.SpawnerManager;

public class FactionDisbandListener implements Listener{
	
	@EventHandler
	public void onDisband(FactionDisbandEvent e) {
		Set<ClaimedSpawner> spawners = SpawnerManager.getInstance().getByFaction(e.getFaction());
		for (ClaimedSpawner spawner: spawners)
			spawner.getLocation().getBlock().setType(Material.AIR);
		
		SpawnerManager.getInstance().clearFaction(e.getFaction());
	}

}
