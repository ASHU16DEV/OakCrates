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

public class RewardEditGUI extends GUIHolder {

    private final Crate crate;
    private final Reward reward;
    
    private static final int INFO_SLOT = 4;
    private static final int DISPLAY_ITEM_SLOT = 10;
    private static final int DISPLAY_NAME_SLOT = 11;
    private static final int SLOT_SETTING_SLOT = 12;
    private static final int PLAYER_LIMIT_SLOT = 14;
    private static final int GLOBAL_LIMIT_SLOT = 15;
    private static final int PERMISSION_SLOT = 16;
    private static final int COMMANDS_SLOT = 28;
    private static final int ITEMS_SLOT = 30;
    private static final int LORE_SLOT = 32;
    private static final int BACK_SLOT = 38;
    private static final int SAVE_SLOT = 40;
    private static final int CLOSE_SLOT = 42;

    public RewardEditGUI(OakCrates plugin, Player player, Crate crate, Reward reward) {
        super(plugin, player);
        this.crate = crate;
        this.reward = reward;
        
        createInventory("&8Edit Reward: " + reward.getId(), 45);
    }

    @Override
    protected void setupInventory() {
        fillEmpty();
        
        inventory.setItem(INFO_SLOT, createInfoItem());
        inventory.setItem(DISPLAY_ITEM_SLOT, createDisplayItemButton());
        inventory.setItem(DISPLAY_NAME_SLOT, createDisplayNameButton());
        inventory.setItem(SLOT_SETTING_SLOT, createSlotSettingButton());
        inventory.setItem(PLAYER_LIMIT_SLOT, createPlayerLimitButton());
        inventory.setItem(GLOBAL_LIMIT_SLOT, createGlobalLimitButton());
        inventory.setItem(PERMISSION_SLOT, createPermissionButton());
        inventory.setItem(COMMANDS_SLOT, createCommandsButton());
        inventory.setItem(ITEMS_SLOT, createItemsButton());
        inventory.setItem(LORE_SLOT, createLoreButton());
        
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(SAVE_SLOT, createSaveButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
    }

    private ItemStack createInfoItem() {
        String displayName = reward.getDisplayName();
        return new ItemBuilder(Material.BOOK)
                .name("&e&lReward Information")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7ID: &f" + reward.getId(),
                    "&7Display: " + ColorUtil.colorize(displayName),
                    "&7Slot: &a" + reward.getSlot(),
                    "&7Items: &a" + reward.getItems().size(),
                    "&7Commands: &a" + reward.getCommands().size(),
                    "&8━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    private ItemStack createDisplayItemButton() {
        ItemStack display = reward.getDisplayItem();
        ItemStack button;
        
        if (display == null || display.getType() == Material.AIR) {
            button = new ItemBuilder(Material.ITEM_FRAME)
                    .name("&e&l⬚ Set Display Item")
                    .lore(Arrays.asList(
                        "&8━━━━━━━━━━━━━━━━━━━━",
                        "&7No display item set.",
                        "&7Hold an item and click",
                        "&7to set it as display.",
                        "&8━━━━━━━━━━━━━━━━━━━━",
                        "",
                        "&a▸ Click to set from hand"
                    ))
                    .build();
        } else {
            button = display.clone();
            ItemMeta meta = button.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add("");
                lore.add(ColorUtil.colorize("&8━━━━━━━━━━━━━━━━━━━━"));
                lore.add(ColorUtil.colorize("&a▸ Click to change"));
                lore.add(ColorUtil.colorize("&c▸ Shift-click to clear"));
                meta.setLore(lore);
                button.setItemMeta(meta);
            }
        }
        
        return button;
    }

