package dev.ashu16.oakcrates.tasks;

import dev.ashu16.oakcrates.OakCrates;
import org.bukkit.scheduler.BukkitRunnable;

public class HologramUpdateTask extends BukkitRunnable {

    private final OakCrates plugin;

    public HologramUpdateTask(OakCrates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getHologramManager().updateAllHolograms();
    }
}
