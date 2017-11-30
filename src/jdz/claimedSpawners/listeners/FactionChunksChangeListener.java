package jdz.claimedSpawners.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import jdz.claimedSpawners.data.SpawnerDatabase;
import me.markeh.factionsframework.event.EventFactionsChunksChange;

public class FactionChunksChangeListener implements Listener{
	
	@EventHandler
	public void onLandClaim(EventFactionsChunksChange e) {
		for (Chunk c: e.getChunks()) {
			List<Block> spawners = SpawnerDatabase.getInstance().getSpawnersIn(c);
			if (e.getNewFaction().isNone() && !spawners.isEmpty()) {
				Player player = e.getFPlayer().asBukkitPlayer();
				player.sendMessage(ChatColor.RED+"Cannot unclaim land while there are spawners in it!");
				
				e.setCancelled(true);
				return;
			}
		}
		
		for (Chunk c: e.getChunks())
			SpawnerDatabase.getInstance().setChunkFaction(c, e.getNewFaction());
	}
}
