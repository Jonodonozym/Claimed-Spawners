
package jdz.claimedSpawners.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.bukkitUtils.misc.WorldUtils;
import jdz.claimedSpawners.ClaimedSpawners;
import jdz.claimedSpawners.data.SpawnerManager;
import lombok.Getter;
import net.redstoneore.legacyfactions.entity.Faction;

public class SpawnerRemoveListener implements Listener {
	@Getter private static final SpawnerRemoveListener instance = new SpawnerRemoveListener(ClaimedSpawners.instance);
	
	private final Map<Player, Integer> tntRecentlyPlaced = new HashMap<Player, Integer>();
	private final Map<Player, Integer> ceggRecentlyPlaced = new HashMap<Player, Integer>();

	@Getter private final FileLogger spawnerBreakLog;
	private final JavaPlugin plugin;

	private SpawnerRemoveListener(JavaPlugin plugin) {
		this.plugin = plugin;
		spawnerBreakLog = new FileLogger(plugin, "BrokenSpawners");
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void onMine(BlockBreakEvent e) {
		if (e.getBlock().getType() != Material.MOB_SPAWNER)
			return;
		
		if (Bukkit.getPluginManager().isPluginEnabled("EpicSpawners"))
			return;
		
		String entity = ((CreatureSpawner)e.getBlock()).getCreatureTypeName();
		Bukkit.getScheduler().runTaskLaterAsynchronously(ClaimedSpawners.instance, ()->{
			if (e.getBlock().getWorld().getBlockAt(e.getBlock().getLocation()).getType() != Material.MOB_SPAWNER) {
				SpawnerManager.getInstance().removeSpawner(e.getBlock().getLocation());
				logBroken(e.getPlayer(), entity, e.getBlock().getLocation());
			}
		}, 40L);
	}
	
	private void logBroken(Player player, String entityName, Location loc) {
		spawnerBreakLog.log(player.getName()+" mined a"+(StringUtils.isVowel(entityName.charAt(0))?"":"n")+" "+entityName+" spawner at "+WorldUtils.locationToLegibleString(loc));
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		for (Block block : e.blockList()) {
			if (block.getType() == Material.MOB_SPAWNER) {
				logTNTed(SpawnerManager.getInstance().getOwner(block.getLocation()), block);
				SpawnerManager.getInstance().removeSpawner(block.getLocation());
			}
		}
	}

	private void logTNTed(Faction faction, Block block) {
		CreatureSpawner cs = (CreatureSpawner) block.getState();
		String location = WorldUtils.locationToLegibleString(block.getLocation());
		String entityName = cs.getSpawnedType().name().toLowerCase().replaceAll("_", " ");

		String logString = "A" + (StringUtils.isVowel(entityName.charAt(0)) ? "" : "n") + " " + entityName
				+ " spawner at " + location
				+ (faction == null || faction.isWilderness() ? " in the wilderness"
						: " belonging to " + faction.getTag())
				+ " Was TNTed";

		Set<Player> nearbyPlayers = WorldUtils.getNearbyPlayers(block.getLocation(), 256);
		if (!nearbyPlayers.isEmpty()) {
			logString += "\nNearby Players:";
			for (Player player : nearbyPlayers) {

				String extData = " (";

				if (tntRecentlyPlaced.containsKey(player)) {
					int tntPlaced = tntRecentlyPlaced.get(player);
					if (tntPlaced > 0)
						extData += " x" + tntPlaced + " TNT placed";
				}

				if (ceggRecentlyPlaced.containsKey(player)) {
					int ceegPlaced = ceggRecentlyPlaced.get(player);
					if (ceegPlaced > 0) {
						if (!extData.equals(" ("))
							extData += " and ";
						extData += " x" + ceegPlaced + " Creeper eggs used";
					}
				}

				extData = extData.equals(" (") ? "" : extData + " in the last 60 seconds)";

				logString += "\n - " + player.getName() + ": " + WorldUtils.locationToLegibleString(player.getLocation())
						+ extData;
			}
		}

		spawnerBreakLog.log(logString);
	}

	@EventHandler(ignoreCancelled = false)
	public void onTNTPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.TNT)
			return;

		Player player = e.getPlayer();

		if (!tntRecentlyPlaced.containsKey(player))
			tntRecentlyPlaced.put(player, 0);
		tntRecentlyPlaced.put(player, tntRecentlyPlaced.get(player) + 1);

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			tntRecentlyPlaced.put(player, tntRecentlyPlaced.get(player) - 1);
		}, 1200);
	}

	@EventHandler
	public void onCeggUse(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getMaterial() == Material.MONSTER_EGG
				&& e.getItem().getDurability() == 50) {

			Player player = e.getPlayer();

			if (!ceggRecentlyPlaced.containsKey(player))
				ceggRecentlyPlaced.put(player, 0);
			ceggRecentlyPlaced.put(player, ceggRecentlyPlaced.get(player) + 1);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				ceggRecentlyPlaced.put(player, ceggRecentlyPlaced.get(player) - 1);
			}, 1200);
		}
	}
}
