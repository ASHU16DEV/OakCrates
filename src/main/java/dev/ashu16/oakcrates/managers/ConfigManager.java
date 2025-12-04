package dev.ashu16.oakcrates.managers;

import dev.ashu16.oakcrates.OakCrates;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private final OakCrates plugin;
    
    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration crates;
    
    private File configFile;
    private File messagesFile;
    private File cratesFile;

    public ConfigManager(OakCrates plugin) {
        this.plugin = plugin;
    }

    public void loadAllConfigs() {
        loadConfig();
        loadMessages();
        loadCrates();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            config.setDefaults(defaultConfig);
        }
    }

    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messages.setDefaults(defaultMessages);
        }
    }

    public void loadCrates() {
        cratesFile = new File(plugin.getDataFolder(), "crates.yml");
        
        if (!cratesFile.exists()) {
            plugin.saveResource("crates.yml", false);
        }
        
        crates = YamlConfiguration.loadConfiguration(cratesFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }

    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml: " + e.getMessage());
        }
    }

    public void saveCrates() {
        try {
            crates.save(cratesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save crates.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getCrates() {
        return crates;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        
        InputStream defaultStream = plugin.getResource("config.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            config.setDefaults(defaultConfig);
        }
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messages.setDefaults(defaultMessages);
        }
    }

    public void reloadCrates() {
        crates = YamlConfiguration.loadConfiguration(cratesFile);
    }
}
