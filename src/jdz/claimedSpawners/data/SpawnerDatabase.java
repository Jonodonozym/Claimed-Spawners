
package jdz.claimedSpawners.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import jdz.bukkitUtils.misc.WorldUtils;
import jdz.bukkitUtils.sql.SqlColumn;
import jdz.bukkitUtils.sql.SqlColumnType;
import jdz.bukkitUtils.sql.SqlDatabase;
import jdz.bukkitUtils.sql.SqlRow;
import jdz.claimedSpawners.ClaimedSpawners;
import lombok.Getter;

class SpawnerDatabase extends SqlDatabase{
	@Getter private static final SpawnerDatabase instance = new SpawnerDatabase(ClaimedSpawners.instance);

	private static final String tableName = "ClaimedSpawnersDatabase";
	private static final SqlColumn[] tableColumns = new SqlColumn[] {
			new SqlColumn("FactionID", SqlColumnType.STRING_128),
			new SqlColumn("SpawnerLocation", SqlColumnType.STRING_128), new SqlColumn("ChunkX", SqlColumnType.LONG),
			new SqlColumn("ChunkZ", SqlColumnType.INT_4_BYTE) };

	private SpawnerDatabase(JavaPlugin plugin) {
		super(plugin);
		runOnConnect(() -> {
			addTable(tableName, tableColumns);
		});
	}

	public void addSpawner(ClaimedSpawner spawner) {
		Bukkit.getScheduler().runTaskAsynchronously(ClaimedSpawners.instance, () -> {
			String locationStr = WorldUtils.locationToString(spawner.getLocation());

			List<SqlRow> result = query("SELECT factionID FROM " + tableName + " WHERE spawnerLocation = '" + locationStr + "';");
			if (result.isEmpty())
				updateAsync("INSERT INTO " + tableName + " " + "(factionID,spawnerLocation, chunkX, chunkZ) "
						+ "VALUES('" + spawner.getFaction().getId() + "','" + locationStr + "'," + spawner.getChunkX()
						+ "," + spawner.getChunkZ() + ");");
		});
	}

	public void removeSpawner(ClaimedSpawner spawnerToRemove) {
		updateAsync("DELETE FROM " + tableName + " WHERE spawnerLocation = '"
				+ WorldUtils.locationToString(spawnerToRemove.getLocation()) + "'");
	}

	public Set<ClaimedSpawner> getAllSpawners() {
		Set<ClaimedSpawner> spawners = new HashSet<ClaimedSpawner>();

		List<SqlRow> rows = query("SELECT factionID, spawnerLocation FROM " + tableName + ";");
		for (SqlRow row : rows) {
			Location location = WorldUtils.locationFromString(row.get(1));
			Faction faction = Factions.getInstance().getFactionById(row.get(0));
			spawners.add(new ClaimedSpawner(faction, location));
		}

		return spawners;
	}

	public void setChunkFaction(Faction faction, long chunkX, long chunkY) {
		updateAsync("UPDATE " + tableName + " SET factionID = '" + faction.getId() + "' WHERE chunkX = "
				+ chunkX + " AND chunkZ = " + chunkY + ";");
	}

	public void clearSpawnerData(Faction faction) {
		updateAsync("DELETE FROM " + tableName + " WHERE factionID = '" + faction.getId() + "';");
	}
}
