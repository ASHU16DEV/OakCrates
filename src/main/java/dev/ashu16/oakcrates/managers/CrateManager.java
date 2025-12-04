package dev.ashu16.oakcrates.managers;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.*;
import dev.ashu16.oakcrates.models.CrateAnimation;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CrateManager {

    private final OakCrates plugin;
    private final Map<String, Crate> crates;

    public CrateManager(OakCrates plugin) {
        this.plugin = plugin;
        this.crates = new HashMap<>();
    }

    public void loadCrates() {
        crates.clear();
        
        FileConfiguration config = plugin.getConfigManager().getCrates();
        ConfigurationSection cratesSection = config.getConfigurationSection("crates");
        
        if (cratesSection == null) {
            plugin.getLogger().warning("No crates found in crates.yml");
            return;
        }

        for (String crateId : cratesSection.getKeys(false)) {
            ConfigurationSection crateSection = cratesSection.getConfigurationSection(crateId);
            if (crateSection == null) continue;

            try {
                Crate crate = loadCrate(crateId, crateSection);
                crates.put(crateId, crate);
                plugin.getLogger().info("Loaded crate: " + crateId);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load crate '" + crateId + "': " + e.getMessage());
            }
        }

        plugin.getLogger().info("Loaded " + crates.size() + " crate(s)");
    }

    private Crate loadCrate(String crateId, ConfigurationSection section) {
        Crate crate = new Crate(crateId);
        
        crate.setDisplayName(section.getString("display-name", "&7" + crateId));
        crate.setGuiSize(section.getInt("gui-size", 27));
        crate.setKeyType(Crate.KeyType.valueOf(section.getString("key-type", "VIRTUAL").toUpperCase()));
        crate.setMaxRewardsPerKey(section.getInt("max-rewards-per-key", 1));
        crate.setPerPlayerTotalClaimLimit(section.getInt("per-player-total-claim-limit", 0));
        
        ConfigurationSection physicalKeySection = section.getConfigurationSection("physical-key");
        if (physicalKeySection != null) {
            PhysicalKey physicalKey = new PhysicalKey();
            physicalKey.setMaterial(Material.valueOf(physicalKeySection.getString("material", "TRIPWIRE_HOOK").toUpperCase()));
            physicalKey.setName(physicalKeySection.getString("name", "&e&lCrate Key"));
            physicalKey.setLore(physicalKeySection.getStringList("lore"));
            physicalKey.setGlow(physicalKeySection.getBoolean("glow", true));
            physicalKey.setCustomModelData(physicalKeySection.getInt("custom-model-data", 0));
            crate.setPhysicalKey(physicalKey);
        }
        
        ConfigurationSection hologramSection = section.getConfigurationSection("hologram");
        if (hologramSection != null) {
            HologramSettings hologramSettings = new HologramSettings();
            hologramSettings.setEnabled(hologramSection.getBoolean("enabled", true));
            hologramSettings.setLines(hologramSection.getStringList("lines"));
            hologramSettings.setHeight(hologramSection.getDouble("height", 2.5));
            crate.setHologramSettings(hologramSettings);
        }
        
        crate.setBoundBlocks(section.getStringList("bound-blocks"));
        
        String animationName = section.getString("animation", "NONE");
        try {
            crate.setAnimation(CrateAnimation.valueOf(animationName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            crate.setAnimation(CrateAnimation.NONE);
        }
        
        crate.setOpenSound(section.getString("open-sound", "BLOCK_ENDER_CHEST_OPEN"));
        crate.setCloseSound(section.getString("close-sound", "BLOCK_ENDER_CHEST_CLOSE"));
        
        ConfigurationSection rewardsSection = section.getConfigurationSection("rewards");
        if (rewardsSection != null) {
            for (String rewardId : rewardsSection.getKeys(false)) {
                ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(rewardId);
                if (rewardSection != null) {
                    Reward reward = loadReward(rewardId, rewardSection);
                    crate.addReward(rewardId, reward);
                }
            }
        }

        return crate;
    }

    private Reward loadReward(String rewardId, ConfigurationSection section) {
        Reward reward = new Reward(rewardId);
        
        reward.setSlot(section.getInt("slot", 0));
        reward.setPerPlayerClaimLimit(section.getInt("per-player-claim-limit", 0));
        reward.setGlobalClaimLimit(section.getInt("global-claim-limit", 0));
        reward.setPermissionRequired(section.getString("permission-required", ""));
        reward.setCommands(section.getStringList("commands"));
        
        ConfigurationSection displaySection = section.getConfigurationSection("display-item");
        if (displaySection != null) {
            ItemStack displayItem = loadItemStack(displaySection);
            reward.setDisplayItem(displayItem);
        }
        
        List<Map<?, ?>> itemsList = section.getMapList("items");
        for (Map<?, ?> itemMap : itemsList) {
            RewardItem rewardItem = loadRewardItem(itemMap);
            if (rewardItem != null) {
                reward.addItem(rewardItem);
            }
        }

        return reward;
    }

    private ItemStack loadItemStack(ConfigurationSection section) {
        Material material = Material.valueOf(section.getString("material", "STONE").toUpperCase());
        int amount = section.getInt("amount", 1);
        String name = section.getString("name");
        List<String> lore = section.getStringList("lore");
        boolean glow = section.getBoolean("glow", false);

        ItemBuilder builder = new ItemBuilder(material)
                .amount(amount);

        if (name != null) {
            builder.name(name);
        }

        if (!lore.isEmpty()) {
            builder.lore(lore);
        }

        if (glow) {
            builder.glow(true);
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private RewardItem loadRewardItem(Map<?, ?> map) {
        try {
            RewardItem item = new RewardItem();
            
            if (map.containsKey("material")) {
                item.setMaterial(Material.valueOf(map.get("material").toString().toUpperCase()));
            }
            
            if (map.containsKey("amount")) {
                item.setAmount(Integer.parseInt(map.get("amount").toString()));
            }
            
            if (map.containsKey("name")) {
                item.setName(map.get("name").toString());
            }
            
            if (map.containsKey("lore")) {
                item.setLore((List<String>) map.get("lore"));
            }
            
            if (map.containsKey("enchantments")) {
                Map<String, Object> enchants = (Map<String, Object>) map.get("enchantments");
                Map<Enchantment, Integer> enchantments = new HashMap<>();
                
                for (Map.Entry<String, Object> entry : enchants.entrySet()) {
                    Enchantment ench = Enchantment.getByName(entry.getKey());
                    if (ench != null) {
                        enchantments.put(ench, Integer.parseInt(entry.getValue().toString()));
                    }
                }
                item.setEnchantments(enchantments);
            }
            
            if (map.containsKey("unbreakable")) {
                item.setUnbreakable(Boolean.parseBoolean(map.get("unbreakable").toString()));
            }
            
            if (map.containsKey("glow")) {
                item.setGlow(Boolean.parseBoolean(map.get("glow").toString()));
            }

            return item;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load reward item: " + e.getMessage());
            return null;
        }
    }

    public void saveCrate(Crate crate) {
        FileConfiguration config = plugin.getConfigManager().getCrates();
        String path = "crates." + crate.getId();
        
        config.set(path + ".display-name", crate.getDisplayName());
        config.set(path + ".gui-size", crate.getGuiSize());
        config.set(path + ".key-type", crate.getKeyType().name());
        config.set(path + ".max-rewards-per-key", crate.getMaxRewardsPerKey());
        config.set(path + ".per-player-total-claim-limit", crate.getPerPlayerTotalClaimLimit());
        
        PhysicalKey pk = crate.getPhysicalKey();
        config.set(path + ".physical-key.material", pk.getMaterial().name());
        config.set(path + ".physical-key.name", pk.getName());
        config.set(path + ".physical-key.lore", pk.getLore());
        config.set(path + ".physical-key.glow", pk.isGlow());
        config.set(path + ".physical-key.custom-model-data", pk.getCustomModelData());
        
        HologramSettings hs = crate.getHologramSettings();
        config.set(path + ".hologram.enabled", hs.isEnabled());
        config.set(path + ".hologram.lines", hs.getLines());
        config.set(path + ".hologram.height", hs.getHeight());
        
        config.set(path + ".bound-blocks", crate.getBoundBlocks());
        
        config.set(path + ".animation", crate.getAnimation().name());
        config.set(path + ".open-sound", crate.getOpenSound());
        config.set(path + ".close-sound", crate.getCloseSound());
        
        for (Map.Entry<String, Reward> entry : crate.getRewards().entrySet()) {
            saveReward(crate.getId(), entry.getKey(), entry.getValue());
        }
        
        plugin.getConfigManager().saveCrates();
    }

    private void saveReward(String crateId, String rewardId, Reward reward) {
        FileConfiguration config = plugin.getConfigManager().getCrates();
        String path = "crates." + crateId + ".rewards." + rewardId;
        
        config.set(path + ".slot", reward.getSlot());
        config.set(path + ".per-player-claim-limit", reward.getPerPlayerClaimLimit());
        config.set(path + ".global-claim-limit", reward.getGlobalClaimLimit());
        config.set(path + ".permission-required", reward.getPermissionRequired());
        config.set(path + ".commands", reward.getCommands());
        
        ItemStack display = reward.getDisplayItem();
        if (display != null) {
            config.set(path + ".display-item.material", display.getType().name());
            config.set(path + ".display-item.amount", display.getAmount());
            if (display.hasItemMeta()) {
                if (display.getItemMeta().hasDisplayName()) {
                    config.set(path + ".display-item.name", ColorUtil.decolorize(display.getItemMeta().getDisplayName()));
                }
                if (display.getItemMeta().hasLore()) {
                    List<String> decolorizedLore = new ArrayList<>();
                    for (String line : display.getItemMeta().getLore()) {
                        decolorizedLore.add(ColorUtil.decolorize(line));
                    }
                    config.set(path + ".display-item.lore", decolorizedLore);
                }
            }
        }
        
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (RewardItem item : reward.getItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("material", item.getMaterial().name());
            itemMap.put("amount", item.getAmount());
            if (item.getName() != null) {
                itemMap.put("name", item.getName());
            }
            if (!item.getLore().isEmpty()) {
                itemMap.put("lore", item.getLore());
            }
            if (!item.getEnchantments().isEmpty()) {
                Map<String, Integer> enchants = new HashMap<>();
                for (Map.Entry<Enchantment, Integer> e : item.getEnchantments().entrySet()) {
                    enchants.put(e.getKey().getName(), e.getValue());
                }
                itemMap.put("enchantments", enchants);
            }
            itemMap.put("unbreakable", item.isUnbreakable());
            itemMap.put("glow", item.isGlow());
            itemsList.add(itemMap);
        }
        config.set(path + ".items", itemsList);
    }

    public Crate createCrate(String crateId) {
        if (crates.containsKey(crateId)) {
            return null;
        }
        
        Crate crate = new Crate(crateId);
        crates.put(crateId, crate);
        saveCrate(crate);
        
        return crate;
    }

    public boolean deleteCrate(String crateId) {
        if (!crates.containsKey(crateId)) {
            return false;
        }
        
        crates.remove(crateId);
        
        FileConfiguration config = plugin.getConfigManager().getCrates();
        config.set("crates." + crateId, null);
        plugin.getConfigManager().saveCrates();
        
        plugin.getHologramManager().removeHologramsForCrate(crateId);
        
        return true;
    }

    public Crate getCrate(String crateId) {
        return crates.get(crateId);
    }

    public Map<String, Crate> getAllCrates() {
        return Collections.unmodifiableMap(crates);
    }

    public Crate getCrateAtLocation(Location location) {
        String blockString = location.getWorld().getName() + ":" + 
                            location.getBlockX() + ":" + 
                            location.getBlockY() + ":" + 
                            location.getBlockZ();
        
        for (Crate crate : crates.values()) {
            if (crate.getBoundBlocks().contains(blockString)) {
                return crate;
            }
        }
        
        return null;
    }

    public List<Location> getBoundLocations(String crateId) {
        Crate crate = getCrate(crateId);
        if (crate == null) return Collections.emptyList();
        
        List<Location> locations = new ArrayList<>();
        
        for (String blockString : crate.getBoundBlocks()) {
            String[] parts = blockString.split(":");
            if (parts.length == 4) {
                World world = Bukkit.getWorld(parts[0]);
                if (world != null) {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int z = Integer.parseInt(parts[3]);
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        
        return locations;
    }

    public boolean exists(String crateId) {
        return crates.containsKey(crateId);
    }
}
