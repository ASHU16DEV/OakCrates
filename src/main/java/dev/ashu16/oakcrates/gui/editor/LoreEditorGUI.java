package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class LoreEditorGUI extends GUIHolder {

    private final Crate crate;
    private final List<String> lines;
    private final Consumer<List<String>> onSave;
    private final Runnable onBack;
    private int page;
    
    private static final int ITEMS_PER_PAGE = 28;
    private static final int ADD_LINE_SLOT = 49;
    private static final int CLEAR_ALL_SLOT = 47;
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int BACK_SLOT = 48;
    private static final int SAVE_SLOT = 50;

    public LoreEditorGUI(OakCrates plugin, Player player, Crate crate, List<String> lines, 
                         Consumer<List<String>> onSave, Runnable onBack) {
        super(plugin, player);
        this.crate = crate;
        this.lines = new ArrayList<>(lines);
        this.onSave = onSave;
        this.onBack = onBack;
        this.page = 0;
        
        createInventory(MessageUtil.getMessage("admin-lore-title"), 54);
    }

    @Override
    protected void setupInventory() {
        fillBorders();
        
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, lines.size());
        
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot % 9 == 0) slot++;
            if (slot % 9 == 8) slot += 2;
            if (slot >= 44) break;
            
            inventory.setItem(slot, createLineItem(i, lines.get(i)));
            slot++;
        }
        
        inventory.setItem(ADD_LINE_SLOT, createAddLineButton());
        inventory.setItem(CLEAR_ALL_SLOT, createClearAllButton());
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(SAVE_SLOT, createSaveButton());
        
        if (page > 0) {
            inventory.setItem(PREV_PAGE_SLOT, getPrevPageButton());
        }
        
        int totalPages = (int) Math.ceil((double) lines.size() / ITEMS_PER_PAGE);
        if (page < totalPages - 1) {
            inventory.setItem(NEXT_PAGE_SLOT, getNextPageButton());
        }
    }

    private ItemStack createLineItem(int index, String line) {
        return new ItemBuilder(Material.PAPER)
                .name("&e&lLine " + (index + 1))
                .lore(Arrays.asList(
                    "&7Content:",
                    ColorUtil.colorize(line),
                    "",
                    "&aLeft-click: Edit line",
                    "&6Middle-click: Move up",
                    "&bShift-left: Move down",
                    "&cRight-click: Delete line"
                ))
                .build();
    }

    private ItemStack createAddLineButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&lAdd New Line")
                .lore(Arrays.asList(
                    "&7Click to add a new line.",
                    "",
                    "&7Current lines: &a" + lines.size()
                ))
                .build();
    }

    private ItemStack createClearAllButton() {
        return new ItemBuilder(Material.TNT)
                .name("&c&lClear All Lines")
                .lore(Arrays.asList(
                    "&7Click to remove all lines.",
                    "",
                    "&c&lWarning: This cannot be undone!"
                ))
                .build();
    }

    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&lSave & Close")
                .lore(Arrays.asList(
                    "&7Save changes and return.",
                    "",
                    "&7Total lines: &a" + lines.size()
                ))
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        if (isFillerSlot(slot)) return;
        
        SoundUtil.playConfirm(player);
        
        if (slot == ADD_LINE_SLOT) {
            handleAddLine();
            return;
        }
        
        if (slot == CLEAR_ALL_SLOT) {
            lines.clear();
            refresh();
            return;
        }
        
        if (slot == BACK_SLOT) {
            player.closeInventory();
            if (onBack != null) {
                onBack.run();
            }
            return;
        }
        
        if (slot == SAVE_SLOT) {
            player.closeInventory();
            if (onSave != null) {
                onSave.accept(new ArrayList<>(lines));
            }
            MessageUtil.sendMessage(player, "edit-saved");
            if (onBack != null) {
                onBack.run();
            }
            return;
        }
        
        if (slot == PREV_PAGE_SLOT && page > 0) {
            page--;
            refresh();
            return;
        }
        
        int totalPages = (int) Math.ceil((double) lines.size() / ITEMS_PER_PAGE);
        if (slot == NEXT_PAGE_SLOT && page < totalPages - 1) {
            page++;
            refresh();
            return;
        }
        
        int lineIndex = getLineIndexAtSlot(slot);
        if (lineIndex >= 0 && lineIndex < lines.size()) {
            handleLineClick(lineIndex, clickType);
        }
    }

    private void handleAddLine() {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&eEnter the new line text (supports color codes):",
            input -> {
                lines.add(input);
                new LoreEditorGUI(plugin, player, crate, lines, onSave, onBack).open();
            },
            () -> new LoreEditorGUI(plugin, player, crate, lines, onSave, onBack).open()
        );
    }

    private void handleLineClick(int index, ClickType clickType) {
        if (clickType == ClickType.LEFT) {
            player.closeInventory();
            plugin.getChatInputManager().requestInput(player,
                "&eEnter the new text for line " + (index + 1) + ":",
                input -> {
                    lines.set(index, input);
                    new LoreEditorGUI(plugin, player, crate, lines, onSave, onBack).open();
                },
                () -> new LoreEditorGUI(plugin, player, crate, lines, onSave, onBack).open()
            );
        } else if (clickType == ClickType.RIGHT) {
            lines.remove(index);
            refresh();
        } else if (clickType == ClickType.MIDDLE && index > 0) {
            String line = lines.remove(index);
            lines.add(index - 1, line);
            refresh();
        } else if (clickType == ClickType.SHIFT_LEFT && index < lines.size() - 1) {
            String line = lines.remove(index);
            lines.add(index + 1, line);
            refresh();
        }
    }

    private int getLineIndexAtSlot(int clickedSlot) {
        int startIndex = page * ITEMS_PER_PAGE;
        int slot = 10;
        
        for (int i = startIndex; i < lines.size() && slot < 44; i++) {
            if (slot % 9 == 0) slot++;
            if (slot % 9 == 8) slot += 2;
            
            if (slot == clickedSlot) {
                return i;
            }
            slot++;
        }
        return -1;
    }
}
