package dev.ashu16.oakcrates.listeners;

import dev.ashu16.oakcrates.OakCrates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private final OakCrates plugin;

    public PlayerJoinQuitListener(OakCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                plugin.getHologramManager().checkPlayerProximity(player);
            }
        }, 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        plugin.getHologramManager().removeAllHologramsForPlayer(player);
        
        plugin.getChatInputManager().cancelInput(player);
    }
}
