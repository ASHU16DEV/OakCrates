package dev.ashu16.oakcrates.gui;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class GUIHolder implements InventoryHolder {

    protected final OakCrates plugin;
    protected final Player player;
    protected Inventory inventory;

    public GUIHolder(OakCrates plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    protected void createInventory(String title, int size) {
        this.inventory = Bukkit.createInventory(this, size, ColorUtil.colorize(title));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        setupInventory();
        player.openInventory(inventory);
    }

    protected abstract void setupInventory();

    public abstract void handleClick(Player player, int slot, ClickType clickType);

    public void onClose(Player player) {
    }

    protected void fillBorders() {
        ItemStack filler = getFiller();
        int size = inventory.getSize();
        int rows = size / 9;

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, filler);
        }

        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, filler);
        }

        for (int i = 1; i < rows - 1; i++) {
            inventory.setItem(i * 9, filler);
            inventory.setItem(i * 9 + 8, filler);
        }
    }

    protected void fillEmpty() {
        ItemStack filler = getFiller();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    protected ItemStack getFiller() {
        String materialName = plugin.getConfigManager().getConfig().getString("gui.filler-item.material", "BLACK_STAINED_GLASS_PANE");
        String name = plugin.getConfigManager().getConfig().getString("gui.filler-item.name", " ");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.BLACK_STAINED_GLASS_PANE;
        }
        
        return new ItemBuilder(material)
                .name(name)
                .build();
    }

    protected ItemStack getBackButton() {
        String materialName = plugin.getConfigManager().getConfig().getString("gui.back-button.material", "ARROW");
        String name = plugin.getConfigManager().getConfig().getString("gui.back-button.name", "&cBack");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.ARROW;
        }
        
        return new ItemBuilder(material)
                .name(name)
                .build();
    }

    protected ItemStack getCloseButton() {
        String materialName = plugin.getConfigManager().getConfig().getString("gui.close-button.material", "BARRIER");
        String name = plugin.getConfigManager().getConfig().getString("gui.close-button.name", "&cClose");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.BARRIER;
        }
        
        return new ItemBuilder(material)
                .name(name)
                .build();
    }

    protected ItemStack getNextPageButton() {
        String materialName = plugin.getConfigManager().getConfig().getString("gui.next-page.material", "ARROW");
        String name = plugin.getConfigManager().getConfig().getString("gui.next-page.name", "&aNext Page");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.ARROW;
        }
        
        return new ItemBuilder(material)
                .name(name)
                .build();
    }

    protected ItemStack getPrevPageButton() {
        String materialName = plugin.getConfigManager().getConfig().getString("gui.prev-page.material", "ARROW");
        String name = plugin.getConfigManager().getConfig().getString("gui.prev-page.name", "&aPrevious Page");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.ARROW;
        }
        
        return new ItemBuilder(material)
                .name(name)
                .build();
    }

    protected void refresh() {
        inventory.clear();
        setupInventory();
    }

    protected boolean isFillerSlot(int slot) {
        if (slot < 0 || slot >= inventory.getSize()) return true;
        
        ItemStack item = inventory.getItem(slot);
        if (item == null || item.getType() == Material.AIR) return true;
        
        ItemStack filler = getFiller();
        if (item.getType() != filler.getType()) return false;
        
        if (item.hasItemMeta() && filler.hasItemMeta()) {
            String itemName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
            String fillerName = filler.getItemMeta().hasDisplayName() ? filler.getItemMeta().getDisplayName() : "";
            return itemName.equals(fillerName);
        }
        
        return true;
    }
}
