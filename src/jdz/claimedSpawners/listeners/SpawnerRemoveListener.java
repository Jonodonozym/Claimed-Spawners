
package jdz.claimedSpawners.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.bukkitUtils.misc.WorldUtils;
import jdz.claimedSpawners.data.SpawnerDatabase;

public class SpawnerRemoveListener implements Listener {
	private final Map<Player, Integer> tntRecentlyPlaced = new HashMap<Player, Integer>();
	private final Map<Player, Integer> ceggRecentlyPlaced = new HashMap<Player, Integer>();

	private final FileLogger spawnerBreakLog;
	private final JavaPlugin plugin;

	public SpawnerRemoveListener(JavaPlugin plugin) {
		this.plugin = plugin;
		spawnerBreakLog = new FileLogger(plugin, "BrokenSpawners");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpawnerBreak(BlockBreakEvent e) {

		if (e.getBlock().getType() != Material.MOB_SPAWNER)
			return;
		
		SpawnerDatabase.getInstance().removeSpawner(e.getBlock().getLocation());
		
		logBreaking(e.getBlock());
	}

	private void logBreaking(Block block) {
		CreatureSpawner cs = (CreatureSpawner) block.getState();
		
		String location = WorldUtils.locationToString(block.getLocation());
		String entityName = cs.getSpawnedType().name().toLowerCase().replaceAll("_", " ");
		
		String logString = "A"+(StringUtils.isVowel(entityName.charAt(0))?"":"n")+" "+entityName+" spawner at "+location+" Was broken";
		
		Set<Player> nearbyPlayers = WorldUtils.getNearbyPlayers(block.getLocation(), 256);
		if (!nearbyPlayers.isEmpty()) {
			logString += "\nNearby Players:";
			for (Player player: nearbyPlayers) {
				
				String extData = " (";
				
				if (tntRecentlyPlaced.containsKey(player)) {
					int tntPlaced = tntRecentlyPlaced.get(player);
					if (tntPlaced > 0)
						extData += " x"+tntPlaced+" TNT placed";
				}
				
				if (ceggRecentlyPlaced.containsKey(player)) {
					int ceegPlaced = ceggRecentlyPlaced.get(player);
						if (ceegPlaced > 0) {
							if (!extData.equals(" ("))
								extData += " and ";
							extData += " x"+ceegPlaced+" Creeper eggs used";
						}
				}
				
				extData = extData.equals(" (")?"":extData + " in the last 60 seconds)";
				
				logString += "\n - "+player.getName()+": "+WorldUtils.locationToString(player.getLocation())+extData;
			}
		}
		
		spawnerBreakLog.log(logString);
	}

	
	@EventHandler
	public void onTNTPlace(BlockPlaceEvent e) {	
		if (e.getBlock().getType() != Material.TNT) return;
		
		Player player = e.getPlayer();
		
		if (!tntRecentlyPlaced.containsKey(player))
			tntRecentlyPlaced.put(player, 0);
		tntRecentlyPlaced.put(player, tntRecentlyPlaced.get(player)+1);
		
		Bukkit.getScheduler().runTaskLater(plugin, ()->{
			tntRecentlyPlaced.put(player, tntRecentlyPlaced.get(player)-1);
		}, 1200);
	}
	
	@EventHandler
    public void onCeggUse(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getMaterial() == Material.MONSTER_EGG &&
                e.getItem().getDurability() == 50) {

    		Player player = e.getPlayer();
    		
    		if (!ceggRecentlyPlaced.containsKey(player))
    			ceggRecentlyPlaced.put(player, 0);
    		ceggRecentlyPlaced.put(player, ceggRecentlyPlaced.get(player)+1);
    		
    		Bukkit.getScheduler().runTaskLater(plugin, ()->{
    			ceggRecentlyPlaced.put(player, ceggRecentlyPlaced.get(player)-1);
    		}, 1200);
        }
    }
}
