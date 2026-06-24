package com.mcai.dungeonrealms.progression;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

// Stores simple permanent progression currency on the player.
public final class SoulShardProgression {
    private static final String ROOT_KEY = "dungeonrealms_progression";
    private static final String SOUL_SHARDS_KEY = "soul_shards";

    private SoulShardProgression() {
    }

    public static int getSoulShards(ServerPlayer player) {
        CompoundTag root = getOrCreateRoot(player);
        return root.getInt(SOUL_SHARDS_KEY);
    }

    public static int addSoulShards(ServerPlayer player, int amount) {
        CompoundTag root = getOrCreateRoot(player);
        int next = Math.max(0, root.getInt(SOUL_SHARDS_KEY) + amount);
        root.putInt(SOUL_SHARDS_KEY, next);
        return next;
    }

    private static CompoundTag getOrCreateRoot(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(ROOT_KEY, CompoundTag.TAG_COMPOUND)) {
            persistent.put(ROOT_KEY, new CompoundTag());
        }
        return persistent.getCompound(ROOT_KEY);
    }
}
