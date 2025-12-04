package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.PlaceholderUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SettingsEditorGUI extends GUIHolder {

    private final Crate crate;
    
    private static final int INFO_SLOT = 4;
    private static final int DISPLAY_NAME_SLOT = 10;
    private static final int GUI_SIZE_SLOT = 12;
    private static final int MAX_REWARDS_SLOT = 14;
    private static final int TOTAL_LIMIT_SLOT = 16;
    private static final int BACK_SLOT = 29;
    private static final int SAVE_SLOT = 31;
    private static final int CLOSE_SLOT = 33;

    public SettingsEditorGUI(OakCrates plugin, Player player, Crate crate) {
        super(plugin, player);
        this.crate = crate;
        
        String title = MessageUtil.getMessage("admin-settings-title");
        title = PlaceholderUtil.parse(title, player, crate);
        
        createInventory(title, 36);
    }

    @Override
    protected void setupInventory() {
        fillEmpty();
        
        inventory.setItem(INFO_SLOT, createInfoItem());
        inventory.setItem(DISPLAY_NAME_SLOT, createDisplayNameButton());
        inventory.setItem(GUI_SIZE_SLOT, createGuiSizeButton());
        inventory.setItem(MAX_REWARDS_SLOT, createMaxRewardsButton());
        inventory.setItem(TOTAL_LIMIT_SLOT, createTotalLimitButton());
        
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(SAVE_SLOT, createSaveButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
    }

    private ItemStack createInfoItem() {
        return new ItemBuilder(Material.BOOK)
                .name("&e&lCrate Settings")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Configure general settings",
                    "&7for this crate.",
                    "&8━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    private ItemStack createDisplayNameButton() {
        return new ItemBuilder(Material.NAME_TAG)
                .name("&e&l✎ Display Name")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: " + ColorUtil.colorize(crate.getDisplayName()),
                    "",
                    "&7The name shown in GUIs",
                    "&7and messages.",
                    "&7Supports color codes.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to change"
                ))
                .build();
    }

    private ItemStack createGuiSizeButton() {
        return new ItemBuilder(Material.CHEST)
                .name("&e&l⊞ GUI Size")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: &a" + crate.getGuiSize() + " slots",
                    "",
                    "&7Size of crate reward GUI.",
                    "&7Valid sizes: 9, 18, 27, 36, 45, 54",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: +9 slots",
                    "&c▸ Right-click: -9 slots"
                ))
                .build();
    }

    private ItemStack createMaxRewardsButton() {
        return new ItemBuilder(Material.DIAMOND)
                .name("&e&l⚔ Max Rewards Per Key")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: &a" + crate.getMaxRewardsPerKey(),
                    "",
                    "&7How many rewards a player",
                    "&7can claim with one key.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: +1",
                    "&c▸ Right-click: -1"
                ))
                .glow(true)
                .build();
    }

    private ItemStack createTotalLimitButton() {
        String limit = crate.getPerPlayerTotalClaimLimit() == 0 ? "&aUnlimited" : "&e" + crate.getPerPlayerTotalClaimLimit();
        return new ItemBuilder(Material.BARRIER)
                .name("&e&l⚑ Per-Player Total Limit")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Current: " + limit,
                    "",
                    "&7Maximum total claims from",
                    "&7this crate per player.",
                    "&70 = Unlimited",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to change"
                ))
                .build();
    }

    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&l✔ Save Changes")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Click to save all",
                    "&7crate settings.",
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
            case DISPLAY_NAME_SLOT:
                handleDisplayNameEdit();
                break;
            case GUI_SIZE_SLOT:
                handleGuiSizeEdit(clickType);
                break;
            case MAX_REWARDS_SLOT:
                handleMaxRewardsEdit(clickType);
                break;
            case TOTAL_LIMIT_SLOT:
                handleTotalLimitEdit();
                break;
            case BACK_SLOT:
                player.closeInventory();
                new CrateEditGUI(plugin, player, crate).open();
                break;
            case SAVE_SLOT:
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&a✔ Settings saved successfully!");
                SoundUtil.playSuccess(player);
                break;
            case CLOSE_SLOT:
                player.closeInventory();
                break;
        }
    }

    private void handleDisplayNameEdit() {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&e&lEnter the new display name (supports color codes):",
            input -> {
                crate.setDisplayName(input);
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&a✔ Display name updated to: " + ColorUtil.colorize(input));
                new SettingsEditorGUI(plugin, player, crate).open();
            },
            () -> new SettingsEditorGUI(plugin, player, crate).open()
        );
    }

    private void handleGuiSizeEdit(ClickType clickType) {
        int currentSize = crate.getGuiSize();
        int newSize;
        
        if (clickType == ClickType.LEFT) {
            newSize = Math.min(54, currentSize + 9);
        } else if (clickType == ClickType.RIGHT) {
            newSize = Math.max(9, currentSize - 9);
        } else {
            return;
        }
        
        crate.setGuiSize(newSize);
        plugin.getCrateManager().saveCrate(crate);
        MessageUtil.sendWithPrefix(player, "&a✔ GUI size updated to " + newSize + " slots");
        refresh();
    }

    private void handleMaxRewardsEdit(ClickType clickType) {
        int current = crate.getMaxRewardsPerKey();
        int newValue;
        
        if (clickType == ClickType.LEFT) {
            newValue = current + 1;
        } else if (clickType == ClickType.RIGHT) {
            newValue = Math.max(1, current - 1);
        } else {
            return;
        }
        
        crate.setMaxRewardsPerKey(newValue);
        plugin.getCrateManager().saveCrate(crate);
        MessageUtil.sendWithPrefix(player, "&a✔ Max rewards updated to " + newValue);
        refresh();
    }

    private void handleTotalLimitEdit() {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&e&lEnter the new total claim limit (0 = unlimited):",
            input -> {
                try {
                    int limit = Integer.parseInt(input);
                    crate.setPerPlayerTotalClaimLimit(Math.max(0, limit));
                    plugin.getCrateManager().saveCrate(crate);
                    MessageUtil.sendWithPrefix(player, "&a✔ Total claim limit updated to " + limit);
                } catch (NumberFormatException e) {
                    MessageUtil.sendWithPrefix(player, "&c✘ Please enter a valid number!");
                }
                new SettingsEditorGUI(plugin, player, crate).open();
            },
            () -> new SettingsEditorGUI(plugin, player, crate).open()
        );
    }
}
