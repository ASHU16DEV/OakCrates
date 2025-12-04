package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.HologramSettings;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.PlaceholderUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HologramEditorGUI extends GUIHolder {

    private final Crate crate;
    private final HologramSettings settings;
    
    private static final int TOGGLE_SLOT = 10;
    private static final int HEIGHT_SLOT = 12;
    private static final int EDIT_LINES_SLOT = 14;
    private static final int ADD_LINE_SLOT = 16;
    private static final int PREVIEW_SLOT = 22;
    private static final int BACK_SLOT = 30;
    private static final int SAVE_SLOT = 31;
    private static final int CLOSE_SLOT = 32;

    public HologramEditorGUI(OakCrates plugin, Player player, Crate crate) {
        super(plugin, player);
        this.crate = crate;
        this.settings = crate.getHologramSettings();
        
        String title = MessageUtil.getMessage("admin-hologram-title");
        title = PlaceholderUtil.parse(title, player, crate);
        
        createInventory(title, 36);
    }

    @Override
    protected void setupInventory() {
        fillEmpty();
        
        inventory.setItem(TOGGLE_SLOT, createToggleButton());
        inventory.setItem(HEIGHT_SLOT, createHeightButton());
        inventory.setItem(EDIT_LINES_SLOT, createEditLinesButton());
        inventory.setItem(ADD_LINE_SLOT, createAddLineButton());
        inventory.setItem(PREVIEW_SLOT, createPreviewButton());
        
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(SAVE_SLOT, createSaveButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
    }

    private ItemStack createToggleButton() {
        Material material = settings.isEnabled() ? Material.LIME_DYE : Material.GRAY_DYE;
        String status = settings.isEnabled() ? "&aEnabled" : "&cDisabled";
        
        return new ItemBuilder(material)
                .name("&e&lHologram Status")
                .lore(Arrays.asList(
                    "&7Current: " + status,
                    "",
                    "&7Toggle hologram visibility",
                    "&7above crate blocks.",
                    "",
                    "&eClick to toggle"
                ))
                .glow(settings.isEnabled())
                .build();
    }

    private ItemStack createHeightButton() {
        return new ItemBuilder(Material.LADDER)
                .name("&e&lHologram Height")
                .lore(Arrays.asList(
                    "&7Current: &a" + settings.getHeight() + " blocks",
                    "",
                    "&7Height above the crate block.",
                    "",
                    "&aLeft-click: +0.5",
                    "&cRight-click: -0.5"
                ))
                .build();
    }

    private ItemStack createEditLinesButton() {
        List<String> lore = new ArrayList<>();
        lore.add("&7Current lines:");
        for (int i = 0; i < settings.getLines().size(); i++) {
            String line = settings.getLines().get(i);
            lore.add("&7" + (i + 1) + ". " + ColorUtil.colorize(line));
        }
        lore.add("");
        lore.add("&eClick to edit lines");
        
        return new ItemBuilder(Material.WRITABLE_BOOK)
                .name("&e&lEdit Hologram Lines")
                .lore(lore)
                .build();
    }

    private ItemStack createAddLineButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&lAdd New Line")
                .lore(Arrays.asList(
                    "&7Add a new line to the hologram.",
                    "",
                    "&7Placeholders:",
                    "&7- %player% - Player name",
                    "&7- %player_keys% - Keys for this crate",
                    "&7- %crate_name% - Crate ID",
                    "&7- %crate_display_name% - Display name",
                    "",
                    "&eClick to add line"
                ))
                .build();
    }

    private ItemStack createPreviewButton() {
        List<String> lore = new ArrayList<>();
        lore.add("&7Preview with your placeholders:");
        lore.add("");
        for (String line : settings.getLines()) {
            String parsed = PlaceholderUtil.parse(line, player, crate);
            lore.add(ColorUtil.colorize(parsed));
        }
        lore.add("");
        lore.add("&eClick to refresh holograms");
        
        return new ItemBuilder(Material.ENDER_EYE)
                .name("&d&lPreview Hologram")
                .lore(lore)
                .glow(true)
                .build();
    }

    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&lSave Changes")
                .lore(Arrays.asList(
                    "&7Click to save all changes",
                    "&7to hologram settings."
                ))
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        SoundUtil.playConfirm(player);
        
        switch (slot) {
            case TOGGLE_SLOT:
                settings.setEnabled(!settings.isEnabled());
                plugin.getCrateManager().saveCrate(crate);
                plugin.getHologramManager().respawnAllHolograms();
                refresh();
                break;
            case HEIGHT_SLOT:
                handleHeightEdit(clickType);
                break;
            case EDIT_LINES_SLOT:
                player.closeInventory();
                new LoreEditorGUI(plugin, player, crate, settings.getLines(),
                    newLines -> {
                        settings.setLines(newLines);
                        plugin.getCrateManager().saveCrate(crate);
                        plugin.getHologramManager().respawnAllHolograms();
                    },
                    () -> new HologramEditorGUI(plugin, player, crate).open()
                ).open();
                break;
            case ADD_LINE_SLOT:
                handleAddLine();
                break;
            case PREVIEW_SLOT:
                plugin.getHologramManager().respawnAllHolograms();
                MessageUtil.sendWithPrefix(player, "&aHolograms refreshed!");
                refresh();
                break;
            case BACK_SLOT:
                player.closeInventory();
                new CrateEditGUI(plugin, player, crate).open();
                break;
            case SAVE_SLOT:
                plugin.getCrateManager().saveCrate(crate);
                plugin.getHologramManager().respawnAllHolograms();
                MessageUtil.sendMessage(player, "edit-saved");
                break;
            case CLOSE_SLOT:
                player.closeInventory();
                break;
        }
    }

    private void handleHeightEdit(ClickType clickType) {
        double current = settings.getHeight();
        double newHeight;
        
        if (clickType == ClickType.LEFT) {
            newHeight = Math.min(10.0, current + 0.5);
        } else if (clickType == ClickType.RIGHT) {
            newHeight = Math.max(0.5, current - 0.5);
        } else {
            return;
        }
        
        settings.setHeight(newHeight);
        plugin.getCrateManager().saveCrate(crate);
        plugin.getHologramManager().respawnAllHolograms();
        refresh();
    }

    private void handleAddLine() {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&eEnter the new hologram line (supports color codes and placeholders):",
            input -> {
                settings.addLine(input);
                plugin.getCrateManager().saveCrate(crate);
                plugin.getHologramManager().respawnAllHolograms();
                MessageUtil.sendWithPrefix(player, "&aLine added!");
                new HologramEditorGUI(plugin, player, crate).open();
            },
            () -> new HologramEditorGUI(plugin, player, crate).open()
        );
    }
}
