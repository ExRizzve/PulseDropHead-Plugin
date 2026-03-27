package ex.rizzve.pulseDropHead.managers;

import ex.rizzve.pulseDropHead.PulseDropHead;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LanguageManager {
    
    private final PulseDropHead plugin;
    private FileConfiguration langConfig;
    private String currentLang;
    
    public LanguageManager(PulseDropHead plugin) {
        this.plugin = plugin;
    }
    
    public void loadLanguage() {
        currentLang = plugin.getConfig().getString("language", "ru");
        
        File langFolder = new File(plugin.getDataFolder(), "languages");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        
        saveDefaultLanguageFiles(langFolder);
        
        File langFile = new File(langFolder, currentLang + ".yml");
        if (!langFile.exists()) {
            langFile = new File(langFolder, "ru.yml");
        }
        
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }
    
    private void saveDefaultLanguageFiles(File langFolder) {
        saveLanguageFile(langFolder, "ru.yml");
        saveLanguageFile(langFolder, "eng.yml");
    }
    
    private void saveLanguageFile(File langFolder, String fileName) {
        File file = new File(langFolder, fileName);
        if (!file.exists()) {
            try (InputStream in = plugin.getResource("languages/" + fileName)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public String getMessage(String key) {
        return langConfig.getString(key, key);
    }
    
    public String getCurrentLang() {
        return currentLang;
    }
    
    public void setLanguage(String lang) {
        this.currentLang = lang;
        plugin.getConfig().set("language", lang);
        plugin.saveConfig();
        loadLanguage();
    }
}
