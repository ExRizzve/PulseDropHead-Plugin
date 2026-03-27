package ex.rizzve.pulseDropHead.managers;

import ex.rizzve.pulseDropHead.PulseDropHead;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final PulseDropHead plugin;
    private FileConfiguration config;
    
    private DropMode dropMode;
    private double dropChance;
    private String headDisplayName;
    private boolean glowingEnabled;
    private String glowingColor;
    private int glowingDuration;
    private boolean despawnEnabled;
    private int despawnTime;
    private boolean virtualMarkers;
    
    public ConfigManager(PulseDropHead plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        String modeString = config.getString("drop-mode", "pvp").toLowerCase();
        dropMode = parseDropMode(modeString);
        dropChance = config.getDouble("drop-chance", 100.0);
        headDisplayName = config.getString("head-display-name", "Head %player%");
        glowingEnabled = config.getBoolean("glowing.enabled", false);
        glowingColor = config.getString("glowing.color", "WHITE");
        glowingDuration = config.getInt("glowing.duration-seconds", 30);
        despawnEnabled = config.getBoolean("despawn.enabled", true);
        despawnTime = config.getInt("despawn.time-seconds", 300);
        virtualMarkers = config.getBoolean("pulse.virtual-markers", false);
    }
    
    private DropMode parseDropMode(String mode) {
        switch (mode) {
            case "pvp":
                return DropMode.PVP;
            case "pve":
                return DropMode.PVE;
            case "all":
                return DropMode.ALL;
            default:
                return DropMode.PVP;
        }
    }
    
    public DropMode getDropMode() {
        return dropMode;
    }
    
    public double getDropChance() {
        return dropChance;
    }
    
    public String getHeadDisplayName() {
        return headDisplayName;
    }
    
    public boolean isGlowingEnabled() {
        return glowingEnabled;
    }
    
    public String getGlowingColor() {
        return glowingColor;
    }
    
    public int getGlowingDuration() {
        return glowingDuration;
    }
    
    public boolean isDespawnEnabled() {
        return despawnEnabled;
    }
    
    public int getDespawnTime() {
        return despawnTime;
    }
    
    public boolean isVirtualMarkers() {
        return virtualMarkers;
    }
    
    public enum DropMode {
        PVP,
        PVE,
        ALL
    }
}
