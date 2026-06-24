package com.mcai.dungeonrealms;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// Client-only bootstrap. Keep all client-side hooks in this class.
@Mod(value = dungeonrealms.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = dungeonrealms.MODID, value = Dist.CLIENT)
public class dungeonrealmsClient {
    public dungeonrealmsClient(ModContainer container) {
        // Registers the auto-generated config UI on the Mods screen.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Basic startup log for client-side validation.
        dungeonrealms.LOGGER.info("HELLO FROM CLIENT SETUP");
        dungeonrealms.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
