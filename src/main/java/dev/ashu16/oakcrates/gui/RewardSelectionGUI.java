package dev.ashu16.oakcrates.gui;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.Reward;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.PlaceholderUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RewardSelectionGUI extends GUIHolder {

    private final Crate crate;
    private final Location crateLocation;

    public RewardSelectionGUI(OakCrates plugin, Player player, Crate crate) {
        this(plugin, player, crate, null);
    }

    public RewardSelectionGUI(OakCrates plugin, Player player, Crate crate, Location crateLocation) {
        super(plugin, player);
        this.crate = crate;
        this.crateLocation = crateLocation;
        
        String title = MessageUtil.getMessage("selection-title");
        title = PlaceholderUtil.parse(title, player, crate);
        
        createInventory(title, crate.getGuiSize());
    }

    @Override
    protected void setupInventory() {
        fillBorders();
        
        for (Map.Entry<String, Reward> entry : crate.getRewards().entrySet()) {
            Reward reward = entry.getValue();
            int slot = reward.getSlot();
            
            if (slot >= 0 && slot < inventory.getSize()) {
                ItemStack displayItem = createSelectionItem(reward);
                inventory.setItem(slot, displayItem);
            }
        }
        
        int closeSlot = plugin.getConfigManager().getConfig().getInt("gui.close-button.slot", 49);
        if (closeSlot < inventory.getSize()) {
            inventory.setItem(closeSlot, getCloseButton());
        }
    }

    private ItemStack createSelectionItem(Reward reward) {
        ItemStack original = reward.getDisplayItem();
        if (original == null) return null;
        
        ItemStack item = original.clone();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            
            lore.add("");
            
            if (canClaim(reward)) {
                String clickToClaim = MessageUtil.getMessage("selection-click-to-claim");
                lore.add(ColorUtil.colorize(clickToClaim));
            } else {
                String limitReached = MessageUtil.getMessage("reward-limit-reached");
                lore.add(ColorUtil.colorize("&c" + limitReached));
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    private boolean canClaim(Reward reward) {
        if (reward.hasPlayerLimit()) {
            int claims = plugin.getDatabaseManager().getPlayerClaimCount(
                    player.getUniqueId(), crate.getId(), reward.getId());
            if (claims >= reward.getPerPlayerClaimLimit()) {
                return false;
            }
        }
        
        if (reward.hasGlobalLimit()) {
            int globalClaims = plugin.getDatabaseManager().getGlobalClaimCount(crate.getId(), reward.getId());
            if (globalClaims >= reward.getGlobalClaimLimit()) {
                return false;
            }
        }
        
        if (reward.hasPermission()) {
            if (!player.hasPermission(reward.getPermissionRequired())) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        int closeSlot = plugin.getConfigManager().getConfig().getInt("gui.close-button.slot", 49);
        
        if (slot == closeSlot) {
            player.closeInventory();
            return;
        }
        
        for (Map.Entry<String, Reward> entry : crate.getRewards().entrySet()) {
            Reward reward = entry.getValue();
            if (reward.getSlot() == slot) {
                handleRewardClick(reward);
                return;
            }
        }
    }

    private void handleRewardClick(Reward reward) {
        if (!canClaim(reward)) {
            if (reward.hasPlayerLimit()) {
                int claims = plugin.getDatabaseManager().getPlayerClaimCount(
                        player.getUniqueId(), crate.getId(), reward.getId());
                if (claims >= reward.getPerPlayerClaimLimit()) {
                    MessageUtil.sendMessage(player, "reward-limit-reached");
                    return;
                }
            }
            
            if (reward.hasGlobalLimit()) {
                int globalClaims = plugin.getDatabaseManager().getGlobalClaimCount(crate.getId(), reward.getId());
                if (globalClaims >= reward.getGlobalClaimLimit()) {
                    MessageUtil.sendMessage(player, "global-limit-reached");
                    return;
                }
            }
            
            if (reward.hasPermission()) {
                MessageUtil.sendMessage(player, "permission-required");
                return;
            }
            return;
        }
        
        player.closeInventory();
        new ConfirmGUI(plugin, player, crate, reward, crateLocation).open();
    }

    public Location getCrateLocation() {
        return crateLocation;
    }
}
