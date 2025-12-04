package dev.ashu16.oakcrates.commands;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.gui.editor.CrateEditGUI;
import dev.ashu16.oakcrates.listeners.BlockInteractListener;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.util.*;
import java.util.stream.Collectors;

public class OakCratesCommand implements CommandExecutor, TabCompleter {

    private final OakCrates plugin;
    private final Set<UUID> pendingDeletes;

    public OakCratesCommand(OakCrates plugin) {
        this.plugin = plugin;
        this.pendingDeletes = new HashSet<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(sender);
                break;
            case "create":
                handleCreate(sender, args);
                break;
            case "delete":
                handleDelete(sender, args);
                break;
            case "confirm":
                handleConfirm(sender);
                break;
            case "list":
                handleList(sender);
                break;
            case "edit":
                handleEdit(sender, args);
                break;
            case "setblock":
                handleSetBlock(sender, args);
                break;
            case "removeblock":
                handleRemoveBlock(sender, args);
                break;
            case "givekey":
                handleGiveKey(sender, args);
                break;
            case "takekey":
                handleTakeKey(sender, args);
                break;
            case "setkey":
                handleSetKey(sender, args);
                break;
            case "keys":
                handleViewKeys(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "discord":
                handleDiscord(sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        if (!sender.hasPermission("oakcrates.admin.help")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        List<String> header = MessageUtil.getMessageList("help-header");
        MessageUtil.send(sender, ColorUtil.colorize(header));

        String format = MessageUtil.getMessage("help-format");
        
        sendHelpLine(sender, format, "oakcrates create <id>", "Create a new crate");
        sendHelpLine(sender, format, "oakcrates delete <id>", "Delete a crate");
        sendHelpLine(sender, format, "oakcrates list", "List all crates");
        sendHelpLine(sender, format, "oakcrates edit <id>", "Open crate edit GUI");
        sendHelpLine(sender, format, "oakcrates setblock <id>", "Bind block to crate");
        sendHelpLine(sender, format, "oakcrates removeblock <id>", "Unbind blocks from crate");
        sendHelpLine(sender, format, "oakcrates givekey <player> <crate> <amount>", "Give keys");
        sendHelpLine(sender, format, "oakcrates takekey <player> <crate> <amount>", "Take keys");
        sendHelpLine(sender, format, "oakcrates setkey <player> <crate> <amount>", "Set keys");
        sendHelpLine(sender, format, "oakcrates keys <player> [crate]", "View player keys");
        sendHelpLine(sender, format, "oakcrates discord setchannel <id>", "Set Discord channel");
        sendHelpLine(sender, format, "oakcrates reload", "Reload configuration");

        List<String> footer = MessageUtil.getMessageList("help-footer");
        MessageUtil.send(sender, ColorUtil.colorize(footer));
    }

    private void sendHelpLine(CommandSender sender, String format, String cmd, String desc) {
        String line = format.replace("%command%", cmd).replace("%description%", desc);
        MessageUtil.send(sender, line);
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.create")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates create <id>");
            return;
        }

        String crateId = args[1].toLowerCase();
        
        if (plugin.getCrateManager().exists(crateId)) {
            MessageUtil.sendMessage(sender, "crate-already-exists", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        Crate crate = plugin.getCrateManager().createCrate(crateId);
        if (crate != null) {
            MessageUtil.sendMessage(sender, "crate-created", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
        }
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.delete")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates delete <id>");
            return;
        }

        String crateId = args[1].toLowerCase();
        
        if (!plugin.getCrateManager().exists(crateId)) {
            MessageUtil.sendMessage(sender, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        if (sender instanceof Player) {
            pendingDeletes.add(((Player) sender).getUniqueId());
            MessageUtil.sendMessage(sender, "crate-delete-confirm", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (sender instanceof Player) {
                    pendingDeletes.remove(((Player) sender).getUniqueId());
                }
            }, 600L);
        } else {
            if (plugin.getCrateManager().deleteCrate(crateId)) {
                MessageUtil.sendMessage(sender, "crate-deleted", 
                    MessageUtil.createPlaceholders("%crate_name%", crateId));
            }
        }
    }

    private void handleConfirm(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "player-only");
            return;
        }

        Player player = (Player) sender;
        
        if (!pendingDeletes.contains(player.getUniqueId())) {
            MessageUtil.sendWithPrefix(sender, "&cNothing to confirm.");
            return;
        }

        pendingDeletes.remove(player.getUniqueId());
        MessageUtil.sendWithPrefix(sender, "&cCrate deletion requires you to use /oakcrates delete <id> again to delete.");
    }

    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("oakcrates.admin.list")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        Map<String, Crate> crates = plugin.getCrateManager().getAllCrates();
        
        if (crates.isEmpty()) {
            MessageUtil.sendWithPrefix(sender, "&7No crates configured.");
            return;
        }

        MessageUtil.sendWithPrefix(sender, "&aCrates (" + crates.size() + "):");
        for (Crate crate : crates.values()) {
            MessageUtil.send(sender, "&7- &e" + crate.getId() + " &7(" + ColorUtil.colorize(crate.getDisplayName()) + "&7)");
            MessageUtil.send(sender, "  &7Key Type: &f" + crate.getKeyType() + " &7| Blocks: &f" + crate.getBoundBlocks().size());
        }
    }

