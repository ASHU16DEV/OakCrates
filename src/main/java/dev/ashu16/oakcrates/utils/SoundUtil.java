package dev.ashu16.oakcrates.utils;

import dev.ashu16.oakcrates.OakCrates;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playSound(Player player, String configPath) {
        if (player == null) return;
        
        String soundName = OakCrates.getInstance().getConfigManager().getConfig().getString("sounds." + configPath);
        if (soundName == null || soundName.isEmpty() || soundName.equalsIgnoreCase("NONE")) return;
        
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
        }
    }

    public static void playSound(Player player, Sound sound) {
        if (player == null || sound == null) return;
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        if (player == null || sound == null) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void playCrateSound(Player player, String soundName) {
        if (player == null || soundName == null || soundName.isEmpty() || soundName.equalsIgnoreCase("NONE")) return;
        
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
        }
    }

    public static void playOpenPreview(Player player) {
        playSound(player, "open-preview-gui");
    }

    public static void playOpenCrate(Player player) {
        playSound(player, "open-crate-gui");
    }

    public static void playSelectReward(Player player) {
        playSound(player, "select-reward");
    }

    public static void playNoKey(Player player) {
        playSound(player, "no-key");
    }

    public static void playConfirm(Player player) {
        playSound(player, "confirm-click");
    }

    public static void playCancel(Player player) {
        playSound(player, "cancel-click");
    }

    public static void playSuccess(Player player) {
        if (player == null) return;
        try {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
        } catch (Exception e) {
        }
    }

    public static void playError(Player player) {
        if (player == null) return;
        try {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);
        } catch (Exception e) {
        }
    }
}
