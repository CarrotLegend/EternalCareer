package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModEffects;
import java.util.List;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Duplicates edible death drops while the dying entity is ingredient-marked. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class IngredientMarkDropEvents {
    private IngredientMarkDropEvents() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide
                || !event.getEntity().hasEffect(ModEffects.INGREDIENT_MARK.get())) {
            return;
        }

        for (ItemEntity original : List.copyOf(event.getDrops())) {
            ItemStack originalStack = original.getItem();
            if (!originalStack.isEdible() || originalStack.isEmpty()) {
                continue;
            }

            ItemEntity duplicate = new ItemEntity(
                    original.level(),
                    original.getX(),
                    original.getY(),
                    original.getZ(),
                    originalStack.copy());
            duplicate.setDeltaMovement(original.getDeltaMovement());
            duplicate.setDefaultPickUpDelay();
            event.getDrops().add(duplicate);
        }
    }
}
