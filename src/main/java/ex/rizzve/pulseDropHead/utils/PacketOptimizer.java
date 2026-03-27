package ex.rizzve.pulseDropHead.utils;

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
    private final boolean pulseAvailable;
    private boolean isProcessing;
    
    public PacketOptimizer(JavaPlugin plugin, int batchDelayTicks) {
        this.plugin = plugin;
        this.pendingItems = new HashMap<>();
        this.batchDelay = batchDelayTicks;
        this.pulseAvailable = checkPulseAPI();
        
        if (pulseAvailable) {
            plugin.getLogger().info("Pulse detected. PacketOptimizer is working.");
        } else {
            plugin.getLogger().info("Running on Paper/Purpur. PacketOptimizer is not working");
        }
    }
    
    private boolean checkPulseAPI() {
        try {
            Class.forName("dev.pulsemc.pulse.api.network.PulsePlayer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public void queueItemGive(Player player, ItemStack item) {
        if (!pulseAvailable) {
            player.getInventory().addItem(item);
            return;
        }
        
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
                
                if (pulseAvailable) {
                    flushPulseBuffer(player);
                }
            }
        }
    }
    
    private void flushPulseBuffer(Player player) {
        try {
            Class<?> pulsePlayerClass = Class.forName("dev.pulsemc.pulse.api.network.PulsePlayer");
            Object pulsePlayer = pulsePlayerClass.getMethod("from", Player.class).invoke(null, player);
            
            if (pulsePlayer != null) {
                Object buffer = pulsePlayer.getClass().getMethod("getBuffer").invoke(pulsePlayer);
                buffer.getClass().getMethod("flush").invoke(buffer);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to flush Pulse buffer: " + e.getMessage());
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
    
    public boolean isPulseAvailable() {
        return pulseAvailable;
    }
}
