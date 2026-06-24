package com.mcai.dungeonrealms.run;

import static net.minecraft.commands.Commands.literal;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.mcai.dungeonrealms.dungeonrealms;
import com.mcai.dungeonrealms.network.DungeonRealmsNetwork;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

// Owns active run lifecycle and provides debug commands for validation.
public final class RunManager {
    private static final int PORTAL_COOLDOWN_TICKS = 40;
    private static final int BASE_RUN_REWARD_SOUL_SHARDS = 5;
    private static final RunManager INSTANCE = new RunManager();
    private final Map<UUID, RunState> activeRuns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> portalCooldownUntilTick = new ConcurrentHashMap<>();

    private RunManager() {
    }

    public static RunManager get() {
        return INSTANCE;
    }

    // Called by portal collision; toggles start/end based on active run state.
    public void onPortalContact(ServerPlayer player) {
        long now = player.level().getGameTime();
        long availableAt = portalCooldownUntilTick.getOrDefault(player.getUUID(), 0L);
        if (now < availableAt) {
            return;
        }
        portalCooldownUntilTick.put(player.getUUID(), now + PORTAL_COOLDOWN_TICKS);

        boolean activeRun = getActiveRun(player).isPresent();
        if (activeRun) {
            endRun(player, RunEndReason.PORTAL_EXIT);
            player.sendSystemMessage(Component.literal("Run completed. Overworld state restored.")
                    .withStyle(ChatFormatting.GREEN));
            return;
        }

        // Prevent accidental restart loops from the arena return portal.
        if (DungeonTestArenaManager.isInsideArena(player)) {
            player.sendSystemMessage(Component.literal("Return portal is exit-only. Use an overworld portal to start a new run.")
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }

        if (startRun(player)) {
            player.sendSystemMessage(Component.literal("Run started. Entering dungeon test arena.")
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    public boolean startRun(ServerPlayer player) {
        // Guard against duplicate run starts for the same player.
        if (activeRuns.containsKey(player.getUUID())) {
            return false;
        }

        // Snapshot overworld state before mutating the player for dungeon mode.
        PlayerStateSnapshot snapshot = PlayerStateSnapshot.capture(player);
        long seed = player.serverLevel().getSeed() ^ player.getUUID().getMostSignificantBits();
        RunState runState = new RunState(UUID.randomUUID(), seed, 0, 0, snapshot);
        activeRuns.put(player.getUUID(), runState);

        // Reset to clean dungeon baseline.
        player.getInventory().clearContent();
        player.removeAllEffects();
        player.setHealth(player.getMaxHealth());
        player.setAbsorptionAmount(0.0F);
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0F);
        player.getFoodData().setExhaustion(0.0F);
        player.setAirSupply(player.getMaxAirSupply());
        player.clearFire();
        if (!DungeonTestArenaManager.teleportPlayerToArena(player)) {
            // Fail closed: do not leave player in run state if teleport destination is unavailable.
            activeRuns.remove(player.getUUID());
            snapshot.restore(player);
            DungeonRealmsNetwork.syncOverlay(player);
            dungeonrealms.LOGGER.error("Run start aborted for {} because dungeon arena teleport failed.",
                    player.getScoreboardName());
            return false;
        }
        player.containerMenu.broadcastChanges();
        DungeonRealmsNetwork.syncOverlay(player);

        dungeonrealms.LOGGER.info("Started run {} for player {}", runState.runId(), player.getScoreboardName());
        return true;
    }

    public boolean endRun(ServerPlayer player) {
        return endRun(player, RunEndReason.MANUAL_COMMAND);
    }

    public boolean endRun(ServerPlayer player, RunEndReason reason) {
        RunState runState = activeRuns.remove(player.getUUID());
        if (runState == null) {
            return false;
        }

        // Always restore saved overworld state on run exit.
        runState.playerSnapshot().restore(player);
        if (reason.rewardsPlayer()) {
            grantSoulShardItems(player, BASE_RUN_REWARD_SOUL_SHARDS);
        }
        DungeonRealmsNetwork.syncOverlay(player);

        dungeonrealms.LOGGER.info("Ended run {} for player {}", runState.runId(), player.getScoreboardName());
        return true;
    }

    public Optional<RunState> getActiveRun(ServerPlayer player) {
        return Optional.ofNullable(activeRuns.get(player.getUUID()));
    }

    // Debug command surface while portal-driven flow is still in progress.
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        registerCommands(event.getDispatcher());
    }

    // Safety net: if player dies during a run, restore overworld state immediately.
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        endRun(player, RunEndReason.DEATH);
    }

    // Safety net: avoid leaving players stranded in run state after disconnect.
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            endRun(player, RunEndReason.LOGOUT);
        }
    }

    // Keep client overlay values in sync on login and world transitions.
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DungeonRealmsNetwork.syncOverlay(player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DungeonRealmsNetwork.syncOverlay(player);
        }
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("dr")
                        .requires(source -> source.hasPermission(2))
                        .then(literal("run")
                                .then(literal("start").executes(this::runStartCommand))
                                .then(literal("end").executes(this::runEndCommand))
                                .then(literal("status").executes(this::runStatusCommand))));
    }

    private int runStartCommand(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }

        if (!startRun(player)) {
            context.getSource().sendFailure(Component.literal("Run is already active for this player."));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Dungeon run started. Overworld state snapshot captured.")
                        .withStyle(ChatFormatting.GREEN),
                false);
        return 1;
    }

    private int runEndCommand(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }

        if (!endRun(player)) {
            context.getSource().sendFailure(Component.literal("No active run for this player."));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Dungeon run ended. Overworld state restored.")
                        .withStyle(ChatFormatting.GREEN),
                false);
        return 1;
    }

    private int runStatusCommand(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }

        Optional<RunState> run = getActiveRun(player);
        if (run.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("No active run."), false);
            return 1;
        }

        RunState runState = run.get();
        context.getSource().sendSuccess(
                () -> Component.literal("Active run: " + runState.runId() + " | seed: " + runState.seed()),
                false);
        return 1;
    }

    private void grantSoulShardItems(ServerPlayer player, int amount) {
        ItemStack reward = new ItemStack(dungeonrealms.SOUL_SHARD.get(), amount);
        boolean added = player.getInventory().add(reward);
        if (!added || !reward.isEmpty()) {
            player.drop(reward, false);
        }
        player.containerMenu.broadcastChanges();
    }

    public enum RunEndReason {
        MANUAL_COMMAND(true),
        PORTAL_EXIT(true),
        DEATH(false),
        LOGOUT(false);

        private final boolean rewardsPlayer;

        RunEndReason(boolean rewardsPlayer) {
            this.rewardsPlayer = rewardsPlayer;
        }

        public boolean rewardsPlayer() {
            return rewardsPlayer;
        }
    }
}
