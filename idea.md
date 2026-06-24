# Dungeon Realms - Project Roadmap

## Overview
Dungeon Realms is a NeoForge 1.21.1 Minecraft mod that adds a roguelike dungeon dimension separate from normal Minecraft progression.

The core loop is simple:

1. Players enter a dungeon run with no access to their overworld inventory.
2. They gain temporary relics and dungeon gear during the run.
3. They defeat rooms, elites, and bosses to earn progression currency.
4. They return to the overworld with their original inventory restored.

The goal is a highly replayable dungeon experience that works well inside large modpacks and does not get invalidated by endgame gear.

## Project Stack

| Area | Choice |
| --- | --- |
| Minecraft version | 1.21.1 |
| Mod loader | NeoForge 21.1+ |
| Java | JDK 21 |
| IDE | IntelliJ IDEA Community |
| Source control | Git + GitHub |

### Why NeoForge
- Better fit for modern Minecraft modding.
- Stronger long-term direction for a new project.
- Cleaner APIs for current development.
- More future-proof than starting on Forge for this codebase.

## Core Gameplay Loop

```text
Overworld
  -> Enter portal
  -> Save inventory
  -> Remove overworld gear
  -> Start dungeon run
  -> Clear random rooms
  -> Fight combat encounters
  -> Collect relics
  -> Defeat bosses
  -> Earn rewards
  -> End run
  -> Delete dungeon inventory
  -> Restore overworld inventory
  -> Spend progression currency
  -> Repeat
```

## Design Goals

### Goals
- Infinite replayability.
- Modpack-friendly progression.
- Separate dungeon progression track.
- Cooperative multiplayer support.
- No power creep from endgame modded gear.
- Long-term progression that feels meaningful.
- Overworld rewards that stay relevant but balanced.

### Non-goals
- Replacing Minecraft progression.
- Requiring grind just to access content.
- Overpowered rewards.
- Mandatory progression gates.

## Core Systems

### Player State System
The mod must save and restore all critical player state around dungeon runs.

#### Save
- Inventory.
- Armor.
- Offhand.
- XP.
- Hunger.
- Potion effects.
- Position.
- Dimension.

#### Restore on
- Run completion.
- Player death.
- Manual exit.

### Dungeon Run System
The run manager tracks the dungeon state from entry to exit.

#### Track
- Run ID.
- Seed.
- Floor number.
- Rooms cleared.
- Relics.
- Temporary inventory.
- Currency earned.
- Bosses defeated.

### Difficulty Scaling
Runs should scale as the player progresses through floors and content tiers.

### Dungeon Dimension
The dungeon should live in a dedicated dimension.

#### Possible implementation
- Void dimension.
- Generated room structures.

#### Benefits
- Easier generation.
- Predictable performance.
- Cleaner boss arenas.
- Easier balancing.

### Room System
Dungeon content is built from room categories.

#### Room categories
- Start room.
  - Safe spawn area.
- Combat room.
  - Standard enemies.
- Elite room.
  - Stronger enemies.
- Treasure room.
  - Loot rewards.
- Event room.
  - Random encounters.
  - Shrines.
  - Gambling events.
  - NPC encounters.
  - Relic trades.
- Shop room.
  - Currency spending.
- Boss room.
  - Major encounters.
- Secret room.
  - Rare rewards.

### Dungeon Generation

#### Version 1
Random room chain.

```text
Start
  -> Combat
  -> Combat
  -> Treasure
  -> Elite
  -> Boss
```

#### Version 2
Branching paths.

#### Version 3
Procedural floor layouts.

## Content Systems

### Enemy System

#### Base enemy types
- Goblin - fast melee.
- Skeleton Warrior - balanced melee.
- Cultist - ranged caster.
- Slime Brute - tank enemy.
- Cave Stalker - fast ambusher.
- Void Spawn - late-game enemy.

#### Elite variants
Modifiers can include:
- Enraged.
- Frozen.
- Toxic.
- Regenerating.
- Explosive.

### Boss System

#### Boss framework
Bosses should support:
- Multiple phases.
- Boss bars.
- Arena mechanics.
- Summons.
- Custom loot.

#### Planned bosses
- Forgotten King - sword attacks, summons guards.
- Crystal Hydra - multiple heads, crystal explosions.
- Void Warden - teleportation, arena hazards.
- Ancient Titan - slow heavy attacks, arena destruction.
- Soul Reaper - high mobility, life drain mechanics.

### Relic System
Relics are temporary power gained during runs and removed at run end.

#### Rarity tiers
- Common.
  - Minor bonuses.
  - Examples: attack speed, health, movement.
