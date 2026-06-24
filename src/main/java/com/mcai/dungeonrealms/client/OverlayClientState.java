package com.mcai.dungeonrealms.client;

// Client-side cache for overlay text values synced from server.
public final class OverlayClientState {
    private static String dimensionName = "unknown";
    private static int soulShards = 0;
    private static boolean runActive = false;

    private OverlayClientState() {
    }

    public static void update(String dimension, int shards, boolean activeRun) {
        dimensionName = dimension;
        soulShards = shards;
        runActive = activeRun;
    }

    public static String dimensionName() {
        return dimensionName;
    }

    public static int soulShards() {
        return soulShards;
    }

    public static boolean runActive() {
        return runActive;
    }
}
