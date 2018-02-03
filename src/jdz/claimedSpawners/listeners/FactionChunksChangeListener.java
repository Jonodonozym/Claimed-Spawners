package jdz.claimedSpawners.listeners;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import jdz.claimedSpawners.data.ClaimedSpawner;
import jdz.claimedSpawners.data.SpawnerManager;
import net.redstoneore.legacyfactions.FLocation;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.event.EventFactionsLandChange;

public class FactionChunksChangeListener implements Listener{


	// unclaim
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLandUnclaim(EventFactionsLandChange e) {
		if (!e.getTransactions().containsKey(FactionColl.get().getWilderness()))
			return;
		
		for (FLocation location: e.getTransactions().keySet()) {
			if (!e.getTransactions().get(location).equals(FactionColl.get().getWilderness()))
				continue;
			
			Set<ClaimedSpawner> spawners = SpawnerManager.getInstance().getSpawnersIn(location.getChunk().getX(), location.getChunk().getZ());
			if (!spawners.isEmpty()) {
				Player player = e.getFPlayer().getPlayer();
				player.sendMessage(ChatColor.RED+"Cannot unclaim chunk "+location.getChunk().getX()+","+location.getChunk().getZ()+" while there are spawners in it!");
				e.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLandClaim(EventFactionsLandChange e) {
		for (FLocation l: e.getTransactions().keySet())
			SpawnerManager.getInstance().setChunkFaction(e.getTransactions().get(l), l.getChunk().getX(), l.getChunk().getZ());
	}
}
