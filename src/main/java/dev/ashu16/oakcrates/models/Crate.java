package dev.ashu16.oakcrates.models;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crate {

    private String id;
    private String displayName;
    private int guiSize;
    private KeyType keyType;
    private int maxRewardsPerKey;
    private int perPlayerTotalClaimLimit;
    private PhysicalKey physicalKey;
    private HologramSettings hologramSettings;
    private List<String> boundBlocks;
    private Map<String, Reward> rewards;
    private CrateAnimation animation;
    private String openSound;
    private String closeSound;

    public Crate(String id) {
        this.id = id;
        this.displayName = "&7" + id;
        this.guiSize = 27;
        this.keyType = KeyType.VIRTUAL;
        this.maxRewardsPerKey = 1;
        this.perPlayerTotalClaimLimit = 0;
        this.physicalKey = new PhysicalKey();
        this.hologramSettings = new HologramSettings();
        this.boundBlocks = new ArrayList<>();
        this.rewards = new HashMap<>();
        this.animation = CrateAnimation.NONE;
        this.openSound = "BLOCK_ENDER_CHEST_OPEN";
        this.closeSound = "BLOCK_ENDER_CHEST_CLOSE";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getGuiSize() {
        return guiSize;
    }

    public void setGuiSize(int guiSize) {
        this.guiSize = Math.min(54, Math.max(9, (guiSize / 9) * 9));
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public int getMaxRewardsPerKey() {
        return maxRewardsPerKey;
    }

    public void setMaxRewardsPerKey(int maxRewardsPerKey) {
        this.maxRewardsPerKey = maxRewardsPerKey;
    }

    public int getPerPlayerTotalClaimLimit() {
        return perPlayerTotalClaimLimit;
    }

    public void setPerPlayerTotalClaimLimit(int perPlayerTotalClaimLimit) {
        this.perPlayerTotalClaimLimit = perPlayerTotalClaimLimit;
    }

    public PhysicalKey getPhysicalKey() {
        return physicalKey;
    }

    public void setPhysicalKey(PhysicalKey physicalKey) {
        this.physicalKey = physicalKey;
    }

    public HologramSettings getHologramSettings() {
        return hologramSettings;
    }

    public void setHologramSettings(HologramSettings hologramSettings) {
        this.hologramSettings = hologramSettings;
    }

    public List<String> getBoundBlocks() {
        return boundBlocks;
    }

    public void setBoundBlocks(List<String> boundBlocks) {
        this.boundBlocks = boundBlocks;
    }

    public void addBoundBlock(Location location) {
        String blockString = location.getWorld().getName() + ":" + 
                            location.getBlockX() + ":" + 
                            location.getBlockY() + ":" + 
                            location.getBlockZ();
        if (!boundBlocks.contains(blockString)) {
            boundBlocks.add(blockString);
        }
    }

    public void removeBoundBlock(Location location) {
        String blockString = location.getWorld().getName() + ":" + 
                            location.getBlockX() + ":" + 
                            location.getBlockY() + ":" + 
                            location.getBlockZ();
        boundBlocks.remove(blockString);
    }

    public boolean isBoundBlock(Location location) {
        String blockString = location.getWorld().getName() + ":" + 
                            location.getBlockX() + ":" + 
                            location.getBlockY() + ":" + 
                            location.getBlockZ();
        return boundBlocks.contains(blockString);
    }

    public Map<String, Reward> getRewards() {
        return rewards;
    }

    public void setRewards(Map<String, Reward> rewards) {
        this.rewards = rewards;
    }

    public Reward getReward(String rewardId) {
        return rewards.get(rewardId);
    }

    public void addReward(String rewardId, Reward reward) {
        rewards.put(rewardId, reward);
    }

    public void removeReward(String rewardId) {
        rewards.remove(rewardId);
    }

    public CrateAnimation getAnimation() {
        return animation != null ? animation : CrateAnimation.NONE;
    }

    public void setAnimation(CrateAnimation animation) {
        this.animation = animation;
    }

    public String getOpenSound() {
        return openSound != null ? openSound : "BLOCK_ENDER_CHEST_OPEN";
    }

    public void setOpenSound(String openSound) {
        this.openSound = openSound;
    }

    public String getCloseSound() {
        return closeSound != null ? closeSound : "BLOCK_ENDER_CHEST_CLOSE";
    }

    public void setCloseSound(String closeSound) {
        this.closeSound = closeSound;
    }

    public boolean acceptsVirtualKeys() {
        return keyType == KeyType.VIRTUAL || keyType == KeyType.BOTH;
    }

    public boolean acceptsPhysicalKeys() {
        return keyType == KeyType.PHYSICAL || keyType == KeyType.BOTH;
    }

    public enum KeyType {
        VIRTUAL,
        PHYSICAL,
        BOTH
    }
}
