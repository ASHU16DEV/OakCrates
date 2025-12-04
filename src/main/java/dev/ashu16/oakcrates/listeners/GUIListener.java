package dev.ashu16.oakcrates.listeners;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

public class GUIListener implements Listener {

    private final OakCrates plugin;

    public GUIListener(OakCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() == null) return;
        
        if (!(inventory.getHolder() instanceof GUIHolder)) return;
        
        event.setCancelled(true);
        
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(event.getView().getTopInventory())) return;
        
        Player player = (Player) event.getWhoClicked();
        GUIHolder holder = (GUIHolder) inventory.getHolder();
        
        holder.handleClick(player, event.getSlot(), event.getClick());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() == null) return;
        
        if (inventory.getHolder() instanceof GUIHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getDestination().getHolder() instanceof GUIHolder) {
            event.setCancelled(true);
        }
        if (event.getSource().getHolder() instanceof GUIHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() == null) return;
        
        if (inventory.getHolder() instanceof GUIHolder) {
            GUIHolder holder = (GUIHolder) inventory.getHolder();
            holder.onClose((Player) event.getPlayer());
        }
    }
}
