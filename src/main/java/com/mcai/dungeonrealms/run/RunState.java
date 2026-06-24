package com.mcai.dungeonrealms.run;

import java.util.UUID;

// Immutable runtime state for a single active dungeon run.
public record RunState(UUID runId, long seed, int floorsCleared, int roomsCleared, PlayerStateSnapshot playerSnapshot) {
}