    private void handleEdit(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.edit")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "player-only");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates edit <id>");
            return;
        }

        String crateId = args[1].toLowerCase();
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        
        if (crate == null) {
            MessageUtil.sendMessage(sender, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        new CrateEditGUI(plugin, (Player) sender, crate).open();
    }

    private void handleSetBlock(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.setblock")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "player-only");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates setblock <id>");
            return;
        }

        String crateId = args[1].toLowerCase();
        
        if (!plugin.getCrateManager().exists(crateId)) {
            MessageUtil.sendMessage(sender, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        Player player = (Player) sender;
        
        for (RegisteredListener listener : HandlerList.getRegisteredListeners(plugin)) {
            if (listener.getListener() instanceof BlockInteractListener) {
                ((BlockInteractListener) listener.getListener()).startBindSession(player, crateId);
                break;
            }
        }
    }

    private void handleRemoveBlock(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.removeblock")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates removeblock <id>");
            return;
        }

        String crateId = args[1].toLowerCase();
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        
        if (crate == null) {
            MessageUtil.sendMessage(sender, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        if (crate.getBoundBlocks().isEmpty()) {
            MessageUtil.sendMessage(sender, "block-not-bound");
            return;
        }

        crate.getBoundBlocks().clear();
        plugin.getCrateManager().saveCrate(crate);
        plugin.getHologramManager().removeHologramsForCrate(crateId);
        
        MessageUtil.sendMessage(sender, "block-unbound", 
            MessageUtil.createPlaceholders("%crate_name%", crateId));
    }

    private void handleGiveKey(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.keys.give")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 4) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates givekey <player> <crate> <amount>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtil.sendMessage(sender, "player-not-found", 
                MessageUtil.createPlaceholders("%player%", args[1]));
            return;
        }

        String crateId = args[2].toLowerCase();
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        
        if (crate == null) {
            MessageUtil.sendMessage(sender, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(sender, "invalid-amount");
            return;
        }

        plugin.getKeyManager().giveVirtualKeys(target.getUniqueId(), crateId, amount);
        plugin.getClaimLogManager().logKeyGive(sender.getName(), target.getName(), crateId, amount);

        Map<String, String> placeholders = MessageUtil.createPlaceholders(
            "%player%", target.getName(),
            "%crate_name%", crateId,
            "%crate_display_name%", ColorUtil.colorize(crate.getDisplayName()),
            "%amount%", String.valueOf(amount)
        );
        
        MessageUtil.sendMessage(sender, "keys-given", placeholders);
        MessageUtil.sendMessage(target, "keys-received", placeholders);
    }

    private void handleTakeKey(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.keys.take")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 4) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates takekey <player> <crate> <amount>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtil.sendMessage(sender, "player-not-found", 
                MessageUtil.createPlaceholders("%player%", args[1]));
            return;
        }

        String crateId = args[2].toLowerCase();
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        
        if (crate == null) {
            MessageUtil.sendMessage(sender, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(sender, "invalid-amount");
            return;
        }

        int currentKeys = plugin.getKeyManager().getVirtualKeys(target.getUniqueId(), crateId);
        if (currentKeys < amount) {
            MessageUtil.sendMessage(sender, "not-enough-keys");
            return;
        }

        plugin.getKeyManager().takeVirtualKeys(target.getUniqueId(), crateId, amount);
        plugin.getClaimLogManager().logKeyTake(sender.getName(), target.getName(), crateId, amount);

        Map<String, String> placeholders = MessageUtil.createPlaceholders(
            "%player%", target.getName(),
            "%crate_name%", crateId,
            "%crate_display_name%", ColorUtil.colorize(crate.getDisplayName()),
            "%amount%", String.valueOf(amount)
        );
        
        MessageUtil.sendMessage(sender, "keys-taken", placeholders);
        MessageUtil.sendMessage(target, "keys-removed", placeholders);
    }

    private void handleSetKey(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.keys.set")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 4) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates setkey <player> <crate> <amount>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtil.sendMessage(sender, "player-not-found", 
                MessageUtil.createPlaceholders("%player%", args[1]));
            return;
        }

        String crateId = args[2].toLowerCase();
        Crate crate = plugin.getCrateManager().getCrate(crateId);
        
        if (crate == null) {
            MessageUtil.sendMessage(sender, "crate-not-found", 
                MessageUtil.createPlaceholders("%crate_name%", crateId));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(sender, "invalid-amount");
            return;
        }

        plugin.getKeyManager().setVirtualKeys(target.getUniqueId(), crateId, amount);
        plugin.getClaimLogManager().logKeySet(sender.getName(), target.getName(), crateId, amount);

        Map<String, String> placeholders = MessageUtil.createPlaceholders(
            "%player%", target.getName(),
            "%crate_name%", crateId,
            "%crate_display_name%", ColorUtil.colorize(crate.getDisplayName()),
            "%amount%", String.valueOf(amount)
        );
        
        MessageUtil.sendMessage(sender, "keys-set", placeholders);
    }

    private void handleViewKeys(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.keys.view")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates keys <player> [crate]");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtil.sendMessage(sender, "player-not-found", 
                MessageUtil.createPlaceholders("%player%", args[1]));
            return;
        }

        if (args.length >= 3) {
            String crateId = args[2].toLowerCase();
            Crate crate = plugin.getCrateManager().getCrate(crateId);
            
            if (crate == null) {
                MessageUtil.sendMessage(sender, "crate-not-found", 
                    MessageUtil.createPlaceholders("%crate_name%", crateId));
                return;
            }

            int keys = plugin.getKeyManager().getVirtualKeys(target.getUniqueId(), crateId);
            MessageUtil.sendWithPrefix(sender, "&e" + target.getName() + " &7has &a" + keys + " &7key(s) for &e" + crateId);
        } else {
            MessageUtil.sendWithPrefix(sender, "&eKeys for &6" + target.getName() + "&e:");
            for (Crate crate : plugin.getCrateManager().getAllCrates().values()) {
                int keys = plugin.getKeyManager().getVirtualKeys(target.getUniqueId(), crate.getId());
                MessageUtil.send(sender, "&7- " + crate.getId() + ": &a" + keys);
            }
        }
    }

    private void handleDiscord(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oakcrates.admin.discord")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates discord <setchannel> <channel_id>");
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "setchannel":
                if (args.length < 3) {
                    MessageUtil.sendWithPrefix(sender, "&cUsage: /oakcrates discord setchannel <channel_id>");
                    return;
                }
                
                if (plugin.getDiscordManager() == null) {
                    MessageUtil.sendMessage(sender, "discord-not-enabled");
                    return;
                }
                
                if (!plugin.getDiscordManager().isEnabled()) {
                    MessageUtil.sendWithPrefix(sender, "&c&l⚠ &cDiscord bot is not connected. Please check your bot token in config.yml and reload.");
                    return;
                }
                
                String channelId = args[2];
                
                if (!channelId.matches("\\d{17,20}")) {
                    MessageUtil.sendWithPrefix(sender, "&c&l✘ &cInvalid channel ID format. Please provide a valid Discord channel ID.");
                    return;
                }
                
                plugin.getDiscordManager().setChannelId(channelId);
                MessageUtil.sendMessage(sender, "discord-channel-set");
                MessageUtil.sendWithPrefix(sender, "&7Channel ID set to: &f" + channelId);
                break;
                
            case "status":
                boolean enabled = plugin.getDiscordManager() != null && plugin.getDiscordManager().isEnabled();
                if (enabled) {
                    MessageUtil.sendWithPrefix(sender, "&a&l✔ &aDiscord bot is connected.");
                    String currentChannel = plugin.getDiscordManager().getChannelId();
                    if (currentChannel != null && !currentChannel.isEmpty()) {
                        MessageUtil.sendWithPrefix(sender, "&7Channel ID: &f" + currentChannel);
                    } else {
                        MessageUtil.sendWithPrefix(sender, "&7Channel: &cNot set");
                    }
                } else {
                    MessageUtil.sendWithPrefix(sender, "&c&l✘ &cDiscord bot is not connected.");
                }
                break;
                
            default:
                MessageUtil.sendWithPrefix(sender, "&cUnknown action. Use: setchannel, status");
                break;
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("oakcrates.admin.reload")) {
            MessageUtil.sendMessage(sender, "no-permission");
            return;
        }

        if (plugin.reload()) {
            MessageUtil.sendMessage(sender, "reload-success");
        } else {
            MessageUtil.sendMessage(sender, "reload-fail");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList(
                "help", "create", "delete", "list", "edit", "setblock", "removeblock",
                "givekey", "takekey", "setkey", "keys", "discord", "reload"
            );
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();
            
            switch (subCommand) {
                case "delete":
                case "edit":
                case "setblock":
                case "removeblock":
                    completions = plugin.getCrateManager().getAllCrates().keySet().stream()
                            .filter(s -> s.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());
                    break;
                case "givekey":
                case "takekey":
                case "setkey":
                case "keys":
                    completions = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(s -> s.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());
                    break;
                case "discord":
                    completions = Arrays.asList("setchannel", "status").stream()
                            .filter(s -> s.startsWith(input))
                            .collect(Collectors.toList());
                    break;
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            String input = args[2].toLowerCase();
            
            if (subCommand.equals("givekey") || subCommand.equals("takekey") || 
                subCommand.equals("setkey") || subCommand.equals("keys")) {
                completions = plugin.getCrateManager().getAllCrates().keySet().stream()
                        .filter(s -> s.toLowerCase().startsWith(input))
                        .collect(Collectors.toList());
            }
        } else if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("givekey") || subCommand.equals("takekey") || subCommand.equals("setkey")) {
                completions = Arrays.asList("1", "5", "10", "25", "50", "100");
            }
        }

        return completions;
    }
}
