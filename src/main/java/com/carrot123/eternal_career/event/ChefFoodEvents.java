package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.registry.ModEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChefFoodEvents {
    private static final int DURATION_TICKS = 2400;
    private static final TagKey<Item> SWEET_FOODS = TagKey.create(Registries.ITEM,
            new ResourceLocation(EternalCareer.MOD_ID, "sweet_foods"));
    private static final TagKey<Item> FEAST_FOODS = TagKey.create(Registries.ITEM,
            new ResourceLocation(EternalCareer.MOD_ID, "feast_foods"));

    private ChefFoodEvents() {
    }

    @SubscribeEvent
    public static void onFoodFinished(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        ItemStack food = event.getItem();
        if (!food.isEdible() || !CurioEquipmentHelper.hasHeadChefSheath(player)) {
            return;
        }
        if (food.is(SWEET_FOODS)) {
            refresh(player, ModEffects.SWEET_IMPULSE.get());
        }
        if (food.is(FEAST_FOODS)) {
            refresh(player, ModEffects.FEAST_SATISFACTION.get());
        }
    }

    private static void refresh(Player player, MobEffect effect) {
        player.removeEffect(effect);
        player.addEffect(new MobEffectInstance(effect, DURATION_TICKS, 0));
    }
}
