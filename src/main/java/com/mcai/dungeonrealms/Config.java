package com.mcai.dungeonrealms;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// Central definition for server/common config values.
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Template debug toggles kept for now during early setup.
    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // Example of list validation for resource-location style config entries.
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    // Overlay configuration. Position can be changed in Mod Config.
    public static final ModConfigSpec.BooleanValue OVERLAY_ENABLED = BUILDER
            .comment("Show the Dungeon Realms HUD overlay.")
            .define("overlayEnabled", true);

    public static final ModConfigSpec.IntValue OVERLAY_X = BUILDER
            .comment("Overlay X position in pixels from the left.")
            .defineInRange("overlayX", 8, 0, 10000);

    public static final ModConfigSpec.IntValue OVERLAY_Y = BUILDER
            .comment("Overlay Y position in pixels from the top.")
            .defineInRange("overlayY", 8, 0, 10000);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
