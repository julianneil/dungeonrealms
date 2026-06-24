package com.mcai.dungeonrealms.client;

import com.mcai.dungeonrealms.Config;
import com.mcai.dungeonrealms.dungeonrealms;
import com.mcai.dungeonrealms.run.DungeonTestArenaManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

// Renders the movable top-left HUD overlay for dungeon run state.
@EventBusSubscriber(modid = dungeonrealms.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class DungeonOverlayRenderer {
    private static final ResourceLocation OVERLAY_ID = ResourceLocation.fromNamespaceAndPath(dungeonrealms.MODID, "dungeon_overlay");

    private DungeonOverlayRenderer() {
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, OVERLAY_ID, DungeonOverlayRenderer::render);
    }

    private static void render(GuiGraphics guiGraphics, net.minecraft.client.DeltaTracker partialTick) {
        if (!Config.OVERLAY_ENABLED.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        if (!minecraft.player.level().dimension().equals(DungeonTestArenaManager.DUNGEON_TEST_LEVEL)) {
            return;
        }

        int x = Config.OVERLAY_X.get();
        int y = Config.OVERLAY_Y.get();
        int line = 0;

        String title = "Dungeon Realms";
        String dimension = "Dimension: " + OverlayClientState.dimensionName();
        String runState = "Run: " + (OverlayClientState.runActive() ? "Active" : "Inactive");

        guiGraphics.drawString(minecraft.font, title, x, y + (line++ * 10), 0x55FF55, true);
        guiGraphics.drawString(minecraft.font, dimension, x, y + (line++ * 10), 0xFFFFFF, true);
        guiGraphics.drawString(minecraft.font, runState, x, y + (line * 10), 0xFFFF55, true);
    }
}
