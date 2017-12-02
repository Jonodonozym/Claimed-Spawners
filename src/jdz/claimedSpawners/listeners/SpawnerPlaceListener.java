
package jdz.claimedSpawners.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.songoda.epicspawners.Spawners.SpawnerChangeEvent;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.bukkitUtils.misc.WorldUtils;
import jdz.claimedSpawners.ClaimedSpawners;
import jdz.claimedSpawners.data.SpawnerDatabase;

public class SpawnerPlaceListener implements Listener {
	private final FileLogger spawnerPlaceLog;
	
	public SpawnerPlaceListener(JavaPlugin plugin) {
		spawnerPlaceLog = new FileLogger(plugin, "PlacedSpawners");
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onSpawnerPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.MOB_SPAWNER) return;
		
		Location l = e.getBlock().getLocation();
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(ClaimedSpawners.instance, ()->{
			
			if (l.getWorld().getBlockAt(l).getType()  != Material.MOB_SPAWNER) return;
			
			FPlayer player = FPlayers.getInstance().getByPlayer(e.getPlayer()); 
			Faction chunkFaction = Board.getInstance().getFactionAt(new FLocation(e.getBlock()));
			
			if (!e.getPlayer().hasPermission("claimedSpawners.bypass")) {
				
				if (player.getFaction().isWilderness()) {
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
			SpawnerDatabase.getInstance().addSpawner(chunkFaction.getId(), e.getBlock().getLocation());
			
			
		}, 20);
	}
	
	@EventHandler
	public void onUpgrade(SpawnerChangeEvent e) {
		System.out.println(e.getOldMulti());
	}
	
	private void logPlacement(Player player, Block block) {
		CreatureSpawner cs = (CreatureSpawner) block.getState();
		
		String playerName = player.getName();
		String location = WorldUtils.locationToString(block.getLocation());
		String entityName = cs.getSpawnedType().name().toLowerCase().replaceAll("_", " ");
		
		spawnerPlaceLog.log(playerName+" placed a"+(StringUtils.isVowel(entityName.charAt(0))?"":"n")+" "+entityName+" spawner at "+location);
	}
	
}
