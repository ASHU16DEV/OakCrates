# OakCrates - Minecraft Spigot Plugin

## Overview
OakCrates is an advanced Minecraft crate plugin for Spigot/Paper servers (1.16.5 - 1.21.10). It features selectable rewards (DonutSMP style), where players choose their reward instead of random drops. Full Discord integration sends reward claim notifications with IST timezone.

**Author:** ASHU16  
**Version:** 1.0  
**Database:** SQLite  
**Discord:** JDA 4.4.0_352 (Java 8 compatible)

## Recent Changes (December 2024)

### Discord Integration (NEW)
- **JDA 4.4.0_352**: Discord bot using Java 8 compatible JDA library
- **Reward Claim Embeds**: Automatic Discord notifications with player name, crate name, reward chosen, and IST date/time
- **/oakcrates discord setchannel**: Set Discord notification channel with validation
- **/oakcrates discord status**: Check Discord bot connection status
- **IST Timezone**: All Discord embeds display date/time in IST (Asia/Kolkata)
- **Config Options**: discord.enabled, discord.bot-token, discord.channel-id, discord.owner-id

### Animation System (FUNCTIONAL - 12 Types)
- **SPIN**: Circular particle rotation around crate
- **CASCADE**: Particles fall from above the crate
- **FIREWORKS**: Colored firework effects
- **LIGHTNING**: Electric particle effects
- **EXPLOSION**: Explosive particle burst
- **RAINBOW**: Multi-colored gradient effects
- **SPIRAL**: Rising spiral particles
- **PULSE**: Pulsing ring expansion
- **FLAME**: Fire particle effects
- **ENCHANT**: Enchanting particle effect
- **PORTAL**: Portal particle effects
- **HEARTS**: Heart particles for rewards
- **AnimationExecutor**: New class with callback support for reward claim flow

### Inventory Full Protection (NEW)
- **Inventory Check**: Before claiming reward, checks if player has enough inventory space
- **Smart Stack Detection**: Considers partial stacks that can absorb items (not just empty slots)
- **Key Protection**: If inventory is full, key is NOT consumed and error message is shown
- **Commands-Only Bypass**: Rewards with only commands skip the inventory check

### Optimizations
- **JAR Size Reduction**: 4644 → 2319 classes (49% minimization) with shaded dependencies
- **Final JAR Size**: ~11.7MB including JDA, SQLite, and HikariCP
- **Null Safety**: Animation executor and ConfirmGUI have null-safe location handling

### Previous Features
- **Per-Crate Sound Customization**: Configure open/close sounds for each crate via GUI
- **Pagination in CratePreviewGUI**: When items exceed available slots, next/previous page buttons appear
- **Add Items from Hand**: In RewardItemsEditorGUI, click to add items directly from your inventory

## Project Structure

```
OakCrates/
├── pom.xml                           # Maven build configuration
├── src/main/java/dev/ashu16/oakcrates/
│   ├── OakCrates.java                # Main plugin class
│   ├── commands/
│   │   ├── KeysCommand.java          # /keys and /key commands
│   │   └── OakCratesCommand.java     # Admin commands (/oakcrates)
│   ├── gui/
│   │   ├── GUIHolder.java            # Base GUI class with drag protection
│   │   ├── CratePreviewGUI.java      # Left-click preview with pagination
│   │   ├── RewardSelectionGUI.java   # Right-click reward selection
│   │   ├── ConfirmGUI.java           # Confirm claim GUI
│   │   └── editor/                   # Admin edit GUIs
│   │       ├── CrateEditGUI.java
│   │       ├── RewardsEditorGUI.java
│   │       ├── RewardEditGUI.java
│   │       ├── RewardItemsEditorGUI.java
│   │       ├── KeyEditorGUI.java
│   │       ├── SettingsEditorGUI.java
│   │       ├── HologramEditorGUI.java
│   │       ├── LoreEditorGUI.java
│   │       ├── CommandsEditorGUI.java
│   │       └── SoundsEditorGUI.java  # NEW: Per-crate sound customization
│   ├── hologram/
│   │   └── HologramManager.java      # Per-player hologram system
│   ├── listeners/
│   │   ├── BlockInteractListener.java
│   │   ├── GUIListener.java          # Drag/click protection
│   │   ├── ChatInputListener.java
│   │   └── PlayerJoinQuitListener.java
│   ├── managers/
│   │   ├── ConfigManager.java
│   │   ├── DatabaseManager.java      # SQLite with HikariCP
│   │   ├── CrateManager.java
│   │   ├── KeyManager.java
│   │   ├── ChatInputManager.java
│   │   └── ClaimLogManager.java
│   ├── models/
│   │   ├── Crate.java                # Includes animation & sound fields
│   │   ├── CrateAnimation.java       # NEW: 12 animation types
│   │   ├── Reward.java
│   │   ├── RewardItem.java
│   │   ├── PhysicalKey.java
│   │   ├── HologramSettings.java
│   │   └── KeyData.java
│   ├── tasks/
│   │   └── HologramUpdateTask.java
│   └── utils/
│       ├── ColorUtil.java
│       ├── ItemBuilder.java
│       ├── MessageUtil.java
│       ├── PlaceholderUtil.java
│       └── SoundUtil.java            # Includes playCrateSound method
└── src/main/resources/
    ├── plugin.yml                    # Plugin metadata
    ├── config.yml                    # Main configuration (5s hologram update)
    ├── messages.yml                  # All messages
    └── crates.yml                    # Crate definitions
```

