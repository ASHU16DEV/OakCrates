package dev.ashu16.oakcrates.listeners;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.CratePreviewGUI;
import dev.ashu16.oakcrates.gui.RewardSelectionGUI;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockInteractListener implements Listener {

    private final OakCrates plugin;
    private final Map<UUID, BlockBindSession> bindSessions;

    public BlockInteractListener(OakCrates plugin) {
        this.plugin = plugin;
        this.bindSessions = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        
        if (block == null) return;
        
        if (bindSessions.containsKey(player.getUniqueId())) {
            handleBindSession(event, player, block);
            return;
        }
        
        Crate crate = plugin.getCrateManager().getCrateAtLocation(block.getLocation());
        if (crate == null) return;
        
        event.setCancelled(true);
        
        if (isInteractableBlock(block.getType())) {
            event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        }
        
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            openPreviewGUI(player, crate, block.getLocation());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleRightClick(player, crate, block.getLocation());
        }
    }

    private boolean isInteractableBlock(Material material) {
        return material == Material.CHEST || 
               material == Material.TRAPPED_CHEST ||
               material == Material.ENDER_CHEST ||
               material == Material.BARREL ||
               material == Material.SHULKER_BOX ||
               material.name().endsWith("_SHULKER_BOX");
    }

    private void openPreviewGUI(Player player, Crate crate, Location location) {
        SoundUtil.playCrateSound(player, crate.getOpenSound());
        new CratePreviewGUI(plugin, player, crate).open();
    }

    private void handleRightClick(Player player, Crate crate, Location location) {
        if (!plugin.getKeyManager().hasKey(player, crate.getId())) {
            showNoKeyMessage(player, crate);
            return;
        }
        
        SoundUtil.playCrateSound(player, crate.getOpenSound());
        new RewardSelectionGUI(plugin, player, crate, location).open();
    }

    private void showNoKeyMessage(Player player, Crate crate) {
        SoundUtil.playNoKey(player);
        
        boolean titleEnabled = plugin.getConfigManager().getMessages().getBoolean("no-key-title.enabled", true);
        
        if (titleEnabled) {
            String title = plugin.getConfigManager().getMessages().getString("no-key-title.title", "&c&lNo Key Found");
            String subtitle = plugin.getConfigManager().getMessages().getString("no-key-title.subtitle", "&7Obtain a key to open this crate");
            int fadeIn = plugin.getConfigManager().getMessages().getInt("no-key-title.fade-in", 10);
            int stay = plugin.getConfigManager().getMessages().getInt("no-key-title.stay", 40);
            int fadeOut = plugin.getConfigManager().getMessages().getInt("no-key-title.fade-out", 10);
            
            MessageUtil.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
        
        String chatMessage = MessageUtil.getMessage("no-key-chat");
        MessageUtil.sendWithPrefix(player, chatMessage);
    }

    public void startBindSession(Player player, String crateId) {
        bindSessions.put(player.getUniqueId(), new BlockBindSession(crateId));
        
        Map<String, String> placeholders = MessageUtil.createPlaceholders(
            "%crate_name%", crateId
        );
        MessageUtil.sendMessage(player, "block-bind-mode", placeholders);
    }

    public void cancelBindSession(Player player) {
        if (bindSessions.remove(player.getUniqueId()) != null) {
            MessageUtil.sendMessage(player, "block-bind-cancelled");
        }
    }

    private void handleBindSession(PlayerInteractEvent event, Player player, Block block) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        event.setCancelled(true);
        
        BlockBindSession session = bindSessions.remove(player.getUniqueId());
        if (session == null) return;
        
        Location location = block.getLocation();
        
        Crate existingCrate = plugin.getCrateManager().getCrateAtLocation(location);
        if (existingCrate != null) {
            MessageUtil.sendMessage(player, "block-already-bound");
            return;
        }
        
        Crate crate = plugin.getCrateManager().getCrate(session.getCrateId());
        if (crate == null) {
            MessageUtil.sendMessage(player, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", session.getCrateId()));
            return;
        }
        
        crate.addBoundBlock(location);
        plugin.getCrateManager().saveCrate(crate);
        
        plugin.getHologramManager().respawnAllHolograms();
        
        Map<String, String> placeholders = MessageUtil.createPlaceholders(
            "%crate_name%", crate.getId(),
            "%crate_display_name%", ColorUtil.colorize(crate.getDisplayName())
        );
        MessageUtil.sendMessage(player, "block-bound", placeholders);
    }

    public boolean hasBindSession(Player player) {
        return bindSessions.containsKey(player.getUniqueId());
    }

    private static class BlockBindSession {
        private final String crateId;
        private final long startTime;

        public BlockBindSession(String crateId) {
            this.crateId = crateId;
            this.startTime = System.currentTimeMillis();
        }

        public String getCrateId() {
            return crateId;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > 60000;
        }
    }
}
