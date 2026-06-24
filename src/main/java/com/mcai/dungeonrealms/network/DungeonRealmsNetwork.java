package com.mcai.dungeonrealms.network;

import com.mcai.dungeonrealms.client.OverlayClientState;
import com.mcai.dungeonrealms.progression.SoulShardCounter;
import com.mcai.dungeonrealms.run.RunManager;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// Registers custom payloads and handles server->client overlay sync.
@EventBusSubscriber(modid = com.mcai.dungeonrealms.dungeonrealms.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class DungeonRealmsNetwork {
    private DungeonRealmsNetwork() {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(OverlaySyncPayload.TYPE, OverlaySyncPayload.STREAM_CODEC, (payload, context) -> {
                    OverlayClientState.update(payload.dimensionName(), payload.soulShards(), payload.runActive());
                });
    }

    public static void syncOverlay(ServerPlayer player) {
        String dimensionName = player.level().dimension().location().toString();
        int soulShards = SoulShardCounter.countTotalSoulShards(player);
        boolean runActive = RunManager.get().getActiveRun(player).isPresent();
        PacketDistributor.sendToPlayer(player, new OverlaySyncPayload(dimensionName, soulShards, runActive));
    }
}
