package com.carrot123.eternal_career.bloodbow;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.career.api.RangeDamageHelper;
import com.carrot123.eternal_career.fletching.BowModificationHelper;
import com.carrot123.eternal_career.fletching.BowModifications;
import com.carrot123.eternal_career.fletching.FletchingBowCompat;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BloodBowCombatEvents {
    private static final ResourceLocation RANGED = new ResourceLocation("puffish_attributes", "ranged_damage");
    private static final UUID FANG_UUID = StableAttributeModifiers.id("eternal_career:fang_bow_upgrade");
    private static final String MARK = "EternalCareerBloodHuntUpgrade";
    private static final String MANUAL_FANG = "EternalCareerManualFangBonus";
    private BloodBowCombatEvents() {}

    public static double fangBonus(ItemStack bow) {
        if (!BowModificationHelper.isBow(bow)) return 0;
        return 0.20D * BowModificationHelper.getLevel(bow, BowModifications.FANG.id())
                + 0.50D * BowModificationHelper.getLevel(bow, BowModifications.FANG_II.id())
                + 1.00D * BowModificationHelper.getLevel(bow, BowModifications.FANG_III.id())
                + 4.00D * BowModificationHelper.getLevel(bow, BowModifications.FANG_IV.id());
    }

    @SubscribeEvent
    public static void onBowAttributes(ItemAttributeModifierEvent event) {
        if (event.getSlotType() != EquipmentSlot.MAINHAND || FletchingBowCompat.usesManualProjectileCompatibility(event.getItemStack())) return;
        double bonus = fangBonus(event.getItemStack());
        if (bonus <= 0) return;
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(RANGED);
        if (attribute != null) event.addModifier(attribute, new AttributeModifier(FANG_UUID, "eternal_career:fletching/fangs", bonus, AttributeModifier.Operation.MULTIPLY_BASE));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk() || !(event.getEntity() instanceof AbstractArrow arrow) || !(arrow.getOwner() instanceof ServerPlayer player)) return;
        ItemStack bow = findBow(player);
        if (bow.isEmpty()) return;
        CompoundTag data = arrow.getPersistentData();
        if (BowModificationHelper.getLevel(bow, BowModifications.BLOOD_HUNT.id()) > 0) data.putBoolean(MARK, true);
        double bonus = fangBonus(bow);
        if (bonus > 0 && FletchingBowCompat.usesManualProjectileCompatibility(bow)) data.putDouble(MANUAL_FANG, bonus);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRangedHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide() || event.getAmount() <= 0) return;
        double multiplier = 1.0D;
        MobEffectInstance mark = event.getEntity().getEffect(BloodBowEffects.BLOOD_HUNT_MARK.get());
        if (mark != null && RangeDamageHelper.isRanged(event.getSource())) multiplier += 0.10D * Math.min(5, mark.getAmplifier() + 1);
        if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow) multiplier *= 1.0D + Math.max(0, arrow.getPersistentData().getDouble(MANUAL_FANG));
        if (Double.isFinite(multiplier)) event.setAmount((float) Math.min(Float.MAX_VALUE, event.getAmount() * multiplier));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide() || event.getAmount() <= 0 || !(event.getSource().getDirectEntity() instanceof AbstractArrow arrow) || !arrow.getPersistentData().getBoolean(MARK)) return;
        LivingEntity target = event.getEntity();
        MobEffectInstance previous = target.getEffect(BloodBowEffects.BLOOD_HUNT_MARK.get());
        int stacks = Math.min(5, previous == null ? 1 : previous.getAmplifier() + 2);
        target.addEffect(new MobEffectInstance(BloodBowEffects.BLOOD_HUNT_MARK.get(), Integer.MAX_VALUE, stacks - 1, false, false, true));
    }

    private static ItemStack findBow(ServerPlayer player) {
        ItemStack using = player.getUseItem();
        if (BowModificationHelper.isBow(using)) return using;
        if (BowModificationHelper.isBow(player.getMainHandItem())) return player.getMainHandItem();
        return BowModificationHelper.isBow(player.getOffhandItem()) ? player.getOffhandItem() : ItemStack.EMPTY;
    }
}
