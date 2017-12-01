package jdz.claimedSpawners.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;

import jdz.bukkitUtils.misc.WorldUtils;
import jdz.claimedSpawners.data.SpawnerDatabase;

public class FactionChunksChangeListener implements Listener{

	
	@EventHandler
	public void onLandUnclaim(LandUnclaimEvent e) {
		List<Block> spawners = SpawnerDatabase.getInstance().getSpawnersIn(e.getLocation().getX(), e.getLocation().getZ());
		if (!spawners.isEmpty()) {
			Player player = e.getfPlayer().getPlayer();
			player.sendMessage(ChatColor.RED+"Cannot unclaim land while there are spawners in it!");
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onLandUnclaimAll(LandUnclaimAllEvent e) {
		List<Block> spawners = SpawnerDatabase.getInstance().getSpawners(e.getFaction().getId());
		if (!spawners.isEmpty()) {
			Player player = e.getfPlayer().getPlayer();
			player.sendMessage(ChatColor.RED+"Cannot unclaim land while there are spawners in it!");
			for (int i=0; i<Math.min(spawners.size(), 5); i++)
				player.sendMessage(ChatColor.RED+"Spawner at "+WorldUtils.locationToString(spawners.get(i).getLocation()));
			if (spawners.size() > 5)
				player.sendMessage(ChatColor.RED+"And "+(spawners.size()-5)+" more...");
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onLandClaim(LandClaimEvent e) {
		SpawnerDatabase.getInstance().setChunkFaction(e.getFaction().getId(), e.getLocation().getX(), e.getLocation().getZ());
	}
}
