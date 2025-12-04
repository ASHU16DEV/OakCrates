package dev.ashu16.oakcrates.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        if (meta != null && name != null) {
            meta.setDisplayName(ColorUtil.colorize(name));
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (meta != null && lore != null) {
            meta.setLore(ColorUtil.colorize(Arrays.asList(lore)));
        }
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        if (meta != null && lore != null) {
            meta.setLore(ColorUtil.colorize(new ArrayList<>(lore)));
        }
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        if (meta != null && lines != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            for (String line : lines) {
                lore.add(ColorUtil.colorize(line));
            }
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder addLore(List<String> lines) {
        if (meta != null && lines != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            for (String line : lines) {
                lore.add(ColorUtil.colorize(line));
            }
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (meta != null && enchantment != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        if (meta != null && enchantments != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (meta != null && glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    public ItemBuilder hideFlags() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        return this;
    }

    public ItemBuilder addFlag(ItemFlag flag) {
        if (meta != null && flag != null) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        if (meta != null && data > 0) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    public ItemBuilder setPersistentData(NamespacedKey key, String value) {
        if (meta != null && key != null && value != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        }
        return this;
    }

    public ItemBuilder setPersistentData(NamespacedKey key, int value) {
        if (meta != null && key != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemBuilder from(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder from(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemStack createFiller(Material material) {
        return new ItemBuilder(material)
                .name(" ")
                .build();
    }

    public static ItemStack createFiller() {
        return createFiller(Material.BLACK_STAINED_GLASS_PANE);
    }
}
