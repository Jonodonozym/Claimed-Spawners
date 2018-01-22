package jdz.claimedSpawners.listeners;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;

import jdz.bukkitUtils.misc.WorldUtils;
import jdz.claimedSpawners.data.ClaimedSpawner;
import jdz.claimedSpawners.data.SpawnerManager;

public class FactionChunksChangeListener implements Listener{

	
	@EventHandler
	public void onLandUnclaim(LandUnclaimEvent e) {
		Set<ClaimedSpawner> spawners = SpawnerManager.getInstance().getSpawnersIn(e.getLocation().getX(), e.getLocation().getZ());
		if (!spawners.isEmpty()) {
			Player player = e.getfPlayer().getPlayer();
			player.sendMessage(ChatColor.RED+"Cannot unclaim land while there are spawners in it!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLandUnclaimAll(LandUnclaimAllEvent e) {
		Set<ClaimedSpawner> spawners = SpawnerManager.getInstance().getByFaction(e.getFaction());
		if (!spawners.isEmpty()) {
			Player player = e.getfPlayer().getPlayer();
			player.sendMessage(ChatColor.RED+"Cannot unclaim land while there are spawners in it!");
			
			Iterator<ClaimedSpawner> iterator = spawners.iterator();
			for (int i=0; i<Math.min(spawners.size(), 5); i++)
				player.sendMessage(ChatColor.RED+"Spawner at "+WorldUtils.locationToString(iterator.next().getLocation()));
			
			if (spawners.size() > 5)
				player.sendMessage(ChatColor.RED+"And "+(spawners.size()-5)+" more...");
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onLandClaim(LandClaimEvent e) {
		SpawnerManager.getInstance().setChunkFaction(e.getFaction(), e.getLocation().getX(), e.getLocation().getZ());
	}
}
