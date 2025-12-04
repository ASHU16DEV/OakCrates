package dev.ashu16.oakcrates.managers;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.PhysicalKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyManager {

    private final OakCrates plugin;

    public KeyManager(OakCrates plugin) {
        this.plugin = plugin;
    }

    public int getVirtualKeys(UUID playerUuid, String crateId) {
        return plugin.getDatabaseManager().getPlayerKeys(playerUuid, crateId);
    }

    public void setVirtualKeys(UUID playerUuid, String crateId, int amount) {
        plugin.getDatabaseManager().setPlayerKeys(playerUuid, crateId, amount);
    }

    public void giveVirtualKeys(UUID playerUuid, String crateId, int amount) {
        plugin.getDatabaseManager().addPlayerKeys(playerUuid, crateId, amount);
    }

    public boolean takeVirtualKeys(UUID playerUuid, String crateId, int amount) {
        return plugin.getDatabaseManager().takePlayerKeys(playerUuid, crateId, amount);
    }

    public int getPhysicalKeys(Player player, String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return 0;

        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && PhysicalKey.isKeyForCrate(item, crateId)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public boolean takePhysicalKey(Player player, String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return false;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && PhysicalKey.isKeyForCrate(item, crateId)) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().setItem(i, null);
                }
                return true;
            }
        }
        return false;
    }

    public void givePhysicalKey(Player player, String crateId, int amount) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return;

        ItemStack keyItem = crate.getPhysicalKey().buildKey(crateId, amount);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(keyItem);
        
        for (ItemStack item : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    public int getTotalKeys(Player player, String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return 0;

        int total = 0;
        
        if (crate.acceptsVirtualKeys()) {
            total += getVirtualKeys(player.getUniqueId(), crateId);
        }
        
        if (crate.acceptsPhysicalKeys()) {
            total += getPhysicalKeys(player, crateId);
        }
        
        return total;
    }

    public boolean hasKey(Player player, String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return false;

        if (crate.acceptsVirtualKeys() && getVirtualKeys(player.getUniqueId(), crateId) > 0) {
            return true;
        }
        
        if (crate.acceptsPhysicalKeys() && getPhysicalKeys(player, crateId) > 0) {
            return true;
        }
        
        return false;
    }

    public boolean consumeKey(Player player, String crateId) {
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        if (crate == null) return false;

        if (crate.acceptsPhysicalKeys()) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (PhysicalKey.isKeyForCrate(mainHand, crateId)) {
                if (mainHand.getAmount() > 1) {
                    mainHand.setAmount(mainHand.getAmount() - 1);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }
                return true;
            }
            
            if (takePhysicalKey(player, crateId)) {
                return true;
            }
        }

        if (crate.acceptsVirtualKeys()) {
            if (takeVirtualKeys(player.getUniqueId(), crateId, 1)) {
                return true;
            }
        }

        return false;
    }

    public Map<String, Integer> getAllPlayerKeys(UUID playerUuid) {
        Map<String, Integer> keys = new HashMap<>();
        
        for (Crate crate : plugin.getCrateManager().getAllCrates().values()) {
            int amount = getVirtualKeys(playerUuid, crate.getId());
            keys.put(crate.getId(), amount);
        }
        
        return keys;
    }
}
