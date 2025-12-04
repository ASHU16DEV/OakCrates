package dev.ashu16.oakcrates.models;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Reward {

    private String id;
    private int slot;
    private ItemStack displayItem;
    private int perPlayerClaimLimit;
    private int globalClaimLimit;
    private String permissionRequired;
    private List<String> commands;
    private List<RewardItem> items;

    public Reward(String id) {
        this.id = id;
        this.slot = 0;
        this.displayItem = null;
        this.perPlayerClaimLimit = 0;
        this.globalClaimLimit = 0;
        this.permissionRequired = "";
        this.commands = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public String getDisplayName() {
        if (displayItem != null && displayItem.hasItemMeta() && displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        return id;
    }

    public int getPerPlayerClaimLimit() {
        return perPlayerClaimLimit;
    }

    public void setPerPlayerClaimLimit(int perPlayerClaimLimit) {
        this.perPlayerClaimLimit = perPlayerClaimLimit;
    }

    public int getGlobalClaimLimit() {
        return globalClaimLimit;
    }

    public void setGlobalClaimLimit(int globalClaimLimit) {
        this.globalClaimLimit = globalClaimLimit;
    }

    public String getPermissionRequired() {
        return permissionRequired;
    }

    public void setPermissionRequired(String permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void removeCommand(int index) {
        if (index >= 0 && index < commands.size()) {
            commands.remove(index);
        }
    }

    public List<RewardItem> getItems() {
        return items;
    }

    public void setItems(List<RewardItem> items) {
        this.items = items;
    }

    public void addItem(RewardItem item) {
        items.add(item);
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public boolean hasCommands() {
        return commands != null && !commands.isEmpty();
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public boolean hasPermission() {
        return permissionRequired != null && !permissionRequired.isEmpty();
    }

    public boolean hasPlayerLimit() {
        return perPlayerClaimLimit > 0;
    }

    public boolean hasGlobalLimit() {
        return globalClaimLimit > 0;
    }
}
