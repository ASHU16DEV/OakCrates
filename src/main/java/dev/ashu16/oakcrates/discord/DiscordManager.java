package dev.ashu16.oakcrates.discord;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.Crate;
import dev.ashu16.oakcrates.models.Reward;
import dev.ashu16.oakcrates.utils.ColorUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

public class DiscordManager {

    private final OakCrates plugin;
    private JDA jda;
    private boolean enabled;
    private String ownerId;
    private String channelId;
    
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter IST_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss a z");

    public DiscordManager(OakCrates plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }

    public void initialize() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        
        if (!config.getBoolean("discord.enabled", false)) {
            plugin.logInfo("Discord integration is disabled.");
            return;
        }

        String token = config.getString("discord.token", "");
        if (token.isEmpty() || token.equals("YOUR_BOT_TOKEN_HERE")) {
            plugin.logWarning("Discord is enabled but no valid token provided.");
            return;
        }

        this.ownerId = config.getString("discord.owner-id", "");
        this.channelId = config.getString("discord.channel-id", "");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                jda = JDABuilder.createLight(token, EnumSet.noneOf(GatewayIntent.class))
                        .setActivity(Activity.playing("OakCrates"))
                        .build();
                
                jda.awaitReady();
                enabled = true;
                plugin.logInfo("Discord bot connected successfully!");
                
            } catch (Exception e) {
                plugin.logError("Failed to connect Discord bot!", e);
                enabled = false;
            }
        });
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled && jda != null;
    }

    public boolean isOwner(String discordUserId) {
        return ownerId != null && ownerId.equals(discordUserId);
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
        FileConfiguration config = plugin.getConfigManager().getConfig();
        config.set("discord.channel-id", channelId);
        plugin.getConfigManager().saveConfig();
    }

    private String getISTDateTime() {
        ZonedDateTime istDateTime = ZonedDateTime.now(IST_ZONE);
        return istDateTime.format(IST_FORMATTER);
    }

    public void sendRewardClaimNotification(Player player, Crate crate, Reward reward) {
        if (!isEnabled() || channelId == null || channelId.isEmpty()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                TextChannel channel = jda.getTextChannelById(channelId);
                if (channel == null) {
                    plugin.logWarning("Discord channel not found: " + channelId);
                    return;
                }

                FileConfiguration config = plugin.getConfigManager().getConfig();
                String title = config.getString("discord.embed.title", "Crate Reward Claimed!");
                String colorHex = config.getString("discord.embed.color", "#00FF00");
                String footer = config.getString("discord.embed.footer", "OakCrates by ASHU16");

                Color embedColor;
                try {
                    embedColor = Color.decode(colorHex);
                } catch (NumberFormatException e) {
                    embedColor = Color.GREEN;
                }

                String cleanCrateName = ColorUtil.stripColor(crate.getDisplayName());
                String cleanRewardName = ColorUtil.stripColor(reward.getDisplayName());
                String istDateTime = getISTDateTime();

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(title)
                        .setColor(embedColor)
                        .addField("Player", player.getName(), true)
                        .addField("Crate", cleanCrateName, true)
                        .addField("Reward", cleanRewardName, true)
                        .addField("Date & Time (IST)", istDateTime, false)
                        .setFooter(footer)
                        .setTimestamp(java.time.Instant.now());

                channel.sendMessage(embed.build()).queue(
                    success -> {},
                    error -> plugin.logWarning("Failed to send Discord notification: " + error.getMessage())
                );

            } catch (Exception e) {
                plugin.logError("Error sending Discord notification!", e);
            }
        });
    }

    public void reload() {
        shutdown();
        initialize();
    }

    public JDA getJda() {
        return jda;
    }
}
