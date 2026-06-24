package com.mcai.dungeonrealms.item;

import com.mcai.dungeonrealms.dungeonrealms;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// Bundle-style container that only accepts Soul Shard items.
public class SoulPouchItem extends BundleItem {
    public SoulPouchItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        ItemStack slotStack = slot.getItem();
        if (!slotStack.isEmpty() && !slotStack.is(dungeonrealms.SOUL_SHARD.get())) {
            return false;
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack stack,
            ItemStack other,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess access) {
        if (!other.isEmpty() && !other.is(dungeonrealms.SOUL_SHARD.get())) {
            return false;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }
}
