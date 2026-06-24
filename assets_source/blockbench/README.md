# Blockbench Conventions

This folder stores Blockbench source files and asset tracking docs for Dungeon Realms.

## Why this exists

- Keep all source `.bbmodel` files in version control.
- Keep export paths and IDs consistent.
- Prevent renaming churn and broken references in code.
- Allow temporary vanilla-style placeholder assets until final custom art is available.

## Core rules

1. Use stable asset IDs from day one.
- Examples:
  - `dungeonrealms:forgotten_king`
  - `dungeonrealms:soul_shard`
  - `dungeonrealms:dungeon_portal_frame`

2. Keep source files in repo, but do not ship them in jars.
- Store `.bbmodel` files under this folder.
- Build already excludes `**/*.bbmodel`.

3. Export by content type.
- Blocks/items:
  - Models -> `src/main/resources/assets/dungeonrealms/models/...`
  - Textures -> `src/main/resources/assets/dungeonrealms/textures/...`
- Entities/bosses:
  - Model class export (or chosen animation pipeline later)
  - Textures in `src/main/resources/assets/dungeonrealms/textures/entity/...`

4. Keep texture resolution policy consistent per tier.
- MVP placeholders: 16x or 32x.
- Final pass: same style/resolution rules across the same content tier.

5. Placeholder policy (current phase).
- Use generic/vanilla-style model and texture copies for speed.
- Prioritize gameplay readability over final art quality.
- Replace placeholders later without changing registry IDs.

6. Track every asset in a checklist.
- Required fields:
  - `id`
  - `type`
  - `blockbench_source`
  - `export_path`
  - `implemented`
  - `in_game_tested`

## Tracking sheet

Use [ASSET_TRACKING.md](/C:/Users/smkne/Videos/folder/DungeonRealms/assets_source/blockbench/ASSET_TRACKING.md) for active asset status.

## Master Blockbench Checklist

This is the source list of art assets we need to create in Blockbench.
Rule: always update this list when a new model/item/mob/boss is planned or added.

### Blocks

- [ ] `dungeonrealms:dungeon_portal_frame`
- [ ] `dungeonrealms:dungeon_portal_core_visual` (optional custom portal visual block model)
- [ ] `dungeonrealms:hub_campfire_upgrade`
- [ ] `dungeonrealms:hub_merchant_station`
- [ ] `dungeonrealms:hub_blacksmith_station`
- [ ] `dungeonrealms:hub_research_hall_core`
- [ ] `dungeonrealms:hub_training_grounds_marker`
- [ ] `dungeonrealms:hub_portal_chamber_core`
- [ ] `dungeonrealms:hub_master_forge`

### Items

- [ ] `dungeonrealms:soul_shard`
- [ ] `dungeonrealms:ancient_coin`
- [ ] `dungeonrealms:boss_token_forgotten_king`
- [ ] `dungeonrealms:boss_token_crystal_hydra`
- [ ] `dungeonrealms:boss_token_void_warden`
- [ ] `dungeonrealms:boss_token_ancient_titan`
- [ ] `dungeonrealms:boss_token_soul_reaper`
- [ ] `dungeonrealms:relic_common_icon_set`
- [ ] `dungeonrealms:relic_rare_icon_set`
- [ ] `dungeonrealms:relic_legendary_icon_set`
- [ ] `dungeonrealms:portable_crafting_table`
- [ ] `dungeonrealms:mining_lantern`
- [ ] `dungeonrealms:recall_stone`
- [ ] `dungeonrealms:portable_storage`
- [ ] `dungeonrealms:travel_tools`

### Mobs

- [ ] `dungeonrealms:goblin`
- [ ] `dungeonrealms:skeleton_warrior`
- [ ] `dungeonrealms:cultist`
- [ ] `dungeonrealms:slime_brute`
- [ ] `dungeonrealms:cave_stalker`
- [ ] `dungeonrealms:void_spawn`

### Bosses

- [ ] `dungeonrealms:forgotten_king`
- [ ] `dungeonrealms:crystal_hydra`
- [ ] `dungeonrealms:void_warden`
- [ ] `dungeonrealms:ancient_titan`
- [ ] `dungeonrealms:soul_reaper`
