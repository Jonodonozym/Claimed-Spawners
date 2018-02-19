
package jdz.claimedSpawners.data;

import java.io.File;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import jdz.bukkitUtils.misc.Config;
import jdz.claimedSpawners.ClaimedSpawners;
import lombok.Getter;
import net.redstoneore.legacyfactions.entity.Faction;

class SpawnerDatabaseYML implements SpawnerDatabase{
	@Getter private static final SpawnerDatabaseYML instance = new SpawnerDatabaseYML();
	private final FileConfiguration config = Config.getConfig(ClaimedSpawners.instance, "spawners.yml");
	private final File configFile = Config.getConfigFile(ClaimedSpawners.instance, "spawners.yml");

	@Override
	public void addSpawner(ClaimedSpawner spawner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSpawner(ClaimedSpawner spawnerToRemove) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<ClaimedSpawner> getAllSpawners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setChunkFaction(Faction faction, long chunkX, long chunkY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearSpawnerData(Faction faction) {
		// TODO Auto-generated method stub
		
	}
}
