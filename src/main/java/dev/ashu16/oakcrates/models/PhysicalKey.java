package dev.ashu16.oakcrates.models;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PhysicalKey {

    private Material material;
    private String name;
    private List<String> lore;
    private boolean glow;
    private int customModelData;

    public PhysicalKey() {
        this.material = Material.TRIPWIRE_HOOK;
        this.name = "&e&lCrate Key";
        this.lore = new ArrayList<>();
        this.lore.add("&7Right-click on a crate to use!");
        this.glow = true;
        this.customModelData = 0;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore != null ? lore : new ArrayList<>();
    }

    public void addLoreLine(String line) {
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(line);
    }

    public void removeLoreLine(int index) {
        if (lore != null && index >= 0 && index < lore.size()) {
            lore.remove(index);
        }
    }

    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public ItemStack buildKey(String crateId, int amount) {
        ItemBuilder builder = new ItemBuilder(material)
                .amount(amount)
                .name(name)
                .lore(lore);

        if (glow) {
            builder.glow(true);
        }

        if (customModelData > 0) {
            builder.setCustomModelData(customModelData);
        }

        NamespacedKey key = new NamespacedKey(OakCrates.getInstance(), "oakcrates_key");
        builder.setPersistentData(key, crateId);

        return builder.build();
    }

    public static boolean isPhysicalKey(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(OakCrates.getInstance(), "oakcrates_key");
        
        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    public static String getCrateIdFromKey(ItemStack item) {
        if (!isPhysicalKey(item)) return null;
        
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(OakCrates.getInstance(), "oakcrates_key");
        
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static boolean isKeyForCrate(ItemStack item, String crateId) {
        String keysCrateId = getCrateIdFromKey(item);
        return crateId != null && crateId.equals(keysCrateId);
    }
}
