package dev.ashu16.oakcrates.gui;

import dev.ashu16.oakcrates.OakCrates;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CratePreviewGUI extends GUIHolder {

    private final Crate crate;
    private int page;
    private final List<Reward> sortedRewards;
    private final int itemsPerPage;
    
    private static final int[] REWARD_SLOTS_27 = {10, 11, 12, 13, 14, 15, 16};
    private static final int[] REWARD_SLOTS_36 = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
    private static final int[] REWARD_SLOTS_45 = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
    private static final int[] REWARD_SLOTS_54 = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

    public CratePreviewGUI(OakCrates plugin, Player player, Crate crate) {
        this(plugin, player, crate, 0);
    }

    public CratePreviewGUI(OakCrates plugin, Player player, Crate crate, int page) {
        super(plugin, player);
        this.crate = crate;
        this.page = page;
        
        this.sortedRewards = new ArrayList<>(crate.getRewards().values());
        this.sortedRewards.sort(Comparator.comparingInt(Reward::getSlot));
        
        int guiSize = crate.getGuiSize();
        this.itemsPerPage = getRewardSlots(guiSize).length;
        
        String title = MessageUtil.getMessage("preview-title");
        title = PlaceholderUtil.parse(title, player, crate);
        
        createInventory(title, guiSize);
    }

    private int[] getRewardSlots(int guiSize) {
        if (guiSize <= 27) return REWARD_SLOTS_27;
        if (guiSize <= 36) return REWARD_SLOTS_36;
        if (guiSize <= 45) return REWARD_SLOTS_45;
        return REWARD_SLOTS_54;
    }

    @Override
    protected void setupInventory() {
        fillBorders();
        
        int[] rewardSlots = getRewardSlots(crate.getGuiSize());
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, sortedRewards.size());
        
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex && slotIndex < rewardSlots.length; i++) {
            Reward reward = sortedRewards.get(i);
            ItemStack displayItem = createPreviewItem(reward);
            if (displayItem != null) {
                inventory.setItem(rewardSlots[slotIndex], displayItem);
            }
            slotIndex++;
        }
        
        int bottomRow = inventory.getSize() - 9;
        int totalPages = getTotalPages();
        
        if (page > 0) {
            inventory.setItem(bottomRow, createPrevPageButton());
        }
        
        inventory.setItem(bottomRow + 4, getCloseButton());
        
        if (page < totalPages - 1) {
            inventory.setItem(bottomRow + 8, createNextPageButton());
        }
        
        if (totalPages > 1) {
            inventory.setItem(bottomRow + 4, createInfoButton(totalPages));
        }
    }

    private int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) sortedRewards.size() / itemsPerPage));
    }

    private ItemStack createPreviewItem(Reward reward) {
        ItemStack original = reward.getDisplayItem();
        if (original == null || original.getType() == Material.AIR) {
            return new ItemBuilder(Material.CHEST)
                    .name("&e" + reward.getId())
                    .lore(Arrays.asList(
                        "&8━━━━━━━━━━━━━━━━━━━━",
                        "&7Click to claim!",
                        "&8━━━━━━━━━━━━━━━━━━━━"
                    ))
                    .build();
        }
        
        ItemStack item = original.clone();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            
            lore.add("");
            lore.add(ColorUtil.colorize("&8━━━━━━━━━━━━━━━━━━━━"));
            String previewInfo = MessageUtil.getMessage("preview-lore-info");
            lore.add(ColorUtil.colorize(previewInfo));
            lore.add(ColorUtil.colorize("&8━━━━━━━━━━━━━━━━━━━━"));
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    private ItemStack createPrevPageButton() {
        return new ItemBuilder(Material.ARROW)
                .name("&a◀ Previous Page")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Click to go to the",
                    "&7previous page of rewards.",
                    "&8━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    private ItemStack createNextPageButton() {
        return new ItemBuilder(Material.ARROW)
                .name("&a▶ Next Page")
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Click to go to the",
                    "&7next page of rewards.",
                    "&8━━━━━━━━━━━━━━━━━━━━"
                ))
                .build();
    }

    private ItemStack createInfoButton(int totalPages) {
        return new ItemBuilder(Material.NETHER_STAR)
                .name("&e&l" + ColorUtil.colorize(crate.getDisplayName()))
                .lore(Arrays.asList(
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "&7Total Rewards: &a" + sortedRewards.size(),
                    "&7Page: &a" + (page + 1) + "&7/&a" + totalPages,
                    "&8━━━━━━━━━━━━━━━━━━━━",
                    "",
                    "&c▸ Click to close"
                ))
                .glow(true)
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        if (isFillerSlot(slot)) return;
        
        int bottomRow = inventory.getSize() - 9;
        int totalPages = getTotalPages();
        
        if (slot == bottomRow && page > 0) {
            SoundUtil.playConfirm(player);
            page--;
            refresh();
            return;
        }
        
        if (slot == bottomRow + 8 && page < totalPages - 1) {
            SoundUtil.playConfirm(player);
            page++;
            refresh();
            return;
        }
        
        if (slot == bottomRow + 4) {
            player.closeInventory();
            return;
        }
        
        int[] rewardSlots = getRewardSlots(crate.getGuiSize());
        for (int i = 0; i < rewardSlots.length; i++) {
            if (rewardSlots[i] == slot) {
                int rewardIndex = (page * itemsPerPage) + i;
                if (rewardIndex < sortedRewards.size()) {
                    Reward reward = sortedRewards.get(rewardIndex);
                    SoundUtil.playConfirm(player);
                    MessageUtil.sendWithPrefix(player, "&7This is the &e" + reward.getDisplayName() + " &7reward!");
                    return;
                }
            }
        }
    }
}
