package com.carrot123.eternal_career.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public final class FistBladeItem extends SwordItem {
    private static final ResourceLocation BLEEDING_EFFECT =
            new ResourceLocation("legendarymonsters", "bleeding");
    private static final float BLEEDING_CHANCE = 0.25F;
    private static final int BLEEDING_DURATION = 100;

    public FistBladeItem(Tier tier, int displayedDamage, int durability, boolean fireResistant) {
        super(tier, displayedDamage - 1 - (int) tier.getAttackDamageBonus(),
                -1.8F, createProperties(durability, fireResistant));
    }

    private static Item.Properties createProperties(int durability, boolean fireResistant) {
        Item.Properties properties = new Item.Properties().durability(durability);
        if (fireResistant) {
            properties.fireResistant();
        }
        return properties;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean hit = super.hurtEnemy(stack, target, attacker);
        if (!hit || attacker.level().isClientSide
                || attacker.getRandom().nextFloat() >= BLEEDING_CHANCE) {
            return hit;
        }

        MobEffect bleeding = ForgeRegistries.MOB_EFFECTS.getValue(BLEEDING_EFFECT);
        if (bleeding != null) {
            target.addEffect(new MobEffectInstance(bleeding, BLEEDING_DURATION, 0));
        }
        return hit;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.eternal_career.fist_blade.bleeding")
                .withStyle(ChatFormatting.GOLD));
    }
}