## Key Features

### Player Features
- **Left-click** crate block: Preview GUI with pagination (no key required)
- **Right-click** crate block: Reward selection GUI (requires key)
- `/keys` or `/key`: View owned keys in chat
- Title/subtitle message when no keys available

### Key System
- Virtual keys stored in SQLite database
- Physical key items with custom NBT data
- Three modes: VIRTUAL, PHYSICAL, or BOTH

### Animation System
12 pre-defined animation types:
- NONE, CSGO_SPIN, FADE_IN, SPIRAL
- WAVE, INSTANT, SLOT_MACHINE, ROULETTE
- RAIN, EXPLOSION, BEAM, SHUFFLE

### Sound System
- Per-crate customizable open/close sounds
- Available sounds: BLOCK_ENDER_CHEST_OPEN, ENTITY_PLAYER_LEVELUP, etc.
- Set to NONE to disable sounds
- Filler blocks are silent (no click sounds)

### Hologram System
- Per-player rendered holograms above crate blocks
- Placeholders: %player%, %player_keys%, %crate_name%, %crate_display_name%
- Auto-updates every 5 seconds (configurable)
- Armor stand based (no external dependencies)

### Admin Features
- Full in-game GUI editors for all settings
- Hot reload: `/oakcrates reload`
- Block binding system for crate locations
- Claim logging to file and console

### GUI Protection
ALL GUIs are strictly click-only:
- Drag events blocked
- Shift-click disabled
- Item pickup/placement disabled
- Only defined button clicks work
- Filler blocks produce no sounds

## Commands

### Player Commands
- `/keys` - View your crate keys in chat
- `/key` - Same as /keys

### Admin Commands
- `/oakcrates help` - Show help
- `/oakcrates create <id>` - Create crate
- `/oakcrates delete <id>` - Delete crate
- `/oakcrates list` - List all crates
- `/oakcrates edit <id>` - Open edit GUI
- `/oakcrates setblock <id>` - Bind block to crate
- `/oakcrates removeblock <id>` - Unbind all blocks
- `/oakcrates givekey <player> <crate> <amount>` - Give keys
- `/oakcrates takekey <player> <crate> <amount>` - Take keys
- `/oakcrates setkey <player> <crate> <amount>` - Set keys
- `/oakcrates keys <player> [crate]` - View player keys
- `/oakcrates reload` - Reload configuration

## Permissions
- `oakcrates.use` - Use crates and /keys (default: true)
- `oakcrates.admin` - Full admin access (default: op)
- Individual permissions for each admin command

## Building

Run `mvn clean package` to build. The JAR file will be in `target/OakCrates-1.0.jar`.

**Final JAR Size:** ~7MB (includes SQLite and HikariCP dependencies)
**Original Plugin Code:** ~149KB

## Installation

1. Build the JAR file using Maven
2. Copy `OakCrates-1.0.jar` to your server's `plugins/` folder
3. Restart/reload the server
4. Configure crates in `plugins/OakCrates/crates.yml`

## User Preferences
- Code style: Java with Spigot API conventions
- Database: SQLite (single file, no external server)
- No external plugin dependencies
- All GUIs: Click-only, no drag operations
- Filler blocks: Silent (no sound on click)