- Rare.
  - Build-defining effects.
  - Examples: freeze attacks, chain lightning, lifesteal.
- Legendary.
  - Major gameplay changes.
  - Examples: double projectiles, summon companions, massive cooldown reduction.

### Class System
Dungeon-only progression can unlock or enhance classes.

#### Planned classes
- Warrior - more health.
- Ranger - ranged bonuses.
- Mage - spell-focused.
- Rogue - critical strikes.
- Engineer - turrets and gadgets.

## Progression

### Currency System

#### Soul Shards
Main progression currency.

Used for:
- Permanent upgrades.
- Class unlocks.

#### Ancient Coins
Shop currency.

Used for:
- Cosmetics.
- Utility items.

#### Boss Tokens
Boss-specific progression currency.

Used for:
- Special unlocks.
- Rare rewards.

### Permanent Progression Tree
Examples of upgrades:
- Health I / II / III.
- Starter food.
- Starter iron sword.
- Increased relic drops.
- Bonus currency.
- Additional revive.
- Unlock Warrior.
- Unlock Mage.
- Unlock Rogue.
- Unlock Engineer.

### Overworld Hub System
The player upgrades a physical hub across progression.

#### Hub progression
```text
Ancient Ruins
  -> Campfire
  -> Merchant
  -> Blacksmith
  -> Research Hall
  -> Training Grounds
  -> Portal Chamber
  -> Master Forge
```

#### Hub functions
- Vendors.
- Progression access.
- Visual achievements.

### Overworld Rewards
These rewards must stay balanced.

#### Utility items
- Portable crafting table.
- Mining lantern.
- Recall stone.
- Portable storage.
- Travel tools.

#### Accessories
Small bonuses only.

Examples:
- +1 heart.
- +5% movement speed.
- Slight mining bonus.

#### Cosmetics
- Armor skins.
- Pets.
- Portal effects.
- Titles.
- Statues.
- Banners.

## Game Modes

### Endless Mode
Unlocked after completing the main progression path.

#### Structure
```text
Floor 1
  -> Floor 2
  -> Floor 3
  -> Infinite
```

#### Scaling
- Enemy health.
- Enemy damage.
- Reward quality.
- Relic frequency.

### Multiplayer Support
Planned as a future milestone.

#### Features
- Shared runs.
- Scaling difficulty.
- Shared rewards.
- Revive mechanics.
- Role-based classes.

## Modpack Compatibility
High priority.

Configurable values should include:
- Dungeon length.
- Enemy health.
- Enemy damage.
- Reward rates.
- Currency rates.
- Class unlock costs.
- Boss difficulty.
- Death penalties.
- Endless scaling.

## Data-Driven Expansion

Goal: allow other mods to integrate cleanly.

### Future APIs
- Register relics.
- Register rooms.
- Register bosses.
- Register enemies.
- Register classes.
- Register rewards.

## Development Milestones

### Milestone 1 - Project Setup
- NeoForge setup.
- Configs.
- Registries.
- Networking.

### Milestone 2 - Player State Save System
- Save inventory.
- Restore inventory.

### Milestone 3 - Dungeon Dimension
- Portal.
- Spawn room.

### Milestone 4 - Run Manager
- Start run.
- End run.
- Death handling.

### Milestone 5 - Room Generation
- Start room.
- Combat room.
- Treasure room.
- Boss room.

### Milestone 6 - Enemy Framework
- Base enemy.
- Scaling.
- Loot.

### Milestone 7 - First Boss
- Forgotten King.

### Milestone 8 - Relic System
- Common.
- Rare.
- Legendary.

### Milestone 9 - Progression Tree
- Soul Shards.
- Unlocks.

### Milestone 10 - Hub System
- Merchant.
- Blacksmith.
- Portal Chamber.

### Milestone 11 - Additional Content
- More rooms.
- More enemies.
- More bosses.

### Milestone 12 - Endless Mode
- Infinite floors.
- Scaling.

## Target Releases

### Version 0.1 Goal
- Portal.
- 5 random rooms.
- 3 enemy types.
- 1 boss.
- 5 relics.
- Soul Shards.
- Inventory restoration.
- Basic progression tree.

Playable alpha target.

### Version 1.0 Goal
- 15+ room types.
- 20+ enemies.
- 5+ bosses.
- 50+ relics.
- 4+ classes.
- Progression tree.
- Overworld hub.
- Utility rewards.
- Cosmetics.
- Multiplayer support.
- Endless mode.

Full modpack release target.

## Immediate Next Step
Create a NeoForge 1.21.1 project and implement:

1. Mod bootstrap.
2. Config system.
3. Player inventory save/restore capability.

Do not begin dungeon generation until inventory restoration works correctly.
