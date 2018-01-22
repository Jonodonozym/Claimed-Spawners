
package jdz.claimedSpawners.data;

import org.bukkit.Location;

import com.massivecraft.factions.Faction;

import lombok.Data;
import lombok.NonNull;

@Data
public class ClaimedSpawner {
	@NonNull
	private Faction faction;
	private final Location location;
	private final int chunkX, chunkZ;

	public ClaimedSpawner(Faction faction, Location location) {
		this.faction = faction;
		this.location = location;
		this.chunkX = location.getChunk().getX();
		this.chunkZ = location.getChunk().getZ();
	}
}
