# Dungeon Realms

Dungeon Realms is a NeoForge 1.21.1 Minecraft mod built around a roguelike dungeon dimension with separate run-based progression.

Players enter a dungeon run without their overworld gear, clear rooms, collect temporary relics, defeat bosses, and return to the overworld with their original inventory restored. The project is designed for replayability, long-term progression, and compatibility with larger modpacks.

## Current focus

- Player inventory and state save/restore.
- Dungeon run tracking.
- Configurable progression and balance.
- Room, enemy, boss, and relic systems.

## Planned systems

- Dedicated dungeon dimension.
- Random and branching room generation.
- Temporary relics and dungeon-only gear.
- Boss encounters with multi-phase mechanics.
- Permanent progression through Soul Shards.
- Physical overworld hub upgrades.
- Endless mode and multiplayer support.

## Development status

This repository is in early development. The immediate priority is the technical foundation required for reliable dungeon runs:

1. Mod bootstrap.
2. Config system.
3. Player inventory save/restore.

Dungeon generation should not move forward until inventory restoration is stable.

## Project stack

- Minecraft 1.21.1
- NeoForge 21.1+
- Java 21
- Git + GitHub

## Building locally

Use the Gradle wrapper from the project root:

```bash
./gradlew build
```

On Windows:

```powershell
.\gradlew.bat build
```

## Roadmap

The full project roadmap is documented in [idea.md](idea.md).

## Coding rule

When adding or changing systems, include concise comments on the main function and structural sections so file flow and intent are clear during maintenance.
