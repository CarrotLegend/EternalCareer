package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.career.api.DamageCategoryHelper;
import com.carrot123.eternal_career.career.api.PlayerDamageCategory;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.effect.IngredientMarkEffect;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.registry.ModEffects;
import com.carrot123.eternal_career.soul.ScytheCombat;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerDamageEvents {
    public static final double INGREDIENT_MARK_KITCHENWARE_MULTIPLIER = 1.25D;

    private PlayerDamageEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide
                || !(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        PlayerDamageCategory category =
                DamageCategoryHelper.classify(player, event.getSource());

        boolean wasIngredientMarked =
                event.getEntity().hasEffect(ModEffects.INGREDIENT_MARK.get());

        Attribute multiplierAttribute =
                category == PlayerDamageCategory.KITCHENWARE
                        ? ModAttributes.KITCHENWARE_DAMAGE.get()
                        : ModAttributes.NON_KITCHENWARE_DAMAGE.get();

        double amount =
                event.getAmount()
                        * player.getAttributeValue(multiplierAttribute);

        if (ScytheCombat.directAttacker(event.getSource()) == null) {
            amount *= player.getAttributeValue(
                    ModAttributes.NON_SCYTHE_DAMAGE.get()
            );
        }

        if (category == PlayerDamageCategory.KITCHENWARE
                && wasIngredientMarked) {
            amount *= INGREDIENT_MARK_KITCHENWARE_MULTIPLIER;
        }

        if (amount < 0.0D) {
            amount = 0.0D;
        }

        if (amount > Float.MAX_VALUE) {
            amount = Float.MAX_VALUE;
        }

        event.setAmount((float) amount);

        if (amount > 0.0D
                && CurioEquipmentHelper.hasGodsRecognition(player)) {
            event.getEntity().addEffect(
                    new MobEffectInstance(
                            ModEffects.INGREDIENT_MARK.get(),
                            IngredientMarkEffect.INGREDIENT_MARK_DURATION_TICKS,
                            0,
                            false,
                            true,
                            true
                    ),
                    player
            );
        }
    }
}