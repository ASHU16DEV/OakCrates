package dev.ashu16.oakcrates.utils;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlaceholderUtil {

    private static final Pattern PLAYER_KEYS_PATTERN = Pattern.compile("%player_keys_([a-zA-Z0-9_]+)%");

    public static String parse(String text, Player player, Crate crate) {
        if (text == null) return null;
        
        if (player != null) {
            text = text.replace("%player%", player.getName());
            text = text.replace("%player_name%", player.getName());
        }
        
        if (crate != null) {
            text = text.replace("%crate_name%", crate.getId());
            text = text.replace("%crate_id%", crate.getId());
            text = text.replace("%crate_display_name%", crate.getDisplayName());
            
            if (player != null) {
                int keys = OakCrates.getInstance().getKeyManager().getVirtualKeys(player.getUniqueId(), crate.getId());
                text = text.replace("%player_keys%", String.valueOf(keys));
            }
        }
        
        if (player != null) {
            Matcher matcher = PLAYER_KEYS_PATTERN.matcher(text);
            StringBuffer buffer = new StringBuffer();
            
            while (matcher.find()) {
                String crateId = matcher.group(1);
                int keys = OakCrates.getInstance().getKeyManager().getVirtualKeys(player.getUniqueId(), crateId);
                matcher.appendReplacement(buffer, String.valueOf(keys));
            }
            matcher.appendTail(buffer);
            text = buffer.toString();
        }
        
        return text;
    }

    public static List<String> parse(List<String> lines, Player player, Crate crate) {
        if (lines == null) return null;
        
        return lines.stream()
                .map(line -> parse(line, player, crate))
                .collect(Collectors.toList());
    }

    public static String parseReward(String text, Player player, Crate crate, String rewardName) {
        text = parse(text, player, crate);
        
        if (rewardName != null) {
            text = text.replace("%reward_name%", rewardName);
        }
        
        return text;
    }

    public static List<String> parseReward(List<String> lines, Player player, Crate crate, String rewardName) {
        if (lines == null) return null;
        
        return lines.stream()
                .map(line -> parseReward(line, player, crate, rewardName))
                .collect(Collectors.toList());
    }

    public static String parseAmount(String text, int amount) {
        if (text == null) return null;
        return text.replace("%amount%", String.valueOf(amount));
    }
}
