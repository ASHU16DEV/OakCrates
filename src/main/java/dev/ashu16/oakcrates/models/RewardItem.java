package dev.ashu16.oakcrates.models;

import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardItem {

    private Material material;
    private int amount;
    private String name;
    private List<String> lore;
    private Map<Enchantment, Integer> enchantments;
    private boolean unbreakable;
    private boolean glow;
    private int customModelData;

    public RewardItem() {
        this.material = Material.STONE;
        this.amount = 1;
        this.name = null;
        this.lore = new ArrayList<>();
        this.enchantments = new HashMap<>();
        this.unbreakable = false;
        this.glow = false;
        this.customModelData = 0;
    }

    public RewardItem(Material material) {
        this();
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(1, Math.min(64, amount));
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

    public void setLoreLine(int index, String line) {
        if (lore != null && index >= 0 && index < lore.size()) {
            lore.set(index, line);
        }
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments != null ? enchantments : new HashMap<>();
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        if (enchantments == null) {
            enchantments = new HashMap<>();
        }
        enchantments.put(enchantment, level);
    }

    public void removeEnchantment(Enchantment enchantment) {
        if (enchantments != null) {
            enchantments.remove(enchantment);
        }
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
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

    public ItemStack build() {
        ItemBuilder builder = new ItemBuilder(material)
                .amount(amount);

        if (name != null && !name.isEmpty()) {
            builder.name(name);
        }

        if (lore != null && !lore.isEmpty()) {
            builder.lore(lore);
        }

        if (enchantments != null && !enchantments.isEmpty()) {
            builder.enchantments(enchantments);
        }

        if (unbreakable) {
            builder.unbreakable(true);
        }

        if (glow && (enchantments == null || enchantments.isEmpty())) {
            builder.glow(true);
        }

        if (customModelData > 0) {
            builder.setCustomModelData(customModelData);
        }

        return builder.build();
    }

    public static RewardItem fromItemStack(ItemStack item) {
        if (item == null) return null;

        RewardItem rewardItem = new RewardItem(item.getType());
        rewardItem.setAmount(item.getAmount());

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            
            if (meta.hasDisplayName()) {
                rewardItem.setName(ColorUtil.decolorize(meta.getDisplayName()));
            }
            
            if (meta.hasLore()) {
                List<String> decolorizedLore = new ArrayList<>();
                for (String line : meta.getLore()) {
                    decolorizedLore.add(ColorUtil.decolorize(line));
                }
                rewardItem.setLore(decolorizedLore);
            }
            
            rewardItem.setUnbreakable(meta.isUnbreakable());
            
            if (meta.hasCustomModelData()) {
                rewardItem.setCustomModelData(meta.getCustomModelData());
            }
        }

        if (!item.getEnchantments().isEmpty()) {
            rewardItem.setEnchantments(new HashMap<>(item.getEnchantments()));
        }

        return rewardItem;
    }
}
