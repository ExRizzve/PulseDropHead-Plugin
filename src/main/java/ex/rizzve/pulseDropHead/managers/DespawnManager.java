package ex.rizzve.pulseDropHead.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DespawnManager {
    
    private final JavaPlugin plugin;
    private final boolean enabled;
    private final int despawnTime;
    private final Map<UUID, BukkitTask> scheduledDespawns;
    
    public DespawnManager(JavaPlugin plugin, boolean enabled, int despawnTime) {
        this.plugin = plugin;
        this.enabled = enabled;
        this.despawnTime = despawnTime;
        this.scheduledDespawns = new HashMap<>();
    }
    
    public void scheduleRemoval(Item item) {
        if (!enabled || item == null) {
            return;
        }
        
        if (item.getItemStack().getType() != Material.PLAYER_HEAD) {
            return;
        }
        
        UUID itemId = item.getUniqueId();
        
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (item.isValid() && !item.isDead()) {
                item.remove();
            }
            scheduledDespawns.remove(itemId);
        }, despawnTime * 20L);
        
        scheduledDespawns.put(itemId, task);
    }
    
    public void cancelRemoval(UUID itemId) {
        BukkitTask task = scheduledDespawns.remove(itemId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    public void shutdown() {
        scheduledDespawns.values().forEach(task -> {
            if (!task.isCancelled()) {
                task.cancel();
            }
        });
        scheduledDespawns.clear();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
