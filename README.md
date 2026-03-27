# PulseDropHead

A Minecraft plugin for Paper/Purpur/Pulse 1.21.11+ that adds player head drops on death with advanced features.

## Features

- **Configurable Drop Modes**: PvP only, PvE only, or all deaths
- **Drop Chance**: Adjustable probability (0-100%)
- **Glowing Effect**: Optional glowing heads with customizable color and duration
- **Auto-Despawn**: Automatic cleanup after configurable time
- **Multi-Language**: Russian and English support
- **Cross-Platform**: Works on Paper, Purpur, and Pulse
- **Pulse Optimization**: Automatic packet batching when running on Pulse

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

## Pulse Integration

When running on Pulse, the plugin automatically enables:
- Network packet batching optimization
- Reduced system call overhead
- Better performance for mass head distribution

## License

All rights reserved.
