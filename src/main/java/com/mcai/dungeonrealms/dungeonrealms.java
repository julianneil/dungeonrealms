package com.mcai.dungeonrealms;

import org.slf4j.Logger;

import com.mcai.dungeonrealms.item.SoulPouchItem;
import com.mojang.logging.LogUtils;
import com.mcai.dungeonrealms.portal.DungeonPortalActivationHandler;
import com.mcai.dungeonrealms.portal.DungeonPortalBlock;
import com.mcai.dungeonrealms.run.RunManager;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(dungeonrealms.MODID)
public class dungeonrealms {
    // Core mod constants and registries.
    public static final String MODID = "dungeonrealms";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Portal content registrations.
    public static final DeferredBlock<Block> DUNGEON_PORTAL_FRAME = BLOCKS.registerBlock(
            "dungeon_portal_frame",
            properties -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE)));
    public static final DeferredItem<BlockItem> DUNGEON_PORTAL_FRAME_ITEM = ITEMS.registerSimpleBlockItem("dungeon_portal_frame", DUNGEON_PORTAL_FRAME);

    public static final DeferredBlock<DungeonPortalBlock> DUNGEON_PORTAL = BLOCKS.registerBlock(
            "dungeon_portal",
            properties -> new DungeonPortalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_PORTAL).noLootTable()));

    // Core progression currency item.
    public static final DeferredItem<Item> SOUL_SHARD = ITEMS.registerSimpleItem("soul_shard");
    public static final DeferredItem<SoulPouchItem> SOUL_POUCH = ITEMS.registerItem("soul_pouch", SoulPouchItem::new, new Item.Properties().stacksTo(1));

    // Creative tab for dungeon items/blocks during development.
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DUNGEON_REALMS_TAB = CREATIVE_MODE_TABS.register("dungeonrealms_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.dungeonrealms"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> DUNGEON_PORTAL_FRAME_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(DUNGEON_PORTAL_FRAME_ITEM.get());
                output.accept(SOUL_SHARD.get());
                output.accept(SOUL_POUCH.get());
            }).build());

    // Mod bootstrap: register content, systems, and config.
    public dungeonrealms(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // Register content.
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register runtime systems and event handlers.
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(RunManager.get());
        NeoForge.EVENT_BUS.register(DungeonPortalActivationHandler.get());

        // Add entries to vanilla tabs.
        modEventBus.addListener(this::addCreative);

        // Register mod config file.
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Temporary bootstrap logs from the template config.
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // Keep frame block visible in vanilla building blocks for quick testing.
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(DUNGEON_PORTAL_FRAME_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(SOUL_SHARD);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(SOUL_POUCH);
        }
    }

    // Server lifecycle hook; useful place for startup validation later.
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
