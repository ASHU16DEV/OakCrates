package dev.ashu16.oakcrates.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.ashu16.oakcrates.OakCrates;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final OakCrates plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(OakCrates plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        String dbPath = plugin.getDataFolder().getAbsolutePath() + File.separator + 
                       plugin.getConfigManager().getConfig().getString("database.file", "data/oakcrates.db");
        
        File dbFile = new File(dbPath);
        dbFile.getParentFile().mkdirs();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbPath);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setMaxLifetime(60000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        
        createTables();
    }

    private void createTables() {
        String keysTable = "CREATE TABLE IF NOT EXISTS player_keys (" +
                          "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                          "uuid VARCHAR(36) NOT NULL," +
                          "crate_id VARCHAR(64) NOT NULL," +
                          "amount INTEGER DEFAULT 0," +
                          "UNIQUE(uuid, crate_id))";

        String claimsTable = "CREATE TABLE IF NOT EXISTS player_claims (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "uuid VARCHAR(36) NOT NULL," +
                            "crate_id VARCHAR(64) NOT NULL," +
                            "reward_id VARCHAR(64) NOT NULL," +
                            "claim_count INTEGER DEFAULT 0," +
                            "UNIQUE(uuid, crate_id, reward_id))";

        String globalClaimsTable = "CREATE TABLE IF NOT EXISTS global_claims (" +
                                   "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                   "crate_id VARCHAR(64) NOT NULL," +
                                   "reward_id VARCHAR(64) NOT NULL," +
                                   "claim_count INTEGER DEFAULT 0," +
                                   "UNIQUE(crate_id, reward_id))";

        try (Connection conn = getConnection()) {
            conn.createStatement().execute(keysTable);
            conn.createStatement().execute(claimsTable);
            conn.createStatement().execute(globalClaimsTable);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create database tables", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public int getPlayerKeys(UUID uuid, String crateId) {
        String query = "SELECT amount FROM player_keys WHERE uuid = ? AND crate_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, crateId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("amount");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player keys", e);
        }
        
        return 0;
    }

    public void setPlayerKeys(UUID uuid, String crateId, int amount) {
        String query = "INSERT OR REPLACE INTO player_keys (uuid, crate_id, amount) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, crateId);
            stmt.setInt(3, Math.max(0, amount));
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set player keys", e);
        }
    }

    public void addPlayerKeys(UUID uuid, String crateId, int amount) {
        int currentKeys = getPlayerKeys(uuid, crateId);
        setPlayerKeys(uuid, crateId, currentKeys + amount);
    }

    public boolean takePlayerKeys(UUID uuid, String crateId, int amount) {
        int currentKeys = getPlayerKeys(uuid, crateId);
        if (currentKeys >= amount) {
            setPlayerKeys(uuid, crateId, currentKeys - amount);
            return true;
        }
        return false;
    }

    public int getPlayerClaimCount(UUID uuid, String crateId, String rewardId) {
        String query = "SELECT claim_count FROM player_claims WHERE uuid = ? AND crate_id = ? AND reward_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, crateId);
            stmt.setString(3, rewardId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("claim_count");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player claim count", e);
        }
        
        return 0;
    }

    public void incrementPlayerClaimCount(UUID uuid, String crateId, String rewardId) {
        String query = "INSERT INTO player_claims (uuid, crate_id, reward_id, claim_count) VALUES (?, ?, ?, 1) " +
                      "ON CONFLICT(uuid, crate_id, reward_id) DO UPDATE SET claim_count = claim_count + 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, crateId);
            stmt.setString(3, rewardId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to increment player claim count", e);
        }
    }

    public int getGlobalClaimCount(String crateId, String rewardId) {
        String query = "SELECT claim_count FROM global_claims WHERE crate_id = ? AND reward_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, crateId);
            stmt.setString(2, rewardId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("claim_count");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get global claim count", e);
        }
        
        return 0;
    }

    public void incrementGlobalClaimCount(String crateId, String rewardId) {
        String query = "INSERT INTO global_claims (crate_id, reward_id, claim_count) VALUES (?, ?, 1) " +
                      "ON CONFLICT(crate_id, reward_id) DO UPDATE SET claim_count = claim_count + 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, crateId);
            stmt.setString(2, rewardId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to increment global claim count", e);
        }
    }

    public int getTotalPlayerClaimCount(UUID uuid, String crateId) {
        String query = "SELECT SUM(claim_count) as total FROM player_claims WHERE uuid = ? AND crate_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, crateId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get total player claim count", e);
        }
        
        return 0;
    }
}
