package dev.ashu16.oakcrates.animations;

import dev.ashu16.oakcrates.OakCrates;
import dev.ashu16.oakcrates.models.CrateAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class AnimationExecutor {

    private final OakCrates plugin;
    private final Random random = new Random();

    public AnimationExecutor(OakCrates plugin) {
        this.plugin = plugin;
    }

    public void playAnimation(Player player, CrateAnimation animation, Location location, Runnable onComplete) {
        if (animation == null || animation == CrateAnimation.NONE || location == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        Location animLoc = location.clone().add(0.5, 1, 0.5);

        switch (animation) {
            case SPIN:
                playSpin(player, animLoc, onComplete);
                break;
            case CASCADE:
                playCascade(player, animLoc, onComplete);
                break;
            case FIREWORKS:
                playFireworks(player, animLoc, onComplete);
                break;
            case LIGHTNING:
                playLightning(player, animLoc, onComplete);
                break;
            case EXPLOSION:
                playExplosion(player, animLoc, onComplete);
                break;
            case RAINBOW:
                playRainbow(player, animLoc, onComplete);
                break;
            case SPIRAL:
                playSpiral(player, animLoc, onComplete);
                break;
            case PULSE:
                playPulse(player, animLoc, onComplete);
                break;
            case FLAME:
                playFlame(player, animLoc, onComplete);
                break;
            case ENCHANT:
                playEnchant(player, animLoc, onComplete);
                break;
            case PORTAL:
                playPortal(player, animLoc, onComplete);
                break;
            case HEARTS:
                playHearts(player, animLoc, onComplete);
                break;
            default:
                if (onComplete != null) {
                    onComplete.run();
                }
                break;
        }
    }

    private void playSpin(Player player, Location loc, Runnable onComplete) {
        int duration = plugin.getConfigManager().getConfig().getInt("animations.spin-duration", 40);
        
        new BukkitRunnable() {
            int tick = 0;
            double angle = 0;

            @Override
            public void run() {
                if (tick >= duration) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 4; i++) {
                    double radius = 1.0;
                    double x = radius * Math.cos(angle + (i * Math.PI / 2));
                    double z = radius * Math.sin(angle + (i * Math.PI / 2));
                    Location particleLoc = loc.clone().add(x, 0.5, z);
                    player.spawnParticle(Particle.CRIT_MAGIC, particleLoc, 1, 0, 0, 0, 0);
                }

                if (tick % 5 == 0) {
                    player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.0f + (tick / (float) duration));
                }

                angle += 0.3;
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playCascade(Player player, Location loc, Runnable onComplete) {
        int duration = plugin.getConfigManager().getConfig().getInt("animations.cascade-duration", 30);
        
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= duration) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 5; i++) {
                    double x = (random.nextDouble() - 0.5) * 2;
                    double z = (random.nextDouble() - 0.5) * 2;
                    Location particleLoc = loc.clone().add(x, 2 - (tick * 0.05), z);
                    player.spawnParticle(Particle.END_ROD, particleLoc, 1, 0, -0.1, 0, 0.01);
                }

                if (tick % 4 == 0) {
                    player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 0.8f + (random.nextFloat() * 0.4f));
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playFireworks(Player player, Location loc, Runnable onComplete) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        
        FireworkEffect effect = FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .withFade(Color.WHITE)
                .withTrail()
                .withFlicker()
                .build();
        
        meta.addEffect(effect);
        meta.setPower(0);
        firework.setFireworkMeta(meta);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            firework.detonate();
            if (onComplete != null) {
                onComplete.run();
            }
        }, 5L);
    }

    private void playLightning(Player player, Location loc, Runnable onComplete) {
        loc.getWorld().strikeLightningEffect(loc);
        player.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.0f);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (int i = 0; i < 20; i++) {
                double x = (random.nextDouble() - 0.5) * 2;
                double y = random.nextDouble() * 2;
                double z = (random.nextDouble() - 0.5) * 2;
                player.spawnParticle(Particle.FLASH, loc.clone().add(x, y, z), 1);
            }
            
            if (onComplete != null) {
                onComplete.run();
            }
        }, 10L);
    }

    private void playExplosion(Player player, Location loc, Runnable onComplete) {
        player.spawnParticle(Particle.EXPLOSION_LARGE, loc, 3, 0.5, 0.5, 0.5, 0);
        player.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.2f);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 10) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 10; i++) {
                    double x = (random.nextDouble() - 0.5) * 3;
                    double y = random.nextDouble() * 2;
                    double z = (random.nextDouble() - 0.5) * 3;
                    player.spawnParticle(Particle.FLAME, loc.clone().add(x, y, z), 1, 0, 0, 0, 0.02);
                }
                
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void playRainbow(Player player, Location loc, Runnable onComplete) {
        Particle.DustOptions[] colors = {
            new Particle.DustOptions(org.bukkit.Color.RED, 1),
            new Particle.DustOptions(org.bukkit.Color.ORANGE, 1),
            new Particle.DustOptions(org.bukkit.Color.YELLOW, 1),
            new Particle.DustOptions(org.bukkit.Color.LIME, 1),
            new Particle.DustOptions(org.bukkit.Color.AQUA, 1),
            new Particle.DustOptions(org.bukkit.Color.BLUE, 1),
            new Particle.DustOptions(org.bukkit.Color.PURPLE, 1)
        };

        new BukkitRunnable() {
            int tick = 0;
            int colorIndex = 0;

            @Override
            public void run() {
                if (tick >= 40) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 8; i++) {
                    double angle = (tick * 0.2) + (i * Math.PI / 4);
                    double radius = 1.5;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    double y = 0.5 + (Math.sin(tick * 0.1) * 0.3);
                    
                    player.spawnParticle(Particle.REDSTONE, loc.clone().add(x, y, z), 1, colors[colorIndex % colors.length]);
                }

                if (tick % 3 == 0) colorIndex++;
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playSpiral(Player player, Location loc, Runnable onComplete) {
        int duration = plugin.getConfigManager().getConfig().getInt("animations.spiral-duration", 40);
        
        new BukkitRunnable() {
            int tick = 0;
            double height = 0;
            double angle = 0;

            @Override
            public void run() {
                if (tick >= duration) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 2; i++) {
                    double radius = 1.0 - (height / 3);
                    double x = radius * Math.cos(angle + (i * Math.PI));
                    double z = radius * Math.sin(angle + (i * Math.PI));
                    
                    player.spawnParticle(Particle.SPELL_WITCH, loc.clone().add(x, height, z), 1, 0, 0, 0, 0);
                }

                angle += 0.4;
                height += 0.05;
                if (height > 2.5) height = 0;
                
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playPulse(Player player, Location loc, Runnable onComplete) {
        new BukkitRunnable() {
            int tick = 0;
            double radius = 0.1;
            boolean expanding = true;

            @Override
            public void run() {
                if (tick >= 40) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 20; i++) {
                    double angle = (i / 20.0) * Math.PI * 2;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    player.spawnParticle(Particle.VILLAGER_HAPPY, loc.clone().add(x, 0.5, z), 1, 0, 0, 0, 0);
                }

                if (expanding) {
                    radius += 0.15;
                    if (radius >= 2.0) expanding = false;
                } else {
                    radius -= 0.15;
                    if (radius <= 0.1) expanding = true;
                }

                if (tick % 10 == 0) {
                    player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1.0f);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playFlame(Player player, Location loc, Runnable onComplete) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 30) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 15; i++) {
                    double x = (random.nextDouble() - 0.5) * 1.5;
                    double z = (random.nextDouble() - 0.5) * 1.5;
                    player.spawnParticle(Particle.FLAME, loc.clone().add(x, 0, z), 1, 0, 0.1, 0, 0.05);
                    player.spawnParticle(Particle.SMOKE_NORMAL, loc.clone().add(x, 0.5, z), 1, 0, 0.05, 0, 0.02);
                }

                if (tick % 5 == 0) {
                    player.playSound(loc, Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1.0f);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playEnchant(Player player, Location loc, Runnable onComplete) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 40) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 10; i++) {
                    double x = (random.nextDouble() - 0.5) * 3;
                    double y = random.nextDouble() * 2;
                    double z = (random.nextDouble() - 0.5) * 3;
                    player.spawnParticle(Particle.ENCHANTMENT_TABLE, loc.clone().add(x, y + 1, z), 1, 0, -0.1, 0, 0.5);
                }

                if (tick % 8 == 0) {
                    player.playSound(loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.5f, 1.0f + (random.nextFloat() * 0.3f));
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playPortal(Player player, Location loc, Runnable onComplete) {
        new BukkitRunnable() {
            int tick = 0;
            double angle = 0;

            @Override
            public void run() {
                if (tick >= 40) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 15; i++) {
                    double radius = 1.2;
                    double a = angle + (i * Math.PI / 7.5);
                    double x = radius * Math.cos(a);
                    double z = radius * Math.sin(a);
                    double y = 0.3 + (Math.sin(a * 2) * 0.3);
                    
                    player.spawnParticle(Particle.PORTAL, loc.clone().add(x, y, z), 3, 0, 0, 0, 0.1);
                }

                if (tick % 3 == 0) {
                    player.playSound(loc, Sound.BLOCK_PORTAL_AMBIENT, 0.3f, 1.5f);
                }

                angle += 0.15;
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playHearts(Player player, Location loc, Runnable onComplete) {
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= 30) {
                    this.cancel();
                    if (onComplete != null) {
                        Bukkit.getScheduler().runTask(plugin, onComplete);
                    }
                    return;
                }

                for (int i = 0; i < 5; i++) {
                    double x = (random.nextDouble() - 0.5) * 2;
                    double y = random.nextDouble() * 1.5;
                    double z = (random.nextDouble() - 0.5) * 2;
                    player.spawnParticle(Particle.HEART, loc.clone().add(x, y + 0.5, z), 1, 0, 0.1, 0, 0);
                }

                if (tick % 6 == 0) {
                    player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
