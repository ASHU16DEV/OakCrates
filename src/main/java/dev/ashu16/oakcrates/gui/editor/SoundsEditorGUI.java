package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SoundsEditorGUI extends GUIHolder {

    private final Crate crate;
    
    private static final int INFO_SLOT = 4;
    private static final int OPEN_SOUND_SLOT = 11;
    private static final int CLOSE_SOUND_SLOT = 15;
    private static final int BACK_SLOT = 30;
    private static final int SAVE_SLOT = 31;
    private static final int CLOSE_SLOT = 32;

    private static final String[] AVAILABLE_SOUNDS = {
        "NONE",
        "BLOCK_ENDER_CHEST_OPEN",
        "BLOCK_ENDER_CHEST_CLOSE",
        "BLOCK_CHEST_OPEN",
        "BLOCK_CHEST_CLOSE",
        "ENTITY_PLAYER_LEVELUP",
        "ENTITY_EXPERIENCE_ORB_PICKUP",
        "UI_BUTTON_CLICK",
        "BLOCK_NOTE_BLOCK_PLING",
        "BLOCK_NOTE_BLOCK_CHIME",
        "ENTITY_FIREWORK_ROCKET_LAUNCH",
        "ENTITY_FIREWORK_ROCKET_BLAST"
    };

    public SoundsEditorGUI(OakCrates plugin, Player player, Crate crate) {
        super(plugin, player);
        this.crate = crate;
        
        createInventory("&8Edit Crate Sounds", 36);
    }

    @Override
    protected void setupInventory() {
        fillEmpty();
        
        inventory.setItem(INFO_SLOT, createInfoItem());
        inventory.setItem(OPEN_SOUND_SLOT, createOpenSoundButton());
        inventory.setItem(CLOSE_SOUND_SLOT, createCloseSoundButton());
        
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(SAVE_SLOT, createSaveButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
    }

    private ItemStack createInfoItem() {
        return new ItemBuilder(Material.BOOK)
                .name("&e&l♪ Sound Settings")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Configure the sounds that",
                    "&7play when opening/closing",
                    "&7this crate.",
                    "",
                    "&7Set to NONE to disable.",
                    "&8━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    private ItemStack createOpenSoundButton() {
        return new ItemBuilder(Material.JUKEBOX)
                .name("&a&l♫ Open Sound")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Sound played when crate",
                    "&7is opened by a player.",
                    "",
                    "&7Current: &e" + crate.getOpenSound(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: Next sound",
                    "&c▸ Right-click: Previous sound",
                    "&e▸ Middle-click: Test sound"
                ))
                .glow(!"NONE".equalsIgnoreCase(crate.getOpenSound()))
                .build();
    }

    private ItemStack createCloseSoundButton() {
        return new ItemBuilder(Material.NOTE_BLOCK)
                .name("&c&l♫ Close Sound")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Sound played when crate",
                    "&7is closed by a player.",
                    "",
                    "&7Current: &e" + crate.getCloseSound(),
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Left-click: Next sound",
                    "&c▸ Right-click: Previous sound",
                    "&e▸ Middle-click: Test sound"
                ))
                .glow(!"NONE".equalsIgnoreCase(crate.getCloseSound()))
                .build();
    }

    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&l✔ Save Changes")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Click to save all",
                    "&7sound settings.",
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
            case OPEN_SOUND_SLOT:
                handleSoundChange(true, clickType);
                break;
            case CLOSE_SOUND_SLOT:
                handleSoundChange(false, clickType);
                break;
            case BACK_SLOT:
                player.closeInventory();
                new CrateEditGUI(plugin, player, crate).open();
                break;
            case SAVE_SLOT:
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&a✔ Sound settings saved!");
                SoundUtil.playSuccess(player);
                break;
            case CLOSE_SLOT:
                player.closeInventory();
                break;
        }
    }

    private void handleSoundChange(boolean isOpenSound, ClickType clickType) {
        String current = isOpenSound ? crate.getOpenSound() : crate.getCloseSound();
        int currentIndex = findSoundIndex(current);
        
        String newSound;
        
        if (clickType == ClickType.MIDDLE) {
            if (!"NONE".equalsIgnoreCase(current)) {
                try {
                    Sound sound = Sound.valueOf(current);
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                    MessageUtil.sendWithPrefix(player, "&a♪ Playing: &e" + current);
                } catch (IllegalArgumentException e) {
                    MessageUtil.sendWithPrefix(player, "&c✘ Invalid sound!");
                }
            }
            return;
        } else if (clickType == ClickType.LEFT) {
            newSound = AVAILABLE_SOUNDS[(currentIndex + 1) % AVAILABLE_SOUNDS.length];
        } else if (clickType == ClickType.RIGHT) {
            newSound = AVAILABLE_SOUNDS[(currentIndex - 1 + AVAILABLE_SOUNDS.length) % AVAILABLE_SOUNDS.length];
        } else {
            return;
        }
        
        if (isOpenSound) {
            crate.setOpenSound(newSound);
        } else {
            crate.setCloseSound(newSound);
        }
        
        plugin.getCrateManager().saveCrate(crate);
        String type = isOpenSound ? "Open" : "Close";
        MessageUtil.sendWithPrefix(player, "&a✔ " + type + " sound changed to: &e" + newSound);
        refresh();
    }

    private int findSoundIndex(String sound) {
        for (int i = 0; i < AVAILABLE_SOUNDS.length; i++) {
            if (AVAILABLE_SOUNDS[i].equalsIgnoreCase(sound)) {
                return i;
            }
        }
        return 0;
    }
}
