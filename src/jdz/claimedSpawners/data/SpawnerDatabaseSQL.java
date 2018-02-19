
package jdz.claimedSpawners.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.misc.WorldUtils;
import jdz.bukkitUtils.sql.Database;
import jdz.bukkitUtils.sql.SqlColumn;
import jdz.bukkitUtils.sql.SqlColumnType;
import jdz.claimedSpawners.ClaimedSpawners;
import lombok.Getter;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;

class SpawnerDatabaseSQL extends Database implements SpawnerDatabase{
	@Getter private static final SpawnerDatabaseSQL instance = new SpawnerDatabaseSQL(ClaimedSpawners.instance);

	private static final String tableName = "ClaimedSpawnersDatabase";
	private static final SqlColumn[] tableColumns = new SqlColumn[] {
			new SqlColumn("FactionID", SqlColumnType.STRING_128),
			new SqlColumn("SpawnerLocation", SqlColumnType.STRING_128), new SqlColumn("ChunkX", SqlColumnType.LONG),
			new SqlColumn("ChunkZ", SqlColumnType.INT_4_BYTE) };

	private SpawnerDatabaseSQL(JavaPlugin plugin) {
		super(plugin);
		api.runOnConnect(() -> {
			api.addTable(tableName, tableColumns);
		});
	}

	@Override
	public void addSpawner(ClaimedSpawner spawner) {
		Bukkit.getScheduler().runTaskAsynchronously(ClaimedSpawners.instance, () -> {
			String locationStr = WorldUtils.locationToString(spawner.getLocation());

			List<String[]> result = api
					.getRows("SELECT factionID FROM " + tableName + " WHERE spawnerLocation = '" + locationStr + "';");
			if (result.isEmpty())
				api.executeUpdateAsync("INSERT INTO " + tableName + " " + "(factionID,spawnerLocation, chunkX, chunkZ) "
						+ "VALUES('" + spawner.getFaction().getId() + "','" + locationStr + "'," + spawner.getChunkX()
						+ "," + spawner.getChunkZ() + ");");
		});
	}

	@Override
	public void removeSpawner(ClaimedSpawner spawnerToRemove) {
		api.executeUpdateAsync("DELETE FROM " + tableName + " WHERE spawnerLocation = '"
				+ WorldUtils.locationToString(spawnerToRemove.getLocation()) + "'");
	}

	@Override
	public Set<ClaimedSpawner> getAllSpawners() {
		Set<ClaimedSpawner> spawners = new HashSet<ClaimedSpawner>();

		List<String[]> rows = api
				.getRows("SELECT factionID, spawnerLocation FROM " + tableName + ";");
		for (String[] row : rows) {
			Location location = WorldUtils.locationFromString(row[1]);
			Faction faction = FactionColl.get().getFactionById(row[0]);
			spawners.add(new ClaimedSpawner(faction, location));
		}

		return spawners;
	}

	@Override
	public void setChunkFaction(Faction faction, long chunkX, long chunkY) {
		api.executeUpdateAsync("UPDATE " + tableName + " SET factionID = '" + faction.getId() + "' WHERE chunkX = "
				+ chunkX + " AND chunkZ = " + chunkY + ";");
	}

	@Override
	public void clearSpawnerData(Faction faction) {
		api.executeUpdateAsync("DELETE FROM " + tableName + " WHERE factionID = '" + faction.getId() + "';");
	}
}
