package dev.ashu16.oakcrates.utils;

import dev.ashu16.oakcrates.OakCrates;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {

    private final OakCrates plugin;

    public MessageUtil(OakCrates plugin) {
        this.plugin = plugin;
    }

    public static void send(CommandSender sender, String message) {
        if (sender != null && message != null && !message.isEmpty()) {
            sender.sendMessage(ColorUtil.colorize(message));
        }
    }

    public static void send(CommandSender sender, List<String> messages) {
        if (sender != null && messages != null) {
            for (String message : messages) {
                send(sender, message);
            }
        }
    }

    public static void sendWithPrefix(CommandSender sender, String message) {
        String prefix = OakCrates.getInstance().getConfigManager().getConfig().getString("prefix", "&8[&aOakCrates&8] ");
        send(sender, prefix + message);
    }

    public static String getMessage(String path) {
        return OakCrates.getInstance().getConfigManager().getMessages().getString(path, "&cMessage not found: " + path);
    }

    public static List<String> getMessageList(String path) {
        return OakCrates.getInstance().getConfigManager().getMessages().getStringList(path);
    }

    public static String getMessageWithPrefix(String path) {
        String prefix = OakCrates.getInstance().getConfigManager().getConfig().getString("prefix", "&8[&aOakCrates&8] ");
        return prefix + getMessage(path);
    }

    public static void sendMessage(CommandSender sender, String path) {
        send(sender, getMessageWithPrefix(path));
    }

    public static void sendMessage(CommandSender sender, String path, Map<String, String> placeholders) {
        String message = getMessageWithPrefix(path);
        message = replacePlaceholders(message, placeholders);
        send(sender, message);
    }

    public static void sendMessageList(CommandSender sender, String path) {
        send(sender, getMessageList(path));
    }

    public static void sendMessageList(CommandSender sender, String path, Map<String, String> placeholders) {
        List<String> messages = getMessageList(path);
        for (String message : messages) {
            send(sender, replacePlaceholders(message, placeholders));
        }
    }

    public static String replacePlaceholders(String text, Map<String, String> placeholders) {
        if (text == null) return null;
        if (placeholders == null) return text;
        
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }

    public static List<String> replacePlaceholders(List<String> lines, Map<String, String> placeholders) {
        if (lines == null) return null;
        
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, replacePlaceholders(lines.get(i), placeholders));
        }
        return lines;
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        
        String coloredTitle = ColorUtil.colorize(title != null ? title : "");
        String coloredSubtitle = ColorUtil.colorize(subtitle != null ? subtitle : "");
        
        player.sendTitle(coloredTitle, coloredSubtitle, fadeIn, stay, fadeOut);
    }

    public static Map<String, String> createPlaceholders(String... pairs) {
        Map<String, String> placeholders = new HashMap<>();
        for (int i = 0; i < pairs.length - 1; i += 2) {
            placeholders.put(pairs[i], pairs[i + 1]);
        }
        return placeholders;
    }
}
