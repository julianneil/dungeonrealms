package com.mcai.dungeonrealms.portal;

import com.mcai.dungeonrealms.dungeonrealms;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

// Handles portal ignition from right-click + flint and steel.
public final class DungeonPortalActivationHandler {
    private static final int MIN_WIDTH = 2;
    private static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    private static final int MAX_HEIGHT = 21;
    private static final DungeonPortalActivationHandler INSTANCE = new DungeonPortalActivationHandler();

    private DungeonPortalActivationHandler() {
    }

    public static DungeonPortalActivationHandler get() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Server-only, main-hand only activation path.
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack heldItem = event.getEntity().getMainHandItem();
        if (!heldItem.is(Items.FLINT_AND_STEEL)) {
            return;
        }

        Level level = event.getLevel();
        BlockPos clickedPos = event.getPos();
        BlockPos targetPos = clickedPos.relative(event.getFace());

        if (!isInterior(level, targetPos)) {
            return;
        }

        // Match nether-style behavior: attempt both frame orientations.
        boolean created = tryCreatePortal(level, targetPos, Direction.Axis.X)
                || tryCreatePortal(level, targetPos, Direction.Axis.Z);
        if (!created) {
            return;
        }

        EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        heldItem.hurtAndBreak(1, event.getEntity(), slot);
        level.playSound(null, targetPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private boolean tryCreatePortal(Level level, BlockPos start, Direction.Axis axis) {
        // Build a candidate rectangle around the clicked interior block.
        Direction forward = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction backward = forward.getOpposite();

        BlockPos base = findBase(level, start);
        int leftInterior = countInteriorUntilFrame(level, base, backward);
        int rightInterior = countInteriorUntilFrame(level, base, forward);
        if (leftInterior < 0 || rightInterior < 0) {
            return false;
        }

        int width = leftInterior + 1 + rightInterior;
        if (width < MIN_WIDTH || width > MAX_WIDTH) {
            return false;
        }

        BlockPos leftMost = base.relative(backward, leftInterior);
        if (!hasFrameFloor(level, leftMost, forward, width)) {
            return false;
        }

        int height = detectHeight(level, leftMost, forward, backward, width);
        if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
            return false;
        }

        fillPortal(level, leftMost, forward, width, height, axis);
        return true;
    }

    private BlockPos findBase(Level level, BlockPos pos) {
        // Move downward to the first interior block above the frame floor.
        BlockPos current = pos;
        for (int i = 0; i < MAX_HEIGHT; i++) {
            BlockPos below = current.below();
            if (!isInterior(level, below)) {
                break;
            }
            current = below;
        }
        return current;
    }

    private int countInteriorUntilFrame(Level level, BlockPos origin, Direction dir) {
        // Walk interior blocks until a valid frame edge is found.
        int count = 0;
        BlockPos cursor = origin;
        while (count < MAX_WIDTH) {
            BlockPos next = cursor.relative(dir);
            BlockState state = level.getBlockState(next);
            if (isFrame(state)) {
                return count;
            }
            if (!isInterior(level, next)) {
                return -1;
            }
            cursor = next;
            count++;
        }
        return -1;
    }

    private boolean hasFrameFloor(Level level, BlockPos leftMost, Direction step, int width) {
        // Every interior column must sit on frame material.
        for (int x = 0; x < width; x++) {
            BlockPos floorPos = leftMost.relative(step, x).below();
            if (!isFrame(level.getBlockState(floorPos))) {
                return false;
            }
        }
        return true;
    }

    private int detectHeight(Level level, BlockPos leftMost, Direction right, Direction left, int width) {
        // Height is valid when side walls stay frame material and a full top frame closes.
        int sideLeftOffset = 1;
        int sideRightOffset = width;

        for (int y = 0; y <= MAX_HEIGHT; y++) {
            BlockPos leftWallPos = leftMost.relative(left, sideLeftOffset).above(y);
            BlockPos rightWallPos = leftMost.relative(right, sideRightOffset).above(y);
            if (!isFrame(level.getBlockState(leftWallPos)) || !isFrame(level.getBlockState(rightWallPos))) {
                return -1;
            }

            boolean topRow = true;
            for (int x = 0; x < width; x++) {
                BlockPos interiorPos = leftMost.relative(right, x).above(y);
                BlockState interiorState = level.getBlockState(interiorPos);
                if (isFrame(interiorState)) {
                    continue;
                }
                if (!isInterior(level, interiorPos)) {
                    return -1;
                }
                topRow = false;
            }

            if (topRow) {
                return y;
            }
        }
        return -1;
    }

    private void fillPortal(Level level, BlockPos leftMost, Direction step, int width, int height, Direction.Axis axis) {
        // Fill interior with the dungeon portal block.
        BlockState portalState = dungeonrealms.DUNGEON_PORTAL.get().defaultBlockState().setValue(DungeonPortalBlock.AXIS, axis);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BlockPos interiorPos = leftMost.relative(step, x).above(y);
                level.setBlock(interiorPos, portalState, 18);
            }
        }
    }

    private boolean isInterior(Level level, BlockPos pos) {
        // Interior can be empty, fire, or already-portal.
        BlockState state = level.getBlockState(pos);
        return state.isAir()
                || state.is(dungeonrealms.DUNGEON_PORTAL.get())
                || state.is(BlockTags.FIRE);
    }

    private boolean isFrame(BlockState state) {
        return state.is(dungeonrealms.DUNGEON_PORTAL_FRAME.get());
    }
}
