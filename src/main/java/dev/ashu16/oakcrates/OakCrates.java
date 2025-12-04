package dev.ashu16.oakcrates;

import dev.ashu16.oakcrates.animations.AnimationExecutor;
import dev.ashu16.oakcrates.commands.OakCratesCommand;
import dev.ashu16.oakcrates.commands.KeysCommand;
import dev.ashu16.oakcrates.discord.DiscordManager;
import dev.ashu16.oakcrates.hologram.HologramManager;
import dev.ashu16.oakcrates.listeners.BlockInteractListener;
import dev.ashu16.oakcrates.listeners.GUIListener;
import dev.ashu16.oakcrates.listeners.ChatInputListener;
import dev.ashu16.oakcrates.listeners.PlayerJoinQuitListener;
import dev.ashu16.oakcrates.managers.CrateManager;
import dev.ashu16.oakcrates.managers.KeyManager;
import dev.ashu16.oakcrates.managers.DatabaseManager;
import dev.ashu16.oakcrates.managers.ConfigManager;
import dev.ashu16.oakcrates.managers.ChatInputManager;
import dev.ashu16.oakcrates.managers.ClaimLogManager;
import dev.ashu16.oakcrates.tasks.HologramUpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class OakCrates extends JavaPlugin {

    private static OakCrates instance;
    
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private CrateManager crateManager;
    private KeyManager keyManager;
    private HologramManager hologramManager;
    private ChatInputManager chatInputManager;
    private ClaimLogManager claimLogManager;
    private DiscordManager discordManager;
    private AnimationExecutor animationExecutor;
    private HologramUpdateTask hologramUpdateTask;
    private BlockInteractListener blockInteractListener;

    @Override
    public void onEnable() {
        instance = this;
        
        createDataFolders();
        
        try {
            initializeManagers();
            registerCommands();
            registerListeners();
            startTasks();
            initializeDiscord();
            
            logStartup();
        } catch (Exception e) {
            logError("Failed to enable OakCrates!", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (discordManager != null) {
            discordManager.shutdown();
        }
        
        if (hologramUpdateTask != null) {
            hologramUpdateTask.cancel();
        }
        
        if (hologramManager != null) {
            hologramManager.removeAllHolograms();
        }
        
        if (databaseManager != null) {
            databaseManager.close();
        }
        
        logInfo("OakCrates disabled successfully!");
        instance = null;
    }

    private void logStartup() {
        getLogger().info("");
        getLogger().info("  ___        _     ____           _            ");
        getLogger().info(" / _ \\  __ _| | __/ ___|_ __ __ _| |_ ___  ___ ");
        getLogger().info("| | | |/ _` | |/ / |   | '__/ _` | __/ _ \\/ __|");
        getLogger().info("| |_| | (_| |   <| |___| | | (_| | ||  __/\\__ \\");
        getLogger().info(" \\___/ \\__,_|_|\\_\\\\____|_|  \\__,_|\\__\\___||___/");
        getLogger().info("");
        getLogger().info(" Version: " + getDescription().getVersion());
        getLogger().info(" Author: " + getDescription().getAuthors().get(0));
        getLogger().info(" Loaded " + crateManager.getAllCrates().size() + " crate(s)");
        getLogger().info(" Discord: " + (configManager.getConfig().getBoolean("discord.enabled", false) ? "Enabled" : "Disabled"));
        getLogger().info("");
        getLogger().info(" Plugin enabled successfully!");
        getLogger().info("");
    }

    private void createDataFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        File dataDir = new File(getDataFolder(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        File logsDir = new File(getDataFolder(), "logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
    }

    private void initializeManagers() {
        configManager = new ConfigManager(this);
        configManager.loadAllConfigs();
        
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        
        keyManager = new KeyManager(this);
        crateManager = new CrateManager(this);
        crateManager.loadCrates();
        
        hologramManager = new HologramManager(this);
        chatInputManager = new ChatInputManager(this);
        claimLogManager = new ClaimLogManager(this);
        animationExecutor = new AnimationExecutor(this);
    }

    private void initializeDiscord() {
        discordManager = new DiscordManager(this);
        discordManager.initialize();
    }

    private void registerCommands() {
        OakCratesCommand oakCratesCommand = new OakCratesCommand(this);
        getCommand("oakcrates").setExecutor(oakCratesCommand);
        getCommand("oakcrates").setTabCompleter(oakCratesCommand);
        
        KeysCommand keysCommand = new KeysCommand(this);
        getCommand("keys").setExecutor(keysCommand);
        getCommand("key").setExecutor(keysCommand);
    }

    private void registerListeners() {
        blockInteractListener = new BlockInteractListener(this);
        Bukkit.getPluginManager().registerEvents(blockInteractListener, this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatInputListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
    }

    private void startTasks() {
        int updateInterval = configManager.getConfig().getInt("hologram.update-interval", 100);
        hologramUpdateTask = new HologramUpdateTask(this);
        hologramUpdateTask.runTaskTimer(this, 100L, updateInterval);
    }

    public boolean reload() {
        try {
            if (hologramUpdateTask != null) {
                hologramUpdateTask.cancel();
            }
            
            hologramManager.removeAllHolograms();
            
            configManager.loadAllConfigs();
            crateManager.loadCrates();
            
            if (discordManager != null) {
                discordManager.reload();
            }
            
            int updateInterval = configManager.getConfig().getInt("hologram.update-interval", 100);
            hologramUpdateTask = new HologramUpdateTask(this);
            hologramUpdateTask.runTaskTimer(this, 20L, updateInterval);
            
            hologramManager.respawnAllHolograms();
            
            logInfo("OakCrates reloaded successfully!");
            return true;
        } catch (Exception e) {
            logError("Failed to reload OakCrates!", e);
            return false;
        }
    }

    public void logInfo(String message) {
        getLogger().info(message);
    }

    public void logWarning(String message) {
        getLogger().warning(message);
    }

    public void logError(String message, Exception e) {
        getLogger().severe(message);
        if (e != null) {
            e.printStackTrace();
        }
    }

    public static OakCrates getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public ChatInputManager getChatInputManager() {
        return chatInputManager;
    }

    public ClaimLogManager getClaimLogManager() {
        return claimLogManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public AnimationExecutor getAnimationExecutor() {
        return animationExecutor;
    }

    public BlockInteractListener getBlockInteractListener() {
        return blockInteractListener;
    }
}
