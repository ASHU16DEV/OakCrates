package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.PhysicalKey;
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

public class KeyEditorGUI extends GUIHolder {

    private final Crate crate;
    
    private static final int KEY_TYPE_SLOT = 11;
    private static final int KEY_MATERIAL_SLOT = 13;
    private static final int KEY_NAME_SLOT = 15;
    private static final int KEY_LORE_SLOT = 29;
    private static final int KEY_GLOW_SLOT = 31;
    private static final int KEY_PREVIEW_SLOT = 33;
    private static final int BACK_SLOT = 39;
    private static final int SAVE_SLOT = 40;
    private static final int CLOSE_SLOT = 41;

    public KeyEditorGUI(OakCrates plugin, Player player, Crate crate) {
        super(plugin, player);
        this.crate = crate;
        
        String title = MessageUtil.getMessage("admin-keys-title");
        title = PlaceholderUtil.parse(title, player, crate);
        
        createInventory(title, 45);
    }

    @Override
    protected void setupInventory() {
        fillEmpty();
        
        inventory.setItem(KEY_TYPE_SLOT, createKeyTypeButton());
        inventory.setItem(KEY_MATERIAL_SLOT, createKeyMaterialButton());
        inventory.setItem(KEY_NAME_SLOT, createKeyNameButton());
        inventory.setItem(KEY_LORE_SLOT, createKeyLoreButton());
        inventory.setItem(KEY_GLOW_SLOT, createKeyGlowButton());
        inventory.setItem(KEY_PREVIEW_SLOT, createKeyPreview());
        
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(SAVE_SLOT, createSaveButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
    }

    private ItemStack createKeyTypeButton() {
        Crate.KeyType keyType = crate.getKeyType();
        Material material;
        switch (keyType) {
            case PHYSICAL:
                material = Material.TRIPWIRE_HOOK;
                break;
            case BOTH:
                material = Material.ENDER_PEARL;
                break;
            default:
                material = Material.PAPER;
        }
        
        return new ItemBuilder(material)
                .name("&e&lKey Type")
                .lore(Arrays.asList(
                    "&7Current: &a" + keyType.name(),
                    "",
                    "&7VIRTUAL: &fDatabase-stored keys",
                    "&7PHYSICAL: &fItem-based keys",
                    "&7BOTH: &fAccepts both types",
                    "",
                    "&eClick to cycle through types"
                ))
                .build();
    }

    private ItemStack createKeyMaterialButton() {
        PhysicalKey key = crate.getPhysicalKey();
        return new ItemBuilder(key.getMaterial())
                .name("&e&lKey Material")
                .lore(Arrays.asList(
                    "&7Current: &a" + key.getMaterial().name(),
                    "",
                    "&7Material used for physical keys.",
                    "",
                    "&eClick to change material"
                ))
                .build();
    }

    private ItemStack createKeyNameButton() {
        PhysicalKey key = crate.getPhysicalKey();
        return new ItemBuilder(Material.NAME_TAG)
                .name("&e&lKey Name")
                .lore(Arrays.asList(
                    "&7Current: " + ColorUtil.colorize(key.getName()),
                    "",
                    "&7Display name for physical keys.",
                    "",
                    "&eClick to change name"
                ))
                .build();
    }

    private ItemStack createKeyLoreButton() {
        PhysicalKey key = crate.getPhysicalKey();
        return new ItemBuilder(Material.WRITABLE_BOOK)
                .name("&e&lKey Lore")
                .lore(Arrays.asList(
                    "&7Current lines: &a" + key.getLore().size(),
                    "",
                    "&7Lore text for physical keys.",
                    "",
                    "&eClick to edit lore"
                ))
                .build();
    }

    private ItemStack createKeyGlowButton() {
        PhysicalKey key = crate.getPhysicalKey();
        Material material = key.isGlow() ? Material.GLOWSTONE_DUST : Material.GUNPOWDER;
        
        return new ItemBuilder(material)
                .name("&e&lKey Glow Effect")
                .lore(Arrays.asList(
                    "&7Current: " + (key.isGlow() ? "&aEnabled" : "&cDisabled"),
                    "",
                    "&7Adds enchant glow to physical keys.",
                    "",
                    "&eClick to toggle"
                ))
                .glow(key.isGlow())
                .build();
    }

    private ItemStack createKeyPreview() {
        return crate.getPhysicalKey().buildKey(crate.getId(), 1);
    }

    private ItemStack createSaveButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&lSave Changes")
                .lore(Arrays.asList(
                    "&7Click to save all changes",
                    "&7to key settings."
                ))
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        SoundUtil.playConfirm(player);
        
        switch (slot) {
            case KEY_TYPE_SLOT:
                cycleKeyType();
                plugin.getCrateManager().saveCrate(crate);
                refresh();
                break;
            case KEY_MATERIAL_SLOT:
                handleMaterialEdit();
                break;
            case KEY_NAME_SLOT:
                handleNameEdit();
                break;
            case KEY_LORE_SLOT:
                player.closeInventory();
                new LoreEditorGUI(plugin, player, crate, crate.getPhysicalKey().getLore(), 
                    newLore -> {
                        crate.getPhysicalKey().setLore(newLore);
                        plugin.getCrateManager().saveCrate(crate);
                    },
                    () -> new KeyEditorGUI(plugin, player, crate).open()
                ).open();
                break;
            case KEY_GLOW_SLOT:
                crate.getPhysicalKey().setGlow(!crate.getPhysicalKey().isGlow());
                plugin.getCrateManager().saveCrate(crate);
                refresh();
                break;
            case BACK_SLOT:
                player.closeInventory();
                new CrateEditGUI(plugin, player, crate).open();
                break;
            case SAVE_SLOT:
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendMessage(player, "edit-saved");
                break;
            case CLOSE_SLOT:
                player.closeInventory();
                break;
        }
    }

    private void cycleKeyType() {
        Crate.KeyType current = crate.getKeyType();
        switch (current) {
            case VIRTUAL:
                crate.setKeyType(Crate.KeyType.PHYSICAL);
                break;
            case PHYSICAL:
                crate.setKeyType(Crate.KeyType.BOTH);
                break;
            case BOTH:
                crate.setKeyType(Crate.KeyType.VIRTUAL);
                break;
        }
    }

    private void handleMaterialEdit() {
        player.closeInventory();
        MessageUtil.sendWithPrefix(player, "&eHold the item you want as key material and type '&aconfirm&e' or '&ccancel&e'");
        
        plugin.getChatInputManager().requestInput(player,
            null,
            input -> {
                if (input.equalsIgnoreCase("confirm")) {
                    ItemStack handItem = player.getInventory().getItemInMainHand();
                    if (handItem != null && handItem.getType() != Material.AIR) {
                        crate.getPhysicalKey().setMaterial(handItem.getType());
                        plugin.getCrateManager().saveCrate(crate);
                        MessageUtil.sendWithPrefix(player, "&aKey material updated!");
                    } else {
                        MessageUtil.sendWithPrefix(player, "&cYou're not holding any item!");
                    }
                }
                new KeyEditorGUI(plugin, player, crate).open();
            },
            () -> new KeyEditorGUI(plugin, player, crate).open()
        );
    }

    private void handleNameEdit() {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&eEnter the new key name (supports color codes):",
            input -> {
                crate.getPhysicalKey().setName(input);
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&aKey name updated!");
                new KeyEditorGUI(plugin, player, crate).open();
            },
            () -> new KeyEditorGUI(plugin, player, crate).open()
        );
    }
}
