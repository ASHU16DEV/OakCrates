package dev.ashu16.oakcrates.managers;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.Reward;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClaimLogManager {

    private final OakCrates plugin;
    private final SimpleDateFormat dateFormat;
    private File logFile;

    public ClaimLogManager(OakCrates plugin) {
        this.plugin = plugin;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        initLogFile();
    }

    private void initLogFile() {
        String filePath = plugin.getConfigManager().getConfig().getString("logging.claims.file-path", "logs/claims.log");
        logFile = new File(plugin.getDataFolder(), filePath);
        
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create claim log file: " + e.getMessage());
            }
        }
    }

    public void logClaim(Player player, Crate crate, Reward reward) {
        boolean logEnabled = plugin.getConfigManager().getConfig().getBoolean("logging.claims.enabled", true);
        if (!logEnabled) return;

        String timestamp = dateFormat.format(new Date());
        String rewardType = getRewardType(reward);
        String rewardName = reward.getDisplayName();
        
        String logEntry = String.format("[%s] %s claimed '%s' from crate '%s' (Type: %s)",
                timestamp, player.getName(), rewardName, crate.getId(), rewardType);

        boolean logToConsole = plugin.getConfigManager().getConfig().getBoolean("logging.claims.console", true);
        if (logToConsole) {
            plugin.getLogger().info(logEntry);
        }

        boolean logToFile = plugin.getConfigManager().getConfig().getBoolean("logging.claims.file", true);
        if (logToFile) {
            writeToFile(logEntry);
        }
    }

    private String getRewardType(Reward reward) {
        boolean hasCommands = reward.hasCommands();
        boolean hasItems = reward.hasItems();
        
        if (hasCommands && hasItems) {
            return "BOTH";
        } else if (hasCommands) {
            return "COMMAND";
        } else if (hasItems) {
            return "ITEM";
        }
        return "NONE";
    }

    private void writeToFile(String logEntry) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not write to claim log: " + e.getMessage());
        }
    }

    public void logKeyGive(String admin, String playerName, String crateId, int amount) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] %s gave %d key(s) for crate '%s' to %s",
                timestamp, admin, amount, crateId, playerName);
        
        plugin.getLogger().info(logEntry);
        writeToFile(logEntry);
    }

    public void logKeyTake(String admin, String playerName, String crateId, int amount) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] %s took %d key(s) for crate '%s' from %s",
                timestamp, admin, amount, crateId, playerName);
        
        plugin.getLogger().info(logEntry);
        writeToFile(logEntry);
    }

    public void logKeySet(String admin, String playerName, String crateId, int amount) {
        String timestamp = dateFormat.format(new Date());
        String logEntry = String.format("[%s] %s set %s's keys for crate '%s' to %d",
                timestamp, admin, playerName, crateId, amount);
        
        plugin.getLogger().info(logEntry);
        writeToFile(logEntry);
    }
}
