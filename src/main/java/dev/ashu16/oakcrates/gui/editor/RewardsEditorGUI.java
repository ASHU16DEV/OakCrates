package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.Reward;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.PlaceholderUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class RewardsEditorGUI extends GUIHolder {

    private final Crate crate;
    private final int page;
    private final List<String> rewardIds;
    
    private static final int ITEMS_PER_PAGE = 21;
    private static final int ADD_REWARD_SLOT = 49;
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int BACK_SLOT = 47;
    private static final int CLOSE_SLOT = 51;
    private static final int INFO_SLOT = 4;

    public RewardsEditorGUI(OakCrates plugin, Player player, Crate crate, int page) {
        super(plugin, player);
        this.crate = crate;
        this.page = page;
        this.rewardIds = crate.getRewards().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        String title = MessageUtil.getMessage("admin-rewards-title");
        title = PlaceholderUtil.parse(title, player, crate);
        
        createInventory(title, 54);
    }

    @Override
    protected void setupInventory() {
        fillBorders();
        
        inventory.setItem(INFO_SLOT, createInfoItem());
        
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, rewardIds.size());
        
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int slotIndex = 0;
        
        for (int i = startIndex; i < endIndex && slotIndex < slots.length; i++) {
            String rewardId = rewardIds.get(i);
            Reward reward = crate.getReward(rewardId);
            
            if (reward != null) {
                inventory.setItem(slots[slotIndex], createRewardItem(rewardId, reward));
                slotIndex++;
            }
        }
        
        inventory.setItem(ADD_REWARD_SLOT, createAddRewardButton());
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
        
        if (page > 0) {
            inventory.setItem(PREV_PAGE_SLOT, getPrevPageButton());
        } else {
            inventory.setItem(PREV_PAGE_SLOT, createDisabledButton("&7No Previous Page"));
        }
        
        int totalPages = (int) Math.ceil((double) rewardIds.size() / ITEMS_PER_PAGE);
        if (page < totalPages - 1) {
            inventory.setItem(NEXT_PAGE_SLOT, getNextPageButton());
        } else {
            inventory.setItem(NEXT_PAGE_SLOT, createDisabledButton("&7No Next Page"));
        }
    }

    private ItemStack createInfoItem() {
        int totalPages = Math.max(1, (int) Math.ceil((double) rewardIds.size() / ITEMS_PER_PAGE));
        return new ItemBuilder(Material.BOOK)
                .name("&e&lRewards Overview")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Crate: &f" + ColorUtil.colorize(crate.getDisplayName()),
                    "&7Total Rewards: &a" + rewardIds.size(),
                    "&7Page: &a" + (page + 1) + "&7/&a" + totalPages,
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&7Manage all rewards for",
                    "&7this crate from here."
                ))
                .build();
    }

    private ItemStack createRewardItem(String rewardId, Reward reward) {
        if (reward == null) {
            return new ItemBuilder(Material.BARRIER)
                    .name("&c&lInvalid Reward")
                    .lore(Arrays.asList(
                        "&7ID: &f" + rewardId,
                        "&cThis reward data is corrupted.",
                        "",
                        "&cRight-click to delete"
                    ))
                    .build();
        }
        
        ItemStack display = reward.getDisplayItem();
        if (display == null || display.getType() == Material.AIR) {
            display = new ItemBuilder(Material.CHEST)
                    .name("&e" + rewardId)
                    .build();
        } else {
            display = display.clone();
        }
        
        ItemMeta meta = display.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(ColorUtil.colorize("&8━━━━━━━━━━━━━━━━━━━━"));
            lore.add(ColorUtil.colorize("&7ID: &f" + rewardId));
            lore.add(ColorUtil.colorize("&7Slot: &a" + reward.getSlot()));
            lore.add(ColorUtil.colorize("&7Items: &a" + reward.getItems().size()));
            lore.add(ColorUtil.colorize("&7Commands: &a" + reward.getCommands().size()));
            lore.add(ColorUtil.colorize("&7Player Limit: &f" + (reward.getPerPlayerClaimLimit() == 0 ? "&aUnlimited" : "&e" + reward.getPerPlayerClaimLimit())));
            lore.add(ColorUtil.colorize("&7Global Limit: &f" + (reward.getGlobalClaimLimit() == 0 ? "&aUnlimited" : "&e" + reward.getGlobalClaimLimit())));
            lore.add(ColorUtil.colorize("&8━━━━━━━━━━━━━━━━━━━━"));
            lore.add(ColorUtil.colorize("&a▸ Left-click to edit"));
            lore.add(ColorUtil.colorize("&c▸ Right-click to delete"));
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }

    private ItemStack createAddRewardButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&l✚ Add New Reward")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Click to create a new",
                    "&7reward for this crate.",
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&a▸ Click to add"
                ))
                .glow(true)
                .build();
    }

    private ItemStack createDisabledButton(String name) {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(name)
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        if (isFillerSlot(slot)) return;
        
        SoundUtil.playConfirm(player);
        
        if (slot == ADD_REWARD_SLOT) {
            player.closeInventory();
            plugin.getChatInputManager().requestInput(player, 
                "&e&lEnter the ID for the new reward:",
                input -> {
                    String rewardId = input.toLowerCase().replace(" ", "_");
                    if (crate.getRewards().containsKey(rewardId)) {
                        MessageUtil.sendWithPrefix(player, "&c✘ A reward with that ID already exists!");
                        new RewardsEditorGUI(plugin, player, crate, page).open();
                        return;
                    }
                    
                    Reward reward = new Reward(rewardId);
                    reward.setDisplayItem(new ItemBuilder(Material.CHEST)
                            .name("&e" + rewardId)
                            .lore(Arrays.asList("&7A new reward", "&7Edit to customize"))
                            .build());
                    reward.setSlot(findNextAvailableSlot());
                    
                    crate.addReward(rewardId, reward);
                    plugin.getCrateManager().saveCrate(crate);
                    
                    MessageUtil.sendWithPrefix(player, "&a✔ Reward '&e" + rewardId + "&a' created successfully!");
                    new RewardEditGUI(plugin, player, crate, reward).open();
                },
                () -> new RewardsEditorGUI(plugin, player, crate, page).open()
            );
            return;
        }
        
        if (slot == BACK_SLOT) {
            player.closeInventory();
            new CrateEditGUI(plugin, player, crate).open();
            return;
        }
        
        if (slot == CLOSE_SLOT) {
            player.closeInventory();
            return;
        }
        
        if (slot == PREV_PAGE_SLOT && page > 0) {
            player.closeInventory();
            new RewardsEditorGUI(plugin, player, crate, page - 1).open();
            return;
        }
        
        int totalPages = (int) Math.ceil((double) rewardIds.size() / ITEMS_PER_PAGE);
        if (slot == NEXT_PAGE_SLOT && page < totalPages - 1) {
            player.closeInventory();
            new RewardsEditorGUI(plugin, player, crate, page + 1).open();
            return;
        }
        
        String clickedRewardId = getRewardIdAtSlot(slot);
        if (clickedRewardId != null) {
            Reward reward = crate.getReward(clickedRewardId);
            if (clickType == ClickType.LEFT) {
                if (reward != null) {
                    player.closeInventory();
                    new RewardEditGUI(plugin, player, crate, reward).open();
                }
            } else if (clickType == ClickType.RIGHT) {
                crate.removeReward(clickedRewardId);
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&c✔ Reward '&e" + clickedRewardId + "&c' deleted!");
                player.closeInventory();
                new RewardsEditorGUI(plugin, player, crate, 0).open();
            }
        }
    }

    private int findNextAvailableSlot() {
        Set<Integer> usedSlots = new HashSet<>();
        for (Reward reward : crate.getRewards().values()) {
            if (reward != null) {
                usedSlots.add(reward.getSlot());
            }
        }
        
        for (int i = 10; i < crate.getGuiSize() - 9; i++) {
            if (i % 9 != 0 && i % 9 != 8 && !usedSlots.contains(i)) {
                return i;
            }
        }
        return 10;
    }

    private String getRewardIdAtSlot(int clickedSlot) {
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int startIndex = page * ITEMS_PER_PAGE;
        
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == clickedSlot) {
                int rewardIndex = startIndex + i;
                if (rewardIndex < rewardIds.size()) {
                    return rewardIds.get(rewardIndex);
                }
            }
        }
        return null;
    }
}
