package dev.ashu16.oakcrates.models;

import java.util.ArrayList;
import java.util.List;

public class HologramSettings {

    private boolean enabled;
    private List<String> lines;
    private double height;
    private int updateInterval;

    public HologramSettings() {
        this.enabled = true;
        this.lines = new ArrayList<>();
        this.lines.add("&e&lCrate");
        this.lines.add("&7Your Keys: &a%player_keys%");
        this.height = 2.5;
        this.updateInterval = 1200;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines != null ? lines : new ArrayList<>();
    }

    public void addLine(String line) {
        if (lines == null) {
            lines = new ArrayList<>();
        }
        lines.add(line);
    }

    public void removeLine(int index) {
        if (lines != null && index >= 0 && index < lines.size()) {
            lines.remove(index);
        }
    }

    public void setLine(int index, String line) {
        if (lines != null && index >= 0 && index < lines.size()) {
            lines.set(index, line);
        }
    }

    public void moveLineUp(int index) {
        if (lines != null && index > 0 && index < lines.size()) {
            String line = lines.remove(index);
            lines.add(index - 1, line);
        }
    }

    public void moveLineDown(int index) {
        if (lines != null && index >= 0 && index < lines.size() - 1) {
            String line = lines.remove(index);
            lines.add(index + 1, line);
        }
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = Math.max(0.5, Math.min(10.0, height));
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = Math.max(20, updateInterval);
    }

    public HologramSettings clone() {
        HologramSettings clone = new HologramSettings();
        clone.enabled = this.enabled;
        clone.lines = new ArrayList<>(this.lines);
        clone.height = this.height;
        clone.updateInterval = this.updateInterval;
        return clone;
    }
}
