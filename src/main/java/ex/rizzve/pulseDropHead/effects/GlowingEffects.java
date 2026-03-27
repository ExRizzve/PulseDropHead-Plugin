package ex.rizzve.pulseDropHead.effects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlowingEffects {
    
    private final JavaPlugin plugin;
    private final boolean active;
    private final ChatColor effectColor;
    private final int durationSeconds;
    private final Map<UUID, BukkitTask> activeTasks;
    private Team effectTeam;
    
    public GlowingEffects(JavaPlugin plugin, boolean active, String color, int durationSeconds) {
        this.plugin = plugin;
        this.active = active;
        this.effectColor = parseColor(color);
        this.durationSeconds = durationSeconds;
        this.activeTasks = new HashMap<>();
        
        if (active) {
            initializeTeam();
        }
    }
    
    private ChatColor parseColor(String colorName) {
        try {
            ChatColor parsed = ChatColor.valueOf(colorName.toUpperCase());
            return parsed.isColor() ? parsed : ChatColor.WHITE;
        } catch (IllegalArgumentException e) {
            return ChatColor.WHITE;
        }
    }
    
    private void initializeTeam() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "pdh_glow";
        
        effectTeam = board.getTeam(teamName);
        if (effectTeam == null) {
            effectTeam = board.registerNewTeam(teamName);
        }
        
        effectTeam.setColor(effectColor);
        effectTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        effectTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }
    
    public void applyEffect(Item item) {
        if (!active || item == null || effectTeam == null) {
            return;
        }
        
        UUID itemId = item.getUniqueId();
        String entityEntry = itemId.toString();
        
        effectTeam.addEntry(entityEntry);
        item.setGlowing(true);
        
        if (durationSeconds > 0) {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                removeEffect(item);
                activeTasks.remove(itemId);
            }, durationSeconds * 20L);
            
            activeTasks.put(itemId, task);
        }
    }
    
    public void removeEffect(Item item) {
        if (!active || item == null || effectTeam == null) {
            return;
        }
        
        UUID itemId = item.getUniqueId();
        String entityEntry = itemId.toString();
        
        if (effectTeam.hasEntry(entityEntry)) {
            effectTeam.removeEntry(entityEntry);
        }
        
        item.setGlowing(false);
        
        BukkitTask task = activeTasks.remove(itemId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void cleanup() {
        activeTasks.values().forEach(task -> {
            if (!task.isCancelled()) {
                task.cancel();
            }
        });
        activeTasks.clear();
    }
}
