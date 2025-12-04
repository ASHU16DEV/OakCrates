package dev.ashu16.oakcrates.listeners;

import dev.ashu16.oakcrates.OakCrates;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatInputListener implements Listener {

    private final OakCrates plugin;

    public ChatInputListener(OakCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getChatInputManager().hasPendingInput(player)) {
            return;
        }
        
        event.setCancelled(true);
        
        String message = event.getMessage();
        
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getChatInputManager().handleInput(player, message);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getChatInputManager().cancelInput(event.getPlayer());
    }
}
