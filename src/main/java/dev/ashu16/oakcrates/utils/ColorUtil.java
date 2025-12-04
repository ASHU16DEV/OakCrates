package dev.ashu16.oakcrates.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append("§").append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static List<String> colorize(List<String> lines) {
        if (lines == null) {
            return null;
        }
        return lines.stream()
                .map(ColorUtil::colorize)
                .collect(Collectors.toList());
    }

    public static String stripColor(String text) {
        if (text == null) {
            return null;
        }
        return ChatColor.stripColor(colorize(text));
    }

    public static String decolorize(String text) {
        if (text == null) {
            return null;
        }
        return text.replace('§', '&');
    }
}
