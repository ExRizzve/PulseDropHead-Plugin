package ex.rizzve.pulseDropHead.effects;

import dev.pulsemc.pulse.api.network.PulsePlayer;
import dev.pulsemc.pulse.api.virtual.block.VirtualBlockManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
// Maybe don't work...
public class VirtualHeadMarker {
    
    private final JavaPlugin plugin;
    private final Map<UUID, Location> markedLocations;
    private final boolean enabled;
    
    public VirtualHeadMarker(JavaPlugin plugin, boolean enabled) {
        this.plugin = plugin;
        this.enabled = enabled;
        this.markedLocations = new HashMap<>();
    }
    
    public void markHeadLocation(Player player, Location location) {
        if (!enabled) {
            return;
        }
        
        PulsePlayer pulsePlayer = PulsePlayer.from(player);
        if (pulsePlayer == null) {
            return;
        }
        
        VirtualBlockManager blockManager = pulsePlayer.getVirtualBlockManager();
        
        Location beaconLoc = location.clone().add(0, 1, 0);
        blockManager.setBlock(beaconLoc, Material.BEACON.createBlockData());
        
        markedLocations.put(player.getUniqueId(), beaconLoc);
    }
    
    public void removeMarker(Player player) {
        if (!enabled) {
            return;
        }
        
        Location location = markedLocations.remove(player.getUniqueId());
        if (location == null) {
            return;
        }
        
        PulsePlayer pulsePlayer = PulsePlayer.from(player);
        if (pulsePlayer == null) {
            return;
        }
        
        VirtualBlockManager blockManager = pulsePlayer.getVirtualBlockManager();
        blockManager.removeBlock(location);
    }
    
    public void cleanup() {
        markedLocations.clear();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
