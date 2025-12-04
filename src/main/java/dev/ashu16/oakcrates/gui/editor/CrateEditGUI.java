package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.CrateAnimation;
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

public class CrateEditGUI extends GUIHolder {

    private final Crate crate;
    
    private static final int INFO_SLOT = 4;
    private static final int REWARDS_SLOT = 10;
    private static final int KEYS_SLOT = 12;
    private static final int SETTINGS_SLOT = 14;
    private static final int HOLOGRAM_SLOT = 16;
    private static final int ANIMATION_SLOT = 20;
    private static final int SOUNDS_SLOT = 22;
    private static final int PREVIEW_SLOT = 24;
    private static final int BIND_BLOCK_SLOT = 29;
    private static final int UNBIND_BLOCK_SLOT = 30;
    private static final int SAVE_SLOT = 31;
    private static final int CLOSE_SLOT = 33;

    public CrateEditGUI(OakCrates plugin, Player player, Crate crate) {
        super(plugin, player);
        this.crate = crate;
        
        String title = MessageUtil.getMessage("admin-main-title");
        title = PlaceholderUtil.parse(title, player, crate);
        
        createInventory(title, 45);
    }

    @Override
    protected void setupInventory() {
        fillEmpty();
        
        inventory.setItem(INFO_SLOT, createInfoItem());
        inventory.setItem(REWARDS_SLOT, createRewardsButton());
        inventory.setItem(KEYS_SLOT, createKeysButton());
        inventory.setItem(SETTINGS_SLOT, createSettingsButton());
        inventory.setItem(HOLOGRAM_SLOT, createHologramButton());
        inventory.setItem(ANIMATION_SLOT, createAnimationButton());
        inventory.setItem(SOUNDS_SLOT, createSoundsButton());
        inventory.setItem(PREVIEW_SLOT, createPreviewButton());
        inventory.setItem(BIND_BLOCK_SLOT, createBindBlockButton());
        inventory.setItem(UNBIND_BLOCK_SLOT, createUnbindBlockButton());
        inventory.setItem(SAVE_SLOT, createSaveButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
    }

    private ItemStack createInfoItem() {
        return new ItemBuilder(Material.BOOK)
                .name("&e&lCrate Information")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7ID: &f" + crate.getId(),
                    "&7Display: " + ColorUtil.colorize(crate.getDisplayName()),
                    "&7Rewards: &a" + crate.getRewards().size(),
                    "&7Bound Blocks: &a" + crate.getBoundBlocks().size(),
                    "&7Key Type: &b" + crate.getKeyType().name(),
                    "&7Animation: &d" + crate.getAnimation().getDisplayName(),
                    "&8━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    private ItemStack createRewardsButton() {
        return new ItemBuilder(Material.CHEST)
                .name("&e&l⚔ Edit Rewards")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Manage rewards for this crate.",
                    "",
                    "&7Total Rewards: &a" + crate.getRewards().size(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit rewards"
                ))
                .glow(true)
                .build();
    }

