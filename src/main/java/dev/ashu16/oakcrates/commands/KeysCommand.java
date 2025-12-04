package dev.ashu16.oakcrates.commands;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.utils.ColorUtil;
import dev.ashu16.oakcrates.utils.MessageUtil;
import dev.ashu16.oakcrates.utils.PlaceholderUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class KeysCommand implements CommandExecutor {

    private final OakCrates plugin;

    public KeysCommand(OakCrates plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "player-only");
            return true;
        }

        Player player = (Player) sender;
        
        if (!player.hasPermission("oakcrates.use")) {
            MessageUtil.sendMessage(player, "no-permission");
            return true;
        }

        displayKeys(player);
        return true;
    }

    private void displayKeys(Player player) {
        List<String> header = MessageUtil.getMessageList("keys-header");
        MessageUtil.send(player, ColorUtil.colorize(header));
        
        Map<String, Crate> crates = plugin.getCrateManager().getAllCrates();
        boolean showZeroKeys = plugin.getConfigManager().getConfig().getBoolean("show-zero-keys", true);
        boolean hasAnyKeys = false;

        for (Crate crate : crates.values()) {
            int keys = plugin.getKeyManager().getTotalKeys(player, crate.getId());
            
            if (keys == 0 && !showZeroKeys) {
                continue;
            }
            
            hasAnyKeys = true;
            
            String format = MessageUtil.getMessage("keys-format");
            format = PlaceholderUtil.parse(format, player, crate);
            format = PlaceholderUtil.parseAmount(format, keys);
            
            MessageUtil.send(player, format);
        }

        if (!hasAnyKeys && !showZeroKeys) {
            MessageUtil.send(player, ColorUtil.colorize(MessageUtil.getMessage("keys-empty")));
        }

        List<String> footer = MessageUtil.getMessageList("keys-footer");
        MessageUtil.send(player, ColorUtil.colorize(footer));
    }
}
