package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.Reward;
import dev.ashu16.oakcrates.models.RewardItem;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RewardItemsEditorGUI extends GUIHolder {

    private final Crate crate;
    private final Reward reward;
    private int page;
    
    private static final int ITEMS_PER_PAGE = 21;
    private static final int INFO_SLOT = 4;
    private static final int ADD_ITEM_SLOT = 48;
    private static final int CLEAR_ALL_SLOT = 50;
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int BACK_SLOT = 47;
    private static final int CLOSE_SLOT = 51;

    public RewardItemsEditorGUI(OakCrates plugin, Player player, Crate crate, Reward reward, int page) {
        super(plugin, player);
        this.crate = crate;
        this.reward = reward;
        this.page = page;
        
        createInventory("&8Reward Items: " + reward.getId(), 54);
    }

    @Override
    protected void setupInventory() {
        fillBorders();
        
        inventory.setItem(INFO_SLOT, createInfoItem());
        
        List<RewardItem> items = reward.getItems();
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());
        
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int slotIndex = 0;
        
        for (int i = startIndex; i < endIndex && slotIndex < slots.length; i++) {
            inventory.setItem(slots[slotIndex], createItemDisplay(i, items.get(i)));
            slotIndex++;
        }
        
        inventory.setItem(ADD_ITEM_SLOT, createAddItemButton());
        inventory.setItem(CLEAR_ALL_SLOT, createClearAllButton());
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
        
        if (page > 0) {
            inventory.setItem(PREV_PAGE_SLOT, getPrevPageButton());
        } else {
            inventory.setItem(PREV_PAGE_SLOT, createDisabledButton("&7No Previous Page"));
        }
        
        int totalPages = Math.max(1, (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE));
        if (page < totalPages - 1) {
            inventory.setItem(NEXT_PAGE_SLOT, getNextPageButton());
        } else {
            inventory.setItem(NEXT_PAGE_SLOT, createDisabledButton("&7No Next Page"));
        }
    }

    private ItemStack createInfoItem() {
        int totalPages = Math.max(1, (int) Math.ceil((double) reward.getItems().size() / ITEMS_PER_PAGE));
        return new ItemBuilder(Material.BOOK)
                .name("&e&lReward Items Overview")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Reward: &f" + reward.getId(),
                    "&7Total Items: &a" + reward.getItems().size(),
                    "&7Page: &a" + (page + 1) + "&7/&a" + totalPages,
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&7These items are given to",
                    "&7the player when they claim",
                    "&7this reward."
                ))
                .build();
    }

    private ItemStack createItemDisplay(int index, RewardItem rewardItem) {
        ItemStack display = rewardItem.build();
        
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(ColorUtil.colorize("&8━━━━━━━━━━━━━━━━━━━━"));
            lore.add(ColorUtil.colorize("&7Item #" + (index + 1)));
            lore.add(ColorUtil.colorize("&7Amount: &f" + rewardItem.getAmount()));
            lore.add(ColorUtil.colorize("&8━━━━━━━━━━━━━━━━━━━━"));
            lore.add(ColorUtil.colorize("&a▸ Left-click to edit"));
            lore.add(ColorUtil.colorize("&c▸ Right-click to delete"));
            lore.add(ColorUtil.colorize("&e▸ Middle-click to copy"));
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }

    private ItemStack createAddItemButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&l✚ Add Item from Hand")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Hold an item in your hand",
                    "&7and click here to add it",
                    "&7as a reward item.",
                    "",
                    "&7Current items: &a" + reward.getItems().size(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to add item"
                ))
                .glow(true)
                .build();
    }

    private ItemStack createClearAllButton() {
        return new ItemBuilder(Material.TNT)
                .name("&c&l✘ Clear All Items")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Remove all items from",
                    "&7this reward.",
                    "",
                    "&7Current items: &c" + reward.getItems().size(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&c▸ Shift-click to clear all"
                ))
                .build();
    }

    private ItemStack createDisabledButton(String name) {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(name)
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        if (isFillerSlot(slot)) return;
        
        SoundUtil.playConfirm(player);
        
        if (slot == ADD_ITEM_SLOT) {
            handleAddItem();
            return;
        }
        
        if (slot == CLEAR_ALL_SLOT) {
            if (clickType.isShiftClick()) {
                reward.getItems().clear();
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&c✔ All items cleared!");
                page = 0;
                refresh();
            } else {
                MessageUtil.sendWithPrefix(player, "&e⚠ Shift-click to confirm clearing all items!");
            }
            return;
        }
        
        if (slot == BACK_SLOT) {
            player.closeInventory();
            new RewardEditGUI(plugin, player, crate, reward).open();
            return;
        }
        
        if (slot == CLOSE_SLOT) {
            player.closeInventory();
            return;
        }
        
        if (slot == PREV_PAGE_SLOT && page > 0) {
            page--;
            refresh();
            return;
        }
        
        int totalPages = (int) Math.ceil((double) reward.getItems().size() / ITEMS_PER_PAGE);
        if (slot == NEXT_PAGE_SLOT && page < totalPages - 1) {
            page++;
            refresh();
            return;
        }
        
        int itemIndex = getItemIndexAtSlot(slot);
        if (itemIndex >= 0 && itemIndex < reward.getItems().size()) {
            if (clickType == ClickType.RIGHT) {
                reward.removeItem(itemIndex);
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&c✔ Item removed!");
                refresh();
            } else if (clickType == ClickType.LEFT) {
                handleEditItem(itemIndex);
            } else if (clickType == ClickType.MIDDLE) {
                RewardItem original = reward.getItems().get(itemIndex);
                ItemStack copy = original.build();
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(copy);
                    MessageUtil.sendWithPrefix(player, "&a✔ Item copied to your inventory!");
                } else {
                    MessageUtil.sendWithPrefix(player, "&c✘ Your inventory is full!");
                }
            }
        }
    }

    private void handleAddItem() {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        
        if (handItem == null || handItem.getType() == Material.AIR) {
            MessageUtil.sendWithPrefix(player, "&c✘ Hold an item in your hand to add it!");
            SoundUtil.playError(player);
            return;
        }
        
        RewardItem rewardItem = RewardItem.fromItemStack(handItem);
        reward.addItem(rewardItem);
        plugin.getCrateManager().saveCrate(crate);
        
        MessageUtil.sendWithPrefix(player, "&a✔ Item added to reward! (" + handItem.getType().name() + " x" + handItem.getAmount() + ")");
        SoundUtil.playSuccess(player);
        refresh();
    }

    private void handleEditItem(int index) {
        player.closeInventory();
        RewardItem item = reward.getItems().get(index);
        
        plugin.getChatInputManager().requestInput(player,
            "&e&lEnter new amount for this item (1-64):",
            input -> {
                try {
                    int amount = Integer.parseInt(input);
                    if (amount >= 1 && amount <= 64) {
                        item.setAmount(amount);
                        plugin.getCrateManager().saveCrate(crate);
                        MessageUtil.sendWithPrefix(player, "&a✔ Amount updated to " + amount);
                    } else {
                        MessageUtil.sendWithPrefix(player, "&c✘ Amount must be between 1 and 64!");
                    }
                } catch (NumberFormatException e) {
                    MessageUtil.sendWithPrefix(player, "&c✘ Please enter a valid number!");
                }
                new RewardItemsEditorGUI(plugin, player, crate, reward, page).open();
            },
            () -> new RewardItemsEditorGUI(plugin, player, crate, reward, page).open()
        );
    }

    private int getItemIndexAtSlot(int clickedSlot) {
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int startIndex = page * ITEMS_PER_PAGE;
        
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == clickedSlot) {
                int itemIndex = startIndex + i;
                if (itemIndex < reward.getItems().size()) {
                    return itemIndex;
                }
            }
        }
        return -1;
    }
}
