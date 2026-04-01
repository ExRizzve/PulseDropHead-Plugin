# PulseDropHead

A Minecraft plugin for Pulse 1.21.11 that adds player head drops on death with advanced features.
A Minecraft plugin for Paper/Purpur/Pulse 1.21.11+ that adds player head drops on death with advanced features.

## Features

- **Configurable Drop Modes**: PvP only, PvE only, or all deaths
- **Drop Chance**: Adjustable probability (0-100%)
- **Glowing Effect**: Optional glowing heads with customizable color and duration
- **Auto-Despawn**: Automatic cleanup after configurable time
- **Multi-Language**: Russian and English support
- **Cross-Platform**: Works on Paper, Purpur, and Pulse

## Commands

- `/pulsedrophead givehead <player> <head_owner>` - Give a player head to someone
- `/pulsedrophead lang <ru|eng>` - Change language
- `/pulsedrophead reload` - Reload configuration

## Permissions

All commands require OP status.

## Configuration

See `config.yml` for detailed configuration options including:
- Drop modes (pvp/pve/all)
- Drop chance percentage
- Glowing effects
- Auto-despawn settings

## Building

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`

## Requirements

- Paper/Purpur/Pulse 1.21.11+
- Java 21+

## License

All rights reserved.
