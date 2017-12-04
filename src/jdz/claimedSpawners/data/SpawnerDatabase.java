
package jdz.claimedSpawners.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.misc.WorldUtils;
import jdz.bukkitUtils.sql.SqlApi;
import jdz.bukkitUtils.sql.SqlColumn;
import jdz.bukkitUtils.sql.SqlColumnType;

public class SpawnerDatabase {
	private static SpawnerDatabase instance;

	public static SpawnerDatabase getInstance() {
		return instance;
	}

	private static final String tableName = "ClaimedSpawnersDatabase";
	private static final SqlColumn[] tableColumns = new SqlColumn[] {
			new SqlColumn("FactionID", SqlColumnType.STRING_128),
			new SqlColumn("SpawnerLocation", SqlColumnType.STRING_128), new SqlColumn("ChunkX", SqlColumnType.LONG),
			new SqlColumn("ChunkZ", SqlColumnType.INT_4_BYTE) };

	private SqlApi api = null;

	public static void init(JavaPlugin plugin) {
		instance = new SpawnerDatabase();
		instance.api = new SqlApi(plugin);
		instance.api.runOnConnect(() -> {
			instance.api.addTable(tableName, tableColumns);
		});
	}

	public void addSpawner(String factionID, Location location) {
		Chunk chunk = location.getChunk();

		String locationStr = WorldUtils.locationToString(location);

		List<String[]> result = api
				.getRows("SELECT factionID FROM " + tableName + " WHERE spawnerLocation = '" + locationStr + "';");
		if (result.isEmpty())
			api.executeUpdateAsync("INSERT INTO " + tableName + " " + "(factionID,spawnerLocation, chunkX, chunkZ) "
					+ "VALUES('" + factionID + "','" + locationStr + "'," + chunk.getX() + "," + chunk.getZ() + ");");
	}

	public void removeSpawner(Location location) {
		api.executeUpdateAsync("DELETE FROM " + tableName + " WHERE spawnerLocation = '"
				+ WorldUtils.locationToString(location) + "'");
	}

	public void setChunkFaction(String factionID, long chunkX, long chunkY) {
		api.executeUpdateAsync("UPDATE " + tableName + " SET factionID = '" + factionID + "' WHERE chunkX = " + chunkX
				+ " AND chunkZ = " + chunkY + ";");
	}

	public List<Block> getSpawnersIn(long chunkX, long chunkY) {
		List<Block> blocks = new ArrayList<Block>();

		List<String[]> rows = api.getRows("SELECT spawnerLocation FROM " + tableName + " WHERE chunkX = " + chunkX
				+ " AND chunkZ = " + chunkY + ";");
		for (String[] row : rows) {
			Location spawnerLocation = WorldUtils.locationFromString(row[0]);
			blocks.add(spawnerLocation.getBlock());
		}

		return blocks;
	}

	public List<Block> getSpawners(String factionID) {
		List<Block> blocks = new ArrayList<Block>();

		List<String[]> rows = api
				.getRows("SELECT spawnerLocation FROM " + tableName + " WHERE factionID = '" + factionID + "';");
		for (String[] row : rows) {
			Location spawnerLocation = WorldUtils.locationFromString(row[0]);
			blocks.add(spawnerLocation.getBlock());
		}

		return blocks;
	}

	public void clearSpawnerData(String factionID) {
		api.executeUpdateAsync("DELETE FROM " + tableName + " WHERE factionID = '" + factionID + "';");
	}

	public String getOwner(Location location) {
		try {
		return api.getRows("SELECT factionID FROM " + tableName + " WHERE spawnerLocation = '"
				+ WorldUtils.locationToString(location) + "';").get(0)[0];
		}
		catch (Exception e) { return null; }
	}
}
