package ex.rizzve.pulseDropHead.utils;

import dev.pulsemc.pulse.api.network.NetworkBuffer;
import dev.pulsemc.pulse.api.network.PulsePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketOptimizer {
    
    private final JavaPlugin plugin;
    private final Map<UUID, List<ItemStack>> pendingItems;
    private final int batchDelay;
    private boolean isProcessing;
    
    public PacketOptimizer(JavaPlugin plugin, int batchDelayTicks) {
        this.plugin = plugin;
        this.pendingItems = new HashMap<>();
        this.batchDelay = batchDelayTicks;
        this.isProcessing = false;
    }
    
    public void queueItemGive(Player player, ItemStack item) {
        UUID playerId = player.getUniqueId();
        
        pendingItems.computeIfAbsent(playerId, k -> new ArrayList<>()).add(item);
        
        if (!isProcessing) {
            scheduleFlush();
        }
    }
    
    private void scheduleFlush() {
        isProcessing = true;
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            flushPendingItems();
            isProcessing = false;
        }, batchDelay);
    }
    
    private void flushPendingItems() {
        if (pendingItems.isEmpty()) {
            return;
        }
        
        Map<UUID, List<ItemStack>> snapshot = new HashMap<>(pendingItems);
        pendingItems.clear();
        
        for (Map.Entry<UUID, List<ItemStack>> entry : snapshot.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                List<ItemStack> items = entry.getValue();
                
                for (ItemStack item : items) {
                    player.getInventory().addItem(item);
                }
                
                PulsePlayer pulsePlayer = PulsePlayer.from(player);
                if (pulsePlayer != null) {
                    NetworkBuffer buffer = pulsePlayer.getBuffer();
                    buffer.flush();
                }
            }
        }
    }
    
    public void forceFlush() {
        if (!pendingItems.isEmpty()) {
            flushPendingItems();
            isProcessing = false;
        }
    }
    
    public int getPendingCount() {
        return pendingItems.values().stream().mapToInt(List::size).sum();
    }
}
