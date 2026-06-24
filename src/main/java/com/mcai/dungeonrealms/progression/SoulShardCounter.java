package com.mcai.dungeonrealms.progression;

import com.mcai.dungeonrealms.dungeonrealms;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

// Counts physical Soul Shard items carried by the player, including inside Soul Pouches.
public final class SoulShardCounter {
    private SoulShardCounter() {
    }

    public static int countTotalSoulShards(ServerPlayer player) {
        long total = 0L;
        for (ItemStack stack : player.getInventory().items) {
            total += countInStack(stack);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            total += countInStack(stack);
        }
        return (int) Math.min(Integer.MAX_VALUE, total);
    }

    private static long countInStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0L;
        }
        if (stack.is(dungeonrealms.SOUL_SHARD.get())) {
            return stack.getCount();
        }
        if (stack.is(dungeonrealms.SOUL_POUCH.get())) {
            BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (contents == null || contents.isEmpty()) {
                return 0L;
            }

            long subtotal = 0L;
            for (ItemStack contentStack : contents.itemsCopy()) {
                if (contentStack.is(dungeonrealms.SOUL_SHARD.get())) {
                    subtotal += contentStack.getCount();
                }
            }
            return subtotal;
        }
        return 0L;
    }
}
