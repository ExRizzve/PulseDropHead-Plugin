package ex.rizzve.pulseDropHead;

import ex.rizzve.pulseDropHead.commands.Cmd;
import ex.rizzve.pulseDropHead.effects.GlowingEffects;
import ex.rizzve.pulseDropHead.effects.VirtualHeadMarker;
import ex.rizzve.pulseDropHead.listeners.Events;
import ex.rizzve.pulseDropHead.managers.ConfigManager;
import ex.rizzve.pulseDropHead.managers.DespawnManager;
import ex.rizzve.pulseDropHead.managers.LanguageManager;
import ex.rizzve.pulseDropHead.utils.PacketOptimizer;
import org.bukkit.plugin.java.JavaPlugin;

public final class PulseDropHead extends JavaPlugin {

    private static PulseDropHead instance;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private GlowingEffects glowingEffects;
    private DespawnManager despawnManager;
    private PacketOptimizer packetOptimizer;
    private VirtualHeadMarker virtualHeadMarker;

    @Override
    public void onEnable() {
        instance = this;
        
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        languageManager = new LanguageManager(this);
        languageManager.loadLanguage();
        
        glowingEffects = new GlowingEffects(
            this,
            configManager.isGlowingEnabled(),
            configManager.getGlowingColor(),
            configManager.getGlowingDuration()
        );
        
        despawnManager = new DespawnManager(
            this,
            configManager.isDespawnEnabled(),
            configManager.getDespawnTime()
        );
        
        packetOptimizer = new PacketOptimizer(this, 2);
        
        virtualHeadMarker = new VirtualHeadMarker(
            this,
            configManager.isVirtualMarkers()
        );
        
        getServer().getPluginManager().registerEvents(new Events(this), this);
        
        Cmd commandExecutor = new Cmd(this);
        getCommand("pulsedrophead").setExecutor(commandExecutor);
        getCommand("pulsedrophead").setTabCompleter(new ex.rizzve.pulseDropHead.commands.TabCompleter());
        
        getLogger().info("PulseDropHead enabled");
    }

    @Override
    public void onDisable() {
        if (glowingEffects != null) {
            glowingEffects.cleanup();
        }
        if (despawnManager != null) {
            despawnManager.shutdown();
        }
        if (packetOptimizer != null) {
            packetOptimizer.forceFlush();
        }
        if (virtualHeadMarker != null) {
            virtualHeadMarker.cleanup();
        }
    }

    public static PulseDropHead getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public GlowingEffects getGlowingEffects() {
        return glowingEffects;
    }

    public DespawnManager getDespawnManager() {
        return despawnManager;
    }

    public PacketOptimizer getPacketOptimizer() {
        return packetOptimizer;
    }

    public VirtualHeadMarker getVirtualHeadMarker() {
        return virtualHeadMarker;
    }
}