    private ItemStack createKeysButton() {
        return new ItemBuilder(Material.TRIPWIRE_HOOK)
                .name("&b&l⚷ Edit Key Settings")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Configure key type and",
                    "&7physical key properties.",
                    "",
                    "&7Key Type: &a" + crate.getKeyType().name(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit keys"
                ))
                .glow(true)
                .build();
    }

    private ItemStack createSettingsButton() {
        return new ItemBuilder(Material.COMPARATOR)
                .name("&6&l⚙ Edit Settings")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Configure crate settings.",
                    "",
                    "&7Display Name: " + ColorUtil.colorize(crate.getDisplayName()),
                    "&7GUI Size: &a" + crate.getGuiSize() + " slots",
                    "&7Max Rewards/Key: &a" + crate.getMaxRewardsPerKey(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit settings"
                ))
                .build();
    }

    private ItemStack createHologramButton() {
        String status = crate.getHologramSettings().isEnabled() ? "&aEnabled" : "&cDisabled";
        return new ItemBuilder(Material.NAME_TAG)
                .name("&d&l✦ Edit Hologram")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Configure hologram display.",
                    "",
                    "&7Status: " + status,
                    "&7Lines: &a" + crate.getHologramSettings().getLines().size(),
                    "&7Height: &a" + crate.getHologramSettings().getHeight(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit hologram"
                ))
                .glow(crate.getHologramSettings().isEnabled())
                .build();
    }

    private ItemStack createAnimationButton() {
        CrateAnimation anim = crate.getAnimation();
        return new ItemBuilder(Material.FIREWORK_ROCKET)
                .name("&c&l✧ Crate Animation")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Select opening animation.",
                    "",
                    "&7Current: &e" + anim.getDisplayName(),
                    "&7" + anim.getDescription(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: Next",
                    "&c▸ Right-click: Previous"
                ))
                .glow(anim != CrateAnimation.NONE)
                .build();
    }

    private ItemStack createSoundsButton() {
        return new ItemBuilder(Material.NOTE_BLOCK)
                .name("&5&l♪ Edit Sounds")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Configure crate sounds.",
                    "",
                    "&7Open: &f" + crate.getOpenSound(),
                    "&7Close: &f" + crate.getCloseSound(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to edit sounds"
                ))
                .build();
    }

    private ItemStack createPreviewButton() {
        return new ItemBuilder(Material.ENDER_EYE)
                .name("&9&l👁 Preview Crate")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Preview how this crate",
                    "&7looks to players.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to preview"
                ))
                .glow(true)
                .build();
    }

    private ItemStack createBindBlockButton() {
        return new ItemBuilder(Material.EMERALD_BLOCK)
                .name("&a&l⬓ Bind Block")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Bind a block to this crate.",
                    "",
                    "&7Currently bound: &a" + crate.getBoundBlocks().size() + " blocks",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to start binding"
                ))
                .build();
    }

    private ItemStack createUnbindBlockButton() {
        return new ItemBuilder(Material.REDSTONE_BLOCK)
                .name("&c&l⬔ Unbind All Blocks")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Remove all bound blocks.",
                    "",
                    "&7Currently bound: &a" + crate.getBoundBlocks().size() + " blocks",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&c▸ Click to unbind all"
                ))
                .build();
    }

    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&l✔ Save & Apply")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Save all changes and",
                    "&7reload the crate.",
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
            case REWARDS_SLOT:
                player.closeInventory();
                new RewardsEditorGUI(plugin, player, crate, 0).open();
                break;
            case KEYS_SLOT:
                player.closeInventory();
                new KeyEditorGUI(plugin, player, crate).open();
                break;
            case SETTINGS_SLOT:
                player.closeInventory();
                new SettingsEditorGUI(plugin, player, crate).open();
                break;
            case HOLOGRAM_SLOT:
                player.closeInventory();
                new HologramEditorGUI(plugin, player, crate).open();
                break;
            case ANIMATION_SLOT:
                handleAnimationChange(clickType);
                break;
            case SOUNDS_SLOT:
                player.closeInventory();
                new SoundsEditorGUI(plugin, player, crate).open();
                break;
            case PREVIEW_SLOT:
                player.closeInventory();
                new dev.ashu16.oakcrates.gui.CratePreviewGUI(plugin, player, crate).open();
                break;
            case BIND_BLOCK_SLOT:
                player.closeInventory();
                plugin.getBlockInteractListener().startBindSession(player, crate.getId());
                break;
            case UNBIND_BLOCK_SLOT:
                if (!crate.getBoundBlocks().isEmpty()) {
                    crate.getBoundBlocks().clear();
                    plugin.getCrateManager().saveCrate(crate);
                    plugin.getHologramManager().removeHologramsForCrate(crate.getId());
                    MessageUtil.sendWithPrefix(player, "&a✔ All blocks unbound successfully!");
                    refresh();
                } else {
                    MessageUtil.sendWithPrefix(player, "&c✘ No blocks are currently bound.");
                }
                break;
            case SAVE_SLOT:
                plugin.getCrateManager().saveCrate(crate);
                plugin.getHologramManager().respawnAllHolograms();
                MessageUtil.sendWithPrefix(player, "&a✔ Crate saved successfully!");
                SoundUtil.playSuccess(player);
                break;
            case CLOSE_SLOT:
                player.closeInventory();
                break;
        }
    }

    private void handleAnimationChange(ClickType clickType) {
        CrateAnimation current = crate.getAnimation();
        CrateAnimation next;
        
        if (clickType == ClickType.LEFT) {
            next = current.next();
        } else if (clickType == ClickType.RIGHT) {
            next = current.previous();
        } else {
            return;
        }
        
        crate.setAnimation(next);
        plugin.getCrateManager().saveCrate(crate);
        MessageUtil.sendWithPrefix(player, "&a✔ Animation changed to: &e" + next.getDisplayName());
        refresh();
    }
}
