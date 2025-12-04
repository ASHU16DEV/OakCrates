package dev.ashu16.oakcrates.hologram;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.HologramSettings;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class HologramManager {

    private final OakCrates plugin;
    private final Map<String, Map<UUID, List<ArmorStand>>> playerHolograms;
    private final double LINE_SPACING = 0.25;

    public HologramManager(OakCrates plugin) {
        this.plugin = plugin;
        this.playerHolograms = new HashMap<>();
    }

    public void createHologram(Player player, Location crateLocation, Crate crate) {
        if (!plugin.getConfigManager().getConfig().getBoolean("hologram.enabled", true)) {
            return;
        }
        
        HologramSettings settings = crate.getHologramSettings();
        if (!settings.isEnabled() || settings.getLines().isEmpty()) {
            return;
        }

        String locationKey = getLocationKey(crateLocation);
        removeHologram(player, locationKey);

        List<String> lines = new ArrayList<>(settings.getLines());
        Collections.reverse(lines);

        Location hologramLoc = crateLocation.clone().add(0.5, settings.getHeight(), 0.5);
        List<ArmorStand> stands = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            line = PlaceholderUtil.parse(line, player, crate);
            line = ColorUtil.colorize(line);

            Location lineLoc = hologramLoc.clone().add(0, i * LINE_SPACING, 0);
            ArmorStand stand = createArmorStand(lineLoc, line);
            stands.add(stand);
        }

        playerHolograms.computeIfAbsent(locationKey, k -> new HashMap<>())
                      .put(player.getUniqueId(), stands);
    }

    private ArmorStand createArmorStand(Location location, String text) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(text);
        stand.setCustomNameVisible(true);
        stand.setMarker(true);
        stand.setSmall(true);
        stand.setInvulnerable(true);
        stand.setCanPickupItems(false);
        stand.setCollidable(false);
        
        return stand;
    }

    public void removeHologram(Player player, String locationKey) {
        Map<UUID, List<ArmorStand>> locationHolograms = playerHolograms.get(locationKey);
        if (locationHolograms == null) return;

        List<ArmorStand> stands = locationHolograms.remove(player.getUniqueId());
        if (stands != null) {
            for (ArmorStand stand : stands) {
                if (stand != null && !stand.isDead()) {
                    stand.remove();
                }
            }
        }

        if (locationHolograms.isEmpty()) {
            playerHolograms.remove(locationKey);
        }
    }

    public void removeAllHologramsForPlayer(Player player) {
        for (Map.Entry<String, Map<UUID, List<ArmorStand>>> entry : new HashMap<>(playerHolograms).entrySet()) {
            removeHologram(player, entry.getKey());
        }
    }

    public void removeHologramsForCrate(String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return;

        for (Location loc : plugin.getCrateManager().getBoundLocations(crateId)) {
            String locationKey = getLocationKey(loc);
            Map<UUID, List<ArmorStand>> locationHolograms = playerHolograms.remove(locationKey);
            if (locationHolograms != null) {
                for (List<ArmorStand> stands : locationHolograms.values()) {
                    for (ArmorStand stand : stands) {
                        if (stand != null && !stand.isDead()) {
                            stand.remove();
                        }
                    }
                }
            }
        }
    }

    public void removeAllHolograms() {
        for (Map<UUID, List<ArmorStand>> locationHolograms : playerHolograms.values()) {
            for (List<ArmorStand> stands : locationHolograms.values()) {
                for (ArmorStand stand : stands) {
                    if (stand != null && !stand.isDead()) {
                        stand.remove();
                    }
                }
            }
        }
        playerHolograms.clear();
    }

    public void updateHologram(Player player, Location crateLocation, Crate crate) {
        String locationKey = getLocationKey(crateLocation);
        Map<UUID, List<ArmorStand>> locationHolograms = playerHolograms.get(locationKey);
        
        if (locationHolograms == null || !locationHolograms.containsKey(player.getUniqueId())) {
            createHologram(player, crateLocation, crate);
            return;
        }

        List<ArmorStand> stands = locationHolograms.get(player.getUniqueId());
        HologramSettings settings = crate.getHologramSettings();
        List<String> lines = new ArrayList<>(settings.getLines());
        Collections.reverse(lines);

        if (stands.size() != lines.size()) {
            removeHologram(player, locationKey);
            createHologram(player, crateLocation, crate);
            return;
        }

        for (int i = 0; i < stands.size(); i++) {
            ArmorStand stand = stands.get(i);
            if (stand != null && !stand.isDead()) {
                String line = lines.get(i);
                line = PlaceholderUtil.parse(line, player, crate);
                line = ColorUtil.colorize(line);
                stand.setCustomName(line);
            }
        }
    }

    public void updateAllHolograms() {
        int viewDistance = plugin.getConfigManager().getConfig().getInt("hologram.view-distance", 10);

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Crate crate : plugin.getCrateManager().getAllCrates().values()) {
                if (!crate.getHologramSettings().isEnabled()) continue;

                for (Location loc : plugin.getCrateManager().getBoundLocations(crate.getId())) {
                    if (!player.getWorld().equals(loc.getWorld())) continue;

                    double distance = player.getLocation().distance(loc);
                    String locationKey = getLocationKey(loc);

                    if (distance <= viewDistance) {
                        if (hasHologram(player, locationKey)) {
                            updateHologram(player, loc, crate);
                        } else {
                            createHologram(player, loc, crate);
                        }
                    } else {
                        removeHologram(player, locationKey);
                    }
                }
            }
        }
    }

    public void respawnAllHolograms() {
        removeAllHolograms();
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateAllHolograms();
        }, 5L);
    }

    private boolean hasHologram(Player player, String locationKey) {
        Map<UUID, List<ArmorStand>> locationHolograms = playerHolograms.get(locationKey);
        return locationHolograms != null && locationHolograms.containsKey(player.getUniqueId());
    }

    private String getLocationKey(Location location) {
        return location.getWorld().getName() + ":" + 
               location.getBlockX() + ":" + 
               location.getBlockY() + ":" + 
               location.getBlockZ();
    }

    public void checkPlayerProximity(Player player) {
        int viewDistance = plugin.getConfigManager().getConfig().getInt("hologram.view-distance", 10);

        for (Crate crate : plugin.getCrateManager().getAllCrates().values()) {
            if (!crate.getHologramSettings().isEnabled()) continue;

            for (Location loc : plugin.getCrateManager().getBoundLocations(crate.getId())) {
                if (!player.getWorld().equals(loc.getWorld())) continue;

                double distance = player.getLocation().distance(loc);
                String locationKey = getLocationKey(loc);

                if (distance <= viewDistance) {
                    if (!hasHologram(player, locationKey)) {
                        createHologram(player, loc, crate);
                    }
                } else {
                    removeHologram(player, locationKey);
                }
            }
        }
    }
}