    private ItemStack createDisplayNameButton() {
        String displayName = reward.getDisplayName();
        return new ItemBuilder(Material.NAME_TAG)
                .name("&e&l✎ Display Name")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: " + ColorUtil.colorize(displayName),
                    "",
                    "&7The name shown to players",
                    "&7in the crate GUI.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to change"
                ))
                .build();
    }

    private ItemStack createSlotSettingButton() {
        return new ItemBuilder(Material.COMPASS)
                .name("&e&l⊞ GUI Slot")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current slot: &a" + reward.getSlot(),
                    "",
                    "&7Position in the crate",
                    "&7reward selection GUI.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: +1",
                    "&c▸ Right-click: -1",
                    "&e▸ Middle-click: Enter number"
                ))
                .build();
    }

    private ItemStack createPlayerLimitButton() {
        String limit = reward.getPerPlayerClaimLimit() == 0 ? "&aUnlimited" : "&e" + reward.getPerPlayerClaimLimit();
        return new ItemBuilder(Material.PLAYER_HEAD)
                .name("&e&l⚑ Player Claim Limit")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: " + limit,
                    "",
                    "&7Max claims per player.",
                    "&70 = Unlimited",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: +1",
                    "&c▸ Right-click: -1",
                    "&e▸ Middle-click: Enter number"
                ))
                .build();
    }

    private ItemStack createGlobalLimitButton() {
        String limit = reward.getGlobalClaimLimit() == 0 ? "&aUnlimited" : "&e" + reward.getGlobalClaimLimit();
        return new ItemBuilder(Material.ENDER_CHEST)
                .name("&e&l⚐ Global Claim Limit")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: " + limit,
                    "",
                    "&7Max total claims for",
                    "&7this reward globally.",
                    "&70 = Unlimited",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: +1",
                    "&c▸ Right-click: -1",
                    "&e▸ Middle-click: Enter number"
                ))
                .build();
    }

    private ItemStack createCommandsButton() {
        return new ItemBuilder(Material.COMMAND_BLOCK)
                .name("&e&l⌘ Edit Commands")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Commands: &a" + reward.getCommands().size(),
                    "",
                    "&7Commands run when claimed.",
                    "&7Use %player% for player name.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit commands"
                ))
                .glow(reward.hasCommands())
                .build();
    }

    private ItemStack createItemsButton() {
        return new ItemBuilder(Material.CHEST)
                .name("&e&l⚔ Edit Reward Items")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Items: &a" + reward.getItems().size(),
                    "",
                    "&7Items given to player",
                    "&7when this reward is claimed.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit items"
                ))
                .glow(reward.hasItems())
                .build();
    }

    private ItemStack createLoreButton() {
        ItemStack display = reward.getDisplayItem();
        int loreLines = 0;
        if (display != null && display.hasItemMeta() && display.getItemMeta().hasLore()) {
            loreLines = display.getItemMeta().getLore().size();
        }
        
        return new ItemBuilder(Material.WRITABLE_BOOK)
                .name("&e&l≡ Edit Lore")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current lines: &a" + loreLines,
                    "",
                    "&7Edit the lore/description",
                    "&7of the display item.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit lore"
                ))
                .build();
    }

    private ItemStack createPermissionButton() {
        String perm = reward.getPermissionRequired().isEmpty() ? "&aNone" : "&e" + reward.getPermissionRequired();
        return new ItemBuilder(Material.PAPER)
                .name("&e&l⚿ Required Permission")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: " + perm,
                    "",
                    "&7Permission needed to",
                    "&7claim this reward.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to change",
                    "&c▸ Right-click to clear"
                ))
                .glow(reward.hasPermission())
                .build();
    }

    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&l✔ Save Changes")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Click to save all",
                    "&7changes to this reward.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to save"
                ))
                .glow(true)
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        if (isFillerSlot(slot)) return;
        
        SoundUtil.playConfirm(player);
        
        switch (slot) {
            case DISPLAY_ITEM_SLOT:
                handleDisplayItemEdit(clickType);
                break;
            case DISPLAY_NAME_SLOT:
                handleDisplayNameEdit();
                break;
            case SLOT_SETTING_SLOT:
                handleSlotEdit(clickType);
                break;
            case PLAYER_LIMIT_SLOT:
                handlePlayerLimitEdit(clickType);
                break;
            case GLOBAL_LIMIT_SLOT:
                handleGlobalLimitEdit(clickType);
                break;
            case COMMANDS_SLOT:
                player.closeInventory();
                new CommandsEditorGUI(plugin, player, crate, reward, 0).open();
                break;
            case ITEMS_SLOT:
                player.closeInventory();
                new RewardItemsEditorGUI(plugin, player, crate, reward, 0).open();
                break;
            case LORE_SLOT:
                handleLoreEdit();
                break;
            case PERMISSION_SLOT:
                handlePermissionEdit(clickType);
                break;
            case BACK_SLOT:
                player.closeInventory();
                new RewardsEditorGUI(plugin, player, crate, 0).open();
                break;
            case SAVE_SLOT:
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&a✔ Reward saved successfully!");
                SoundUtil.playSuccess(player);
                break;
            case CLOSE_SLOT:
                player.closeInventory();
                break;
        }
    }

    private void handleDisplayItemEdit(ClickType clickType) {
        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
            reward.setDisplayItem(null);
            plugin.getCrateManager().saveCrate(crate);
            MessageUtil.sendWithPrefix(player, "&c✔ Display item cleared!");
            refresh();
            return;
        }
        
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem != null && handItem.getType() != Material.AIR) {
            reward.setDisplayItem(handItem.clone());
            plugin.getCrateManager().saveCrate(crate);
            MessageUtil.sendWithPrefix(player, "&a✔ Display item set from your hand!");
            refresh();
        } else {
            MessageUtil.sendWithPrefix(player, "&c✘ Hold an item in your hand to set it!");
        }
    }

    private void handleDisplayNameEdit() {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&e&lEnter the new display name (supports color codes):",
            input -> {
                ItemStack display = reward.getDisplayItem();
                if (display == null || display.getType() == Material.AIR) {
                    display = new ItemBuilder(Material.CHEST).build();
                }
                
                ItemMeta meta = display.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ColorUtil.colorize(input));
                    display.setItemMeta(meta);
                }
                reward.setDisplayItem(display);
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&a✔ Display name updated to: " + ColorUtil.colorize(input));
                new RewardEditGUI(plugin, player, crate, reward).open();
            },
            () -> new RewardEditGUI(plugin, player, crate, reward).open()
        );
    }

    private void handleSlotEdit(ClickType clickType) {
        int current = reward.getSlot();
        int maxSlot = crate.getGuiSize() - 1;
        
        if (clickType == ClickType.LEFT) {
            reward.setSlot(Math.min(maxSlot, current + 1));
            plugin.getCrateManager().saveCrate(crate);
            refresh();
        } else if (clickType == ClickType.RIGHT) {
            reward.setSlot(Math.max(0, current - 1));
            plugin.getCrateManager().saveCrate(crate);
            refresh();
        } else if (clickType == ClickType.MIDDLE) {
            player.closeInventory();
            plugin.getChatInputManager().requestInput(player,
                "&e&lEnter the slot number (0-" + maxSlot + "):",
                input -> {
                    try {
                        int newSlot = Integer.parseInt(input);
                        if (newSlot >= 0 && newSlot <= maxSlot) {
                            reward.setSlot(newSlot);
                            plugin.getCrateManager().saveCrate(crate);
                            MessageUtil.sendWithPrefix(player, "&a✔ Slot updated to " + newSlot);
                        } else {
                            MessageUtil.sendWithPrefix(player, "&c✘ Invalid slot number!");
                        }
                    } catch (NumberFormatException e) {
                        MessageUtil.sendWithPrefix(player, "&c✘ Please enter a valid number!");
                    }
                    new RewardEditGUI(plugin, player, crate, reward).open();
                },
                () -> new RewardEditGUI(plugin, player, crate, reward).open()
            );
        }
    }

    private void handlePlayerLimitEdit(ClickType clickType) {
        int current = reward.getPerPlayerClaimLimit();
        
        if (clickType == ClickType.LEFT) {
            reward.setPerPlayerClaimLimit(current + 1);
            plugin.getCrateManager().saveCrate(crate);
            refresh();
        } else if (clickType == ClickType.RIGHT) {
            reward.setPerPlayerClaimLimit(Math.max(0, current - 1));
            plugin.getCrateManager().saveCrate(crate);
            refresh();
        } else if (clickType == ClickType.MIDDLE) {
            player.closeInventory();
            plugin.getChatInputManager().requestInput(player,
                "&e&lEnter the player claim limit (0 = unlimited):",
                input -> {
                    try {
                        int limit = Integer.parseInt(input);
                        reward.setPerPlayerClaimLimit(Math.max(0, limit));
                        plugin.getCrateManager().saveCrate(crate);
                        MessageUtil.sendWithPrefix(player, "&a✔ Player limit updated to " + limit);
                    } catch (NumberFormatException e) {
                        MessageUtil.sendWithPrefix(player, "&c✘ Please enter a valid number!");
                    }
                    new RewardEditGUI(plugin, player, crate, reward).open();
                },
                () -> new RewardEditGUI(plugin, player, crate, reward).open()
            );
        }
    }

    private void handleGlobalLimitEdit(ClickType clickType) {
        int current = reward.getGlobalClaimLimit();
        
        if (clickType == ClickType.LEFT) {
            reward.setGlobalClaimLimit(current + 1);
            plugin.getCrateManager().saveCrate(crate);
            refresh();
        } else if (clickType == ClickType.RIGHT) {
            reward.setGlobalClaimLimit(Math.max(0, current - 1));
            plugin.getCrateManager().saveCrate(crate);
            refresh();
        } else if (clickType == ClickType.MIDDLE) {
            player.closeInventory();
            plugin.getChatInputManager().requestInput(player,
                "&e&lEnter the global claim limit (0 = unlimited):",
                input -> {
                    try {
                        int limit = Integer.parseInt(input);
                        reward.setGlobalClaimLimit(Math.max(0, limit));
                        plugin.getCrateManager().saveCrate(crate);
                        MessageUtil.sendWithPrefix(player, "&a✔ Global limit updated to " + limit);
                    } catch (NumberFormatException e) {
                        MessageUtil.sendWithPrefix(player, "&c✘ Please enter a valid number!");
                    }
                    new RewardEditGUI(plugin, player, crate, reward).open();
                },
                () -> new RewardEditGUI(plugin, player, crate, reward).open()
            );
        }
    }

    private void handleLoreEdit() {
        ItemStack display = reward.getDisplayItem();
        List<String> currentLore = new ArrayList<>();
        
        if (display != null && display.hasItemMeta() && display.getItemMeta().hasLore()) {
            for (String line : display.getItemMeta().getLore()) {
                currentLore.add(ColorUtil.decolorize(line));
            }
        }
        
        player.closeInventory();
        new LoreEditorGUI(plugin, player, crate, currentLore,
            newLore -> {
                ItemStack item = reward.getDisplayItem();
                if (item == null || item.getType() == org.bukkit.Material.AIR) {
                    item = new ItemBuilder(org.bukkit.Material.CHEST).name("&e" + reward.getId()).build();
                }
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    List<String> coloredLore = new ArrayList<>();
                    for (String line : newLore) {
                        coloredLore.add(ColorUtil.colorize(line));
                    }
                    meta.setLore(coloredLore);
                    item.setItemMeta(meta);
                }
                reward.setDisplayItem(item);
                plugin.getCrateManager().saveCrate(crate);
            },
            () -> new RewardEditGUI(plugin, player, crate, reward).open()
        ).open();
    }

    private void handlePermissionEdit(ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            reward.setPermissionRequired("");
            plugin.getCrateManager().saveCrate(crate);
            MessageUtil.sendWithPrefix(player, "&c✔ Permission requirement cleared!");
            refresh();
            return;
        }
        
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&e&lEnter the required permission (or 'none' to remove):",
            input -> {
                if (input.equalsIgnoreCase("none")) {
                    reward.setPermissionRequired("");
                } else {
                    reward.setPermissionRequired(input);
                }
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&a✔ Permission updated!");
                new RewardEditGUI(plugin, player, crate, reward).open();
            },
            () -> new RewardEditGUI(plugin, player, crate, reward).open()
        );
    }
}
