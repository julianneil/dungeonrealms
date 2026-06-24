package com.mcai.dungeonrealms.run;

import com.mcai.dungeonrealms.dungeonrealms;
import com.mcai.dungeonrealms.portal.DungeonPortalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

// Temporary dungeon destination used to validate run flow end-to-end.
public final class DungeonTestArenaManager {
    public static final ResourceKey<Level> DUNGEON_TEST_LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(dungeonrealms.MODID, "dungeon_test"));
    private static final BlockPos ARENA_SPAWN = new BlockPos(0, 120, 0);
    private static final int HALF_SIZE = 4;

    private DungeonTestArenaManager() {
    }

    public static boolean teleportPlayerToArena(ServerPlayer player) {
        ServerLevel level = player.server.getLevel(DUNGEON_TEST_LEVEL);
        if (level == null) {
            dungeonrealms.LOGGER.error("Cannot teleport {} to test arena: dungeon dimension {} not found.",
                    player.getScoreboardName(), DUNGEON_TEST_LEVEL.location());
            return false;
        }

        ensureArena(level);
        player.teleportTo(level, ARENA_SPAWN.getX() + 0.5, ARENA_SPAWN.getY() + 1.0, ARENA_SPAWN.getZ() + 0.5, player.getYRot(), player.getXRot());
        return true;
    }

    public static boolean isInsideArena(ServerPlayer player) {
        if (!player.level().dimension().equals(DUNGEON_TEST_LEVEL)) {
            return false;
        }

        int px = Mth.floor(player.getX());
        int py = Mth.floor(player.getY());
        int pz = Mth.floor(player.getZ());
        return px >= ARENA_SPAWN.getX() - HALF_SIZE
                && px <= ARENA_SPAWN.getX() + HALF_SIZE
                && pz >= ARENA_SPAWN.getZ() - HALF_SIZE
                && pz <= ARENA_SPAWN.getZ() + HALF_SIZE
                && py >= ARENA_SPAWN.getY()
                && py <= ARENA_SPAWN.getY() + 4;
    }

    private static void ensureArena(ServerLevel level) {
        for (int x = -HALF_SIZE; x <= HALF_SIZE; x++) {
            for (int z = -HALF_SIZE; z <= HALF_SIZE; z++) {
                BlockPos floor = ARENA_SPAWN.offset(x, 0, z);
                boolean edge = Math.abs(x) == HALF_SIZE || Math.abs(z) == HALF_SIZE;
                level.setBlockAndUpdate(floor, edge ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState());

                BlockPos air1 = floor.above();
                BlockPos air2 = floor.above(2);
                level.setBlockAndUpdate(air1, Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(air2, Blocks.AIR.defaultBlockState());
            }
        }

        level.setBlockAndUpdate(ARENA_SPAWN.above(), Blocks.TORCH.defaultBlockState());
        buildReturnPortal(level, ARENA_SPAWN.offset(0, 1, HALF_SIZE - 1));
    }

    private static void buildReturnPortal(ServerLevel level, BlockPos interiorMin) {
        BlockPos frameMin = interiorMin.offset(-1, -1, 0);

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 4; x++) {
                BlockPos pos = frameMin.offset(x, y, 0);
                boolean frame = x == 0 || x == 3 || y == 0 || y == 4;
                if (frame) {
                    level.setBlockAndUpdate(pos, dungeonrealms.DUNGEON_PORTAL_FRAME.get().defaultBlockState());
                } else {
                    level.setBlockAndUpdate(
                            pos,
                            dungeonrealms.DUNGEON_PORTAL.get().defaultBlockState().setValue(DungeonPortalBlock.AXIS, Direction.Axis.X));
                }
            }
        }
    }
}
