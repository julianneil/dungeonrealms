package com.mcai.dungeonrealms.run;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

// Snapshot of all player state that must survive a dungeon run.
public final class PlayerStateSnapshot {
    private final List<ItemStack> items;
    private final List<ItemStack> armor;
    private final List<ItemStack> offhand;
    private final List<MobEffectInstance> effects;
    private final int experienceLevel;
    private final int totalExperience;
    private final float experienceProgress;
    private final int selectedSlot;
    private final float health;
    private final float absorption;
    private final int foodLevel;
    private final float saturation;
    private final float exhaustion;
    private final int airSupply;
    private final int fireTicks;
    private final ResourceKey<Level> dimension;
    private final Vec3 position;
    private final float yRot;
    private final float xRot;

    private PlayerStateSnapshot(
            List<ItemStack> items,
            List<ItemStack> armor,
            List<ItemStack> offhand,
            List<MobEffectInstance> effects,
            int experienceLevel,
            int totalExperience,
            float experienceProgress,
            int selectedSlot,
            float health,
            float absorption,
            int foodLevel,
            float saturation,
            float exhaustion,
            int airSupply,
            int fireTicks,
            ResourceKey<Level> dimension,
            Vec3 position,
            float yRot,
            float xRot) {
        this.items = items;
        this.armor = armor;
        this.offhand = offhand;
        this.effects = effects;
        this.experienceLevel = experienceLevel;
        this.totalExperience = totalExperience;
        this.experienceProgress = experienceProgress;
        this.selectedSlot = selectedSlot;
        this.health = health;
        this.absorption = absorption;
        this.foodLevel = foodLevel;
        this.saturation = saturation;
        this.exhaustion = exhaustion;
        this.airSupply = airSupply;
        this.fireTicks = fireTicks;
        this.dimension = dimension;
        this.position = position;
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public static PlayerStateSnapshot capture(ServerPlayer player) {
        // Capture a deep copy so run-time mutations never affect saved overworld state.
        return new PlayerStateSnapshot(
                copyStacks(player.getInventory().items),
                copyStacks(player.getInventory().armor),
                copyStacks(player.getInventory().offhand),
                copyEffects(player.getActiveEffects()),
                player.experienceLevel,
                player.totalExperience,
                player.experienceProgress,
                player.getInventory().selected,
                player.getHealth(),
                player.getAbsorptionAmount(),
                player.getFoodData().getFoodLevel(),
                player.getFoodData().getSaturationLevel(),
                player.getFoodData().getExhaustionLevel(),
                player.getAirSupply(),
                player.getRemainingFireTicks(),
                player.level().dimension(),
                player.position(),
                player.getYRot(),
                player.getXRot());
    }

    public void restore(ServerPlayer player) {
        // Restore inventory/loadout first so downstream systems see consistent equipment.
        restoreStacks(player.getInventory().items, this.items);
        restoreStacks(player.getInventory().armor, this.armor);
        restoreStacks(player.getInventory().offhand, this.offhand);
        player.getInventory().selected = this.selectedSlot;

        // Effects are replaced as a full set to avoid leftovers.
        player.removeAllEffects();
        for (MobEffectInstance effect : this.effects) {
            player.addEffect(new MobEffectInstance(effect));
        }

        player.setHealth(Math.min(this.health, player.getMaxHealth()));
        player.setAbsorptionAmount(this.absorption);

        player.experienceLevel = this.experienceLevel;
        player.totalExperience = this.totalExperience;
        player.experienceProgress = this.experienceProgress;

        player.getFoodData().setFoodLevel(this.foodLevel);
        player.getFoodData().setSaturation(this.saturation);
        player.getFoodData().setExhaustion(this.exhaustion);
        player.setAirSupply(this.airSupply);
        player.setRemainingFireTicks(this.fireTicks);

        // Restore exact location + rotation, including cross-dimension return.
        if (!player.level().dimension().equals(this.dimension)) {
            ServerLevel targetLevel = player.server.getLevel(this.dimension);
            if (targetLevel != null) {
                player.teleportTo(
                        targetLevel,
                        this.position.x,
                        this.position.y,
                        this.position.z,
                        this.yRot,
                        this.xRot);
            }
        } else {
            player.teleportTo(this.position.x, this.position.y, this.position.z);
            player.setYRot(this.yRot);
            player.setXRot(this.xRot);
        }

        player.containerMenu.broadcastChanges();
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        List<ItemStack> copy = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            copy.add(stack.copy());
        }
        return copy;
    }

    private static List<MobEffectInstance> copyEffects(Collection<MobEffectInstance> effects) {
        List<MobEffectInstance> copy = new ArrayList<>(effects.size());
        for (MobEffectInstance effect : effects) {
            copy.add(new MobEffectInstance(effect));
        }
        return copy;
    }

    private static void restoreStacks(List<ItemStack> target, List<ItemStack> source) {
        // Fill shared slots, then clear any remainder to prevent stale items.
        int max = Math.min(target.size(), source.size());
        for (int i = 0; i < max; i++) {
            target.set(i, source.get(i).copy());
        }
        for (int i = max; i < target.size(); i++) {
            target.set(i, ItemStack.EMPTY);
        }
    }
}
