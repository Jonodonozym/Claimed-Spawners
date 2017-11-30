
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
import me.markeh.factionsframework.entities.Faction;
import me.markeh.factionsframework.entities.Factions;

public class SpawnerDatabase {
	private static SpawnerDatabase instance;

	public static SpawnerDatabase getInstance() {
		return instance;
	}

	private static final String tableName = "ClaimedSpawnersDatabase";
	private static final SqlColumn[] tableColumns = new SqlColumn[] {
			new SqlColumn("FactionID", SqlColumnType.STRING_128),
			new SqlColumn("SpawnerLocation", SqlColumnType.STRING_128),
			new SqlColumn("ChunkX", SqlColumnType.INT_4_BYTE), new SqlColumn("ChunkZ", SqlColumnType.INT_4_BYTE) };

	private SqlApi api = null;

	public static void init(JavaPlugin plugin) {
		instance = new SpawnerDatabase();
		instance.api = new SqlApi(plugin);
		instance.api.runOnConnect(() -> {
			instance.api.addTable(tableName, tableColumns);
		});
	}

	public void addSpawner(Location location) {
		Chunk chunk = location.getChunk();
		Faction faction = Factions.getFactionAt(chunk);

		api.executeUpdateAsync("INSERT INTO " + tableName + " " + "(factionID,spawnerLocation, chunkX, chunkZ) "
				+ "VALUES('" + faction.getId() + "','" + WorldUtils.locationToString(location) + "'," + chunk.getX()
				+ "," + chunk.getZ() + ");");
	}

	public void removeSpawner(Location location) {
		api.executeUpdateAsync("DELETE FROM " + tableName + " WHERE spawnerLocation = '"
				+ WorldUtils.locationToString(location) + "'");
	}

	public void setChunkFaction(Chunk chunk, Faction newOwner) {
		api.executeUpdateAsync("UPDATE " + tableName + " SET factionID = '" + newOwner.getId() + "' WHERE chunkX = "
				+ chunk.getX() + " AND chunkZ = " + chunk.getZ() + ";");
	}
	
	public List<Block> getSpawnersIn(Chunk chunk){
		List<Block> blocks = new ArrayList<Block>();
		
		List<String[]> rows = api.getRows("SELECT spawnerLocation FROM "+tableName+" WHERE chunkX = "+chunk.getX()+" AND chunkZ = "+chunk.getZ()+";");
		for (String[] row: rows) {
			Location spawnerLocation = WorldUtils.locationFromString(row[0]);
			blocks.add(spawnerLocation.getBlock());
		}
		
		return blocks;
	}
	
	public List<Block> getSpawners(Faction faction){
		List<Block> blocks = new ArrayList<Block>();
		
		List<String[]> rows = api.getRows("SELECT spawnerLocation FROM "+tableName+" WHERE factionID = '"+faction.getId()+"';");
		for (String[] row: rows) {
			Location spawnerLocation = WorldUtils.locationFromString(row[0]);
			blocks.add(spawnerLocation.getBlock());
		}
		
		return blocks;
	}
	
	public void clearSpawnerData(Faction faction) {
		api.executeUpdateAsync("DELETE FROM "+tableName+" WHERE factionID = '"+faction.getId()+"';");
	}
}
