
package jdz.claimedSpawners.data;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import lombok.Getter;

public class SpawnerManager {
	@Getter private static final SpawnerManager instance = new SpawnerManager();
	
	private final Set<ClaimedSpawner> spawners;
	
	private SpawnerManager() {
		spawners = SpawnerDatabase.getInstance().getAllSpawners();
	}

	public Set<ClaimedSpawner> getSpawnersIn(long chunkX, long chunkY){
		Set<ClaimedSpawner> chunkSpawners = new HashSet<ClaimedSpawner>();
		for (ClaimedSpawner spawner: spawners)
			if (spawner.getChunkX() == chunkX && spawner.getChunkZ() == chunkY)
				chunkSpawners.add(spawner);
		return chunkSpawners;
	}
	
	public Set<ClaimedSpawner> getByFaction(Faction faction) {
		Set<ClaimedSpawner> factionSpawners = new HashSet<ClaimedSpawner>();
		for (ClaimedSpawner spawner: spawners)
			if (spawner.getFaction().getId().equals(faction.getId()))
				factionSpawners.add(spawner);
		return factionSpawners;
	}

	public void setChunkFaction(Faction faction, long chunkX, long chunkZ) {
		for (ClaimedSpawner spawner: spawners)
			if (spawner.getChunkX() == chunkX && spawner.getChunkZ() == chunkZ)
				spawner.setFaction(faction);
		SpawnerDatabase.getInstance().setChunkFaction(faction, chunkX, chunkZ);
	}

	public void clearFaction(Faction faction) {
		spawners.removeAll(getByFaction(faction));
		SpawnerDatabase.getInstance().clearSpawnerData(faction);
	}

	public void addSpawner(Faction chunkFaction, Location location) {
		ClaimedSpawner spawner = new ClaimedSpawner(chunkFaction, location);
		spawners.add(spawner);
		SpawnerDatabase.getInstance().addSpawner(spawner);
	}

	public Faction getOwner(Location location) {
		for (ClaimedSpawner spawner: spawners)
			if (spawner.getLocation().equals(location))
				return spawner.getFaction();
		return Factions.getInstance().getWilderness();
	}

	public void removeSpawner(Location location) {
		ClaimedSpawner spawnerToRemove = null;
		for (ClaimedSpawner spawner: spawners)
			if (spawner.getLocation().equals(location)) {
				spawnerToRemove = spawner;
				break;
			}
		if (spawnerToRemove == null)
			return;
		spawners.remove(spawnerToRemove);
		SpawnerDatabase.getInstance().removeSpawner(spawnerToRemove);
	}
}
