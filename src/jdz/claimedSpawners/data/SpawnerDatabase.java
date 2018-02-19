
package jdz.claimedSpawners.data;

import java.util.Set;

import net.redstoneore.legacyfactions.entity.Faction;

public interface SpawnerDatabase {
	public static SpawnerDatabase getInstance() {
		return SpawnerDatabaseSQL.getInstance();
	}
	
	public void addSpawner(ClaimedSpawner spawner);
	public void removeSpawner(ClaimedSpawner spawnerToRemove);
	public void setChunkFaction(Faction faction, long chunkX, long chunkY);
	public void clearSpawnerData(Faction faction);

	public Set<ClaimedSpawner> getAllSpawners();
}
