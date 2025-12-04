package dev.ashu16.oakcrates.models;

public enum CrateAnimation {
    NONE("None", "No animation"),
    SPIN("Spin", "Items spin around before selection"),
    CASCADE("Cascade", "Items cascade down like a waterfall"),
    FIREWORKS("Fireworks", "Firework particles on selection"),
    LIGHTNING("Lightning", "Lightning strike effect"),
    EXPLOSION("Explosion", "Explosion particles on open"),
    RAINBOW("Rainbow", "Rainbow particle trail"),
    SPIRAL("Spiral", "Spiral particle animation"),
    PULSE("Pulse", "Pulsing glow effect"),
    FLAME("Flame", "Flame particle burst"),
    ENCHANT("Enchant", "Enchanting table particles"),
    PORTAL("Portal", "Portal particle swirl"),
    HEARTS("Hearts", "Heart particles shower");

    private final String displayName;
    private final String description;

    CrateAnimation(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public CrateAnimation next() {
        CrateAnimation[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    public CrateAnimation previous() {
        CrateAnimation[] values = values();
        return values[(this.ordinal() - 1 + values.length) % values.length];
    }
}
