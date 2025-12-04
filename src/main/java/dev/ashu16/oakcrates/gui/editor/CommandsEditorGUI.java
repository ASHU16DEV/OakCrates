package dev.ashu16.oakcrates.gui.editor;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.GUIHolder;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.Reward;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.ItemBuilder;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class CommandsEditorGUI extends GUIHolder {

    private final Crate crate;
    private final Reward reward;
    private int page;
    
    private static final int ITEMS_PER_PAGE = 28;
    private static final int ADD_COMMAND_SLOT = 49;
    private static final int PREV_PAGE_SLOT = 45;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int BACK_SLOT = 48;
    private static final int CLOSE_SLOT = 50;

    public CommandsEditorGUI(OakCrates plugin, Player player, Crate crate, Reward reward, int page) {
        super(plugin, player);
        this.crate = crate;
        this.reward = reward;
        this.page = page;
        
        createInventory("&8Commands: " + reward.getId(), 54);
    }

    @Override
    protected void setupInventory() {
        fillBorders();
        
        List<String> commands = reward.getCommands();
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, commands.size());
        
        int slot = 10;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot % 9 == 0) slot++;
            if (slot % 9 == 8) slot += 2;
            if (slot >= 44) break;
            
            inventory.setItem(slot, createCommandItem(i, commands.get(i)));
            slot++;
        }
        
        inventory.setItem(ADD_COMMAND_SLOT, createAddCommandButton());
        inventory.setItem(BACK_SLOT, getBackButton());
        inventory.setItem(CLOSE_SLOT, getCloseButton());
        
        if (page > 0) {
            inventory.setItem(PREV_PAGE_SLOT, getPrevPageButton());
        }
        
        int totalPages = (int) Math.ceil((double) commands.size() / ITEMS_PER_PAGE);
        if (page < totalPages - 1) {
            inventory.setItem(NEXT_PAGE_SLOT, getNextPageButton());
        }
    }

    private ItemStack createCommandItem(int index, String command) {
        String displayCommand = command.length() > 40 ? command.substring(0, 37) + "..." : command;
        
        return new ItemBuilder(Material.COMMAND_BLOCK)
                .name("&e&lCommand " + (index + 1))
                .lore(Arrays.asList(
                    "&7" + displayCommand,
                    "",
                    "&aLeft-click: Edit command",
                    "&cRight-click: Delete command"
                ))
                .build();
    }

    private ItemStack createAddCommandButton() {
        return new ItemBuilder(Material.LIME_DYE)
                .name("&a&lAdd New Command")
                .lore(Arrays.asList(
                    "&7Click to add a new command.",
                    "",
                    "&7Use %player% for player name.",
                    "&7Commands run from console.",
                    "",
                    "&7Current commands: &a" + reward.getCommands().size()
                ))
                .build();
    }

    @Override
    public void handleClick(Player player, int slot, ClickType clickType) {
        if (isFillerSlot(slot)) return;
        
        SoundUtil.playConfirm(player);
        
        if (slot == ADD_COMMAND_SLOT) {
            handleAddCommand();
            return;
        }
        
        if (slot == BACK_SLOT) {
            player.closeInventory();
            new RewardEditGUI(plugin, player, crate, reward).open();
            return;
        }
        
        if (slot == CLOSE_SLOT) {
            player.closeInventory();
            return;
        }
        
        if (slot == PREV_PAGE_SLOT && page > 0) {
            page--;
            refresh();
            return;
        }
        
        int totalPages = (int) Math.ceil((double) reward.getCommands().size() / ITEMS_PER_PAGE);
        if (slot == NEXT_PAGE_SLOT && page < totalPages - 1) {
            page++;
            refresh();
            return;
        }
        
        int commandIndex = getCommandIndexAtSlot(slot);
        if (commandIndex >= 0 && commandIndex < reward.getCommands().size()) {
            if (clickType == ClickType.LEFT) {
                handleEditCommand(commandIndex);
            } else if (clickType == ClickType.RIGHT) {
                reward.removeCommand(commandIndex);
                plugin.getCrateManager().saveCrate(crate);
                refresh();
            }
        }
    }

    private void handleAddCommand() {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&eEnter the new command (without /):",
            input -> {
                reward.addCommand(input);
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&aCommand added!");
                new CommandsEditorGUI(plugin, player, crate, reward, page).open();
            },
            () -> new CommandsEditorGUI(plugin, player, crate, reward, page).open()
        );
    }

    private void handleEditCommand(int index) {
        player.closeInventory();
        plugin.getChatInputManager().requestInput(player,
            "&eEnter the new command (without /):",
            input -> {
                reward.getCommands().set(index, input);
                plugin.getCrateManager().saveCrate(crate);
                MessageUtil.sendWithPrefix(player, "&aCommand updated!");
                new CommandsEditorGUI(plugin, player, crate, reward, page).open();
            },
            () -> new CommandsEditorGUI(plugin, player, crate, reward, page).open()
        );
    }

    private int getCommandIndexAtSlot(int clickedSlot) {
        int startIndex = page * ITEMS_PER_PAGE;
        int slot = 10;
        
        for (int i = startIndex; i < reward.getCommands().size() && slot < 44; i++) {
            if (slot % 9 == 0) slot++;
            if (slot % 9 == 8) slot += 2;
            
            if (slot == clickedSlot) {
                return i;
            }
            slot++;
        }
        return -1;
    }
}
