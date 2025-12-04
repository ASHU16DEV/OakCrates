package dev.ashu16.oakcrates.gui;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.CrateAnimation;
import dev.ashu16.oakcrates.models.Reward;
import dev.ashu16.oakcrates.models.RewardItem;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.PlaceholderUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmGUI extends GUIHolder {

    private final Crate crate;
    private final Reward reward;
    private final Location crateLocation;
    
    private static final int CONFIRM_SLOT = 11;
    private static final int DISPLAY_SLOT = 13;
    private static final int CANCEL_SLOT = 15;

    public ConfirmGUI(OakCrates plugin, Player player, Crate crate, Reward reward) {
        this(plugin, player, crate, reward, null);
    }

    public ConfirmGUI(OakCrates plugin, Player player, Crate crate, Reward reward, Location crateLocation) {
        super(plugin, player);
        this.crate = crate;
        this.reward = reward;
        this.crateLocation = crateLocation;
        
        String title = MessageUtil.getMessage("confirm-title");
        createInventory(title, 27);
    }

    @Override
    protected void setupInventory() {
        fillEmpty();
        
        inventory.setItem(CONFIRM_SLOT, getConfirmButton());
        inventory.setItem(DISPLAY_SLOT, getDisplayItem());
        inventory.setItem(CANCEL_SLOT, getCancelButton());
    }

    private ItemStack getConfirmButton() {
        String materialName = plugin.getConfigManager().getMessages().getString("confirm-yes.material", "LIME_STAINED_GLASS_PANE");
        String name = plugin.getConfigManager().getMessages().getString("confirm-yes.name", "&a&l✔ CONFIRM");
        List<String> lore = plugin.getConfigManager().getMessages().getStringList("confirm-yes.lore");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.LIME_STAINED_GLASS_PANE;
        }
        
        return new ItemBuilder(material)
                .name(name)
                .lore(lore)
                .build();
    }

    private ItemStack getCancelButton() {
        String materialName = plugin.getConfigManager().getMessages().getString("confirm-no.material", "RED_STAINED_GLASS_PANE");
        String name = plugin.getConfigManager().getMessages().getString("confirm-no.name", "&c&l✘ CANCEL");
        List<String> lore = plugin.getConfigManager().getMessages().getStringList("confirm-no.lore");
        
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.RED_STAINED_GLASS_PANE;
        }
        
        return new ItemBuilder(material)
                .name(name)
                .lore(lore)
                .build();
    }

    private ItemStack getDisplayItem() {
        ItemStack item = reward.getDisplayItem();
        if (item == null) {
            return new ItemBuilder(Material.CHEST)
                    .name("&eReward")
                    .build();
        }
        
        ItemStack display = item.clone();
        
        List<String> additionalLore = plugin.getConfigManager().getMessages().getStringList("confirm-reward-display.lore");
        if (!additionalLore.isEmpty()) {
            List<String> lore = display.hasItemMeta() && display.getItemMeta().hasLore() 
                    ? new ArrayList<>(display.getItemMeta().getLore()) 
                    : new ArrayList<>();
            
            lore.add("");
            for (String line : additionalLore) {
                line = PlaceholderUtil.parseReward(line, player, crate, reward.getDisplayName());
                lore.add(ColorUtil.colorize(line));
            }
            
            ItemBuilder builder = new ItemBuilder(display);
            builder.lore(lore);
            return builder.build();
        }
        
        return display;
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        if (slot == CONFIRM_SLOT) {
            confirmClaim();
        } else if (slot == CANCEL_SLOT) {
            SoundUtil.playCancel(player);
            player.closeInventory();
            new RewardSelectionGUI(plugin, player, crate).open();
        }
    }

    private void confirmClaim() {
        if (!plugin.getKeyManager().hasKey(player, crate.getId())) {
            MessageUtil.sendMessage(player, "no-key-chat");
            player.closeInventory();
            return;
        }
        
        if (reward.hasItems() && !hasInventorySpace()) {
            MessageUtil.sendMessage(player, "inventory-full");
            SoundUtil.playCancel(player);
            return;
        }
        
        if (!plugin.getKeyManager().consumeKey(player, crate.getId())) {
            MessageUtil.sendMessage(player, "no-key-chat");
            player.closeInventory();
            return;
        }
        
        player.closeInventory();
        
        CrateAnimation animation = crate.getAnimation();
        Location animLocation = crateLocation != null ? crateLocation : player.getLocation();
        
        if (animation != null && animation != CrateAnimation.NONE && animLocation != null) {
            plugin.getAnimationExecutor().playAnimation(player, animation, animLocation, this::completeRewardClaim);
        } else {
            completeRewardClaim();
        }
    }
    
    private boolean hasInventorySpace() {
        if (!reward.hasItems()) {
            return true;
        }
        
        ItemStack[] contents = player.getInventory().getStorageContents();
        ItemStack[] clonedContents = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            clonedContents[i] = contents[i] != null ? contents[i].clone() : null;
        }
        
        for (RewardItem rewardItem : reward.getItems()) {
            ItemStack itemToAdd = rewardItem.build();
            int remaining = itemToAdd.getAmount();
            
            for (int i = 0; i < clonedContents.length && remaining > 0; i++) {
                ItemStack slot = clonedContents[i];
                if (slot != null && slot.isSimilar(itemToAdd)) {
                    int canAdd = slot.getMaxStackSize() - slot.getAmount();
                    if (canAdd > 0) {
                        int toAdd = Math.min(canAdd, remaining);
                        slot.setAmount(slot.getAmount() + toAdd);
                        remaining -= toAdd;
                    }
                }
            }
            
            for (int i = 0; i < clonedContents.length && remaining > 0; i++) {
                if (clonedContents[i] == null || clonedContents[i].getType() == Material.AIR) {
                    int toAdd = Math.min(itemToAdd.getMaxStackSize(), remaining);
                    ItemStack newItem = itemToAdd.clone();
                    newItem.setAmount(toAdd);
                    clonedContents[i] = newItem;
                    remaining -= toAdd;
                }
            }
            
            if (remaining > 0) {
                return false;
            }
        }
        
        return true;
    }

    private void completeRewardClaim() {
        giveReward();
        
        plugin.getDatabaseManager().incrementPlayerClaimCount(player.getUniqueId(), crate.getId(), reward.getId());
        plugin.getDatabaseManager().incrementGlobalClaimCount(crate.getId(), reward.getId());
        
        plugin.getClaimLogManager().logClaim(player, crate, reward);
        
        if (plugin.getDiscordManager() != null && plugin.getDiscordManager().isEnabled()) {
            plugin.getDiscordManager().sendRewardClaimNotification(player, crate, reward);
        }
        
        SoundUtil.playSelectReward(player);
        
        Map<String, String> placeholders = MessageUtil.createPlaceholders(
            "%reward_name%", reward.getDisplayName(),
            "%crate_name%", crate.getId(),
            "%crate_display_name%", ColorUtil.colorize(crate.getDisplayName())
        );
        MessageUtil.sendMessage(player, "reward-claimed", placeholders);
    }

    private void giveReward() {
        if (reward.hasCommands()) {
            for (String command : reward.getCommands()) {
                command = command.replace("%player%", player.getName());
                command = PlaceholderUtil.parse(command, player, crate);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
        
        if (reward.hasItems()) {
            for (RewardItem rewardItem : reward.getItems()) {
                ItemStack item = rewardItem.build();
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                
                for (ItemStack drop : leftover.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                }
            }
        }
    }
}
