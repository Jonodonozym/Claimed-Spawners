
package jdz.claimedSpawners.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.bukkitUtils.misc.WorldUtils;
import jdz.claimedSpawners.data.SpawnerDatabase;
import me.markeh.factionsframework.entities.FPlayer;
import me.markeh.factionsframework.entities.FPlayers;
import me.markeh.factionsframework.entities.Faction;
import me.markeh.factionsframework.entities.Factions;

public class SpawnerPlaceListener implements Listener {
	private final FileLogger spawnerPlaceLog;
	
	public SpawnerPlaceListener(JavaPlugin plugin) {
		spawnerPlaceLog = new FileLogger(plugin, "PlacedSpawners");
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onSpawnerPlace(BlockPlaceEvent e) {
		
		if (e.getBlock().getType() != Material.MOB_SPAWNER) return;
		
		if (!e.getPlayer().hasPermission("claimedSpawners.bypass")) {
		
			FPlayer player = FPlayers.getBySender(e.getPlayer());
			Faction chunkFaction = Factions.getFactionAt(e.getBlock().getChunk());
			
			if (player.getFaction().isNone()) {
				e.getPlayer().sendMessage(ChatColor.RED+"You need a faction to place spawners");
				e.setCancelled(true);
				return;
			}
				
			if (!player.getFaction().equals(chunkFaction)) {
				e.getPlayer().sendMessage(ChatColor.RED+"You can only place spawners in your faction's claimed land!");
				e.setCancelled(true);
				return;
			}
		}
			
		logPlacement(e.getPlayer(), e.getBlock());
		SpawnerDatabase.getInstance().addSpawner(e.getBlock().getLocation());
	}
	
	private void logPlacement(Player player, Block block) {
		CreatureSpawner cs = (CreatureSpawner) block.getState();
		
		String playerName = player.getName();
		String location = WorldUtils.locationToString(block.getLocation());
		String entityName = cs.getSpawnedType().name().toLowerCase().replaceAll("_", " ");
		
		spawnerPlaceLog.log(playerName+" placed a"+(StringUtils.isVowel(entityName.charAt(0))?"":"n")+" "+entityName+" spawner at "+location);
	}
}
