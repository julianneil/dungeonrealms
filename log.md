# Dungeon Realms - Progress Log

Last updated: 2026-06-24

## Completed

- [x] Initialized and pushed repository to GitHub (`main` branch).
- [x] Rewrote `README.md` into project-facing documentation.
- [x] Reformatted `idea.md` roadmap and removed AI-assistance mentions.
- [x] Added structured comments across core code files for readability.
- [x] Implemented run core:
  - [x] Player snapshot capture/restore (inventory, armor, offhand, effects, XP, hunger, health, air, fire, position, dimension).
  - [x] Run lifecycle manager (`startRun`, `endRun`, status, safety restore on death/logout).
  - [x] Debug commands: `/dr run start|end|status`.
- [x] Added custom portal foundation:
  - [x] `dungeon_portal_frame` block (cobblestone placeholder).
  - [x] `dungeon_portal` block and flint-and-steel activation logic.
- [x] Built first vertical slice:
  - [x] Portal touch starts run.
  - [x] Player teleports to test dungeon arena.
  - [x] Return portal exits run and restores overworld state.
- [x] Moved test arena into custom dimension (`dungeonrealms:dungeon_test`).
- [x] Added fail-safe rollback if arena teleport fails.
- [x] Added movable HUD overlay config:
  - [x] `overlayEnabled`, `overlayX`, `overlayY`.
  - [x] Overlay data sync server -> client.
  - [x] Overlay only renders in dungeon dimension.
- [x] Added Soul Shard physical item and working texture/model wiring.
- [x] Added Soul Pouch (bundle-style) that only accepts Soul Shards.
- [x] Run reward now grants physical Soul Shard items.
- [x] Removed shard visuals from overlay and reward chat line.
- [x] Added Blockbench conventions and tracking docs:
  - [x] `assets_source/blockbench/README.md`
  - [x] `assets_source/blockbench/ASSET_TRACKING.md`

## In Place (Working Rules)

- [x] Use placeholder vanilla-style models/textures for now.
- [x] Keep stable registry IDs and replace art later without renaming IDs.
- [x] Keep `.bbmodel` sources under `assets_source/blockbench/`.

## Next Priorities

- [ ] Replace temporary test arena with actual dungeon floor generation flow.
- [ ] Add real run objective/completion condition (not just portal in/out).
- [ ] Add proper return logic tied to dungeon completion/failure states.
- [ ] Add Soul Pouch custom texture/model (current model uses vanilla bundle texture).
- [ ] Implement first enemy in dungeon dimension (Goblin) and encounter trigger.
- [ ] Add one basic reward node/spend flow for Soul Shards.
- [ ] Add startup validation command/log for dimension registration state.

## Known Technical Debt

- [ ] Event bus subscriber `bus` usage has deprecation warnings on this NeoForge version.
- [ ] Template config fields/logs still exist (`magicNumber`, etc.) and should be cleaned up.
- [ ] `org/example/Main.java` template class still exists and is not part of mod runtime.

## Quick Resume Checklist

- [ ] `./gradlew build` (or `.\gradlew.bat build`) passes.
- [ ] Overworld portal ignition works.
- [ ] Entering portal starts run and teleports to dungeon test dimension.
- [ ] Exiting via return portal restores full player state.
- [ ] Soul Shard item reward granted on successful run exit.
- [ ] Soul Pouch accepts only Soul Shard items.
