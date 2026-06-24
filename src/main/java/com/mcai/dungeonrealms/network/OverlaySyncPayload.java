package com.mcai.dungeonrealms.network;

import com.mcai.dungeonrealms.dungeonrealms;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Clientbound payload for HUD overlay information.
public record OverlaySyncPayload(String dimensionName, int soulShards, boolean runActive) implements CustomPacketPayload {
    public static final Type<OverlaySyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(dungeonrealms.MODID, "overlay_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OverlaySyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OverlaySyncPayload::dimensionName,
            ByteBufCodecs.VAR_INT, OverlaySyncPayload::soulShards,
            ByteBufCodecs.BOOL, OverlaySyncPayload::runActive,
            OverlaySyncPayload::new);

    @Override
    public Type<OverlaySyncPayload> type() {
        return TYPE;
    }
}
