package ex.rizzve.pulseDropHead.listeners;

import ex.rizzve.pulseDropHead.PulseDropHead;
import ex.rizzve.pulseDropHead.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Random;

public class Events implements Listener {
    
    private final PulseDropHead plugin;
    private final Random random;
    
    public Events(PulseDropHead plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ConfigManager config = plugin.getConfigManager();
        
        ConfigManager.DropMode mode = config.getDropMode();
        Player killer = player.getKiller();
        
        boolean shouldDrop = false;
        
        switch (mode) {
            case PVP:
                shouldDrop = killer != null;
                break;
            case PVE:
                shouldDrop = killer == null;
                break;
            case ALL:
                shouldDrop = true;
                break;
        }
        
        if (!shouldDrop) {
            return;
        }
        
        double chance = config.getDropChance();
        if (chance < 100.0 && random.nextDouble() * 100.0 > chance) {
            return;
        }
        
        ItemStack skull = createPlayerHead(player);
        Location deathLocation = player.getLocation();
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Item droppedHead = player.getWorld().dropItemNaturally(deathLocation, skull);
            if (config.isGlowingEnabled()) {
                plugin.getGlowingEffects().applyEffect(droppedHead);
            }
            if (config.isDespawnEnabled()) {
                plugin.getDespawnManager().scheduleRemoval(droppedHead);
            }
        }, 1L);
    }
    
    public ItemStack createPlayerHead(Player player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);
        
        String displayName = plugin.getConfigManager().getHeadDisplayName()
                .replace("%player%", player.getName());
        meta.setDisplayName(displayName);
        
        skull.setItemMeta(meta);
        return skull;
    }
}
