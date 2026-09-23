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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PlayerDamageEvents {

    public static final double INGREDIENT_MARK_KITCHENWARE_MULTIPLIER =
            1.25D;

    private PlayerDamageEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(
            LivingDamageEvent event
    ) {
        if (event.getEntity().level().isClientSide
                || !(event.getSource().getEntity()
                instanceof Player player)) {
            return;
        }

        double amount = event.getAmount();

        if (amount <= 0.0D) {
            return;
        }

        PlayerDamageCategory category =
                DamageCategoryHelper.classify(
                        player,
                        event.getSource()
                );

        boolean wasIngredientMarked =
                event.getEntity().hasEffect(
                        ModEffects.INGREDIENT_MARK.get()
                );

        Attribute categoryAttribute =
                category == PlayerDamageCategory.KITCHENWARE
                        ? ModAttributes.KITCHENWARE_DAMAGE.get()
                        : ModAttributes.NON_KITCHENWARE_DAMAGE.get();

        amount *= getMultiplier(
                player,
                categoryAttribute
        );

        if (ScytheCombat.directAttacker(
                event.getSource()
        ) == null) {
            amount *= getMultiplier(
                    player,
                    ModAttributes.NON_SCYTHE_DAMAGE.get()
            );
        }

        if (category == PlayerDamageCategory.KITCHENWARE
                && wasIngredientMarked) {
            amount *=
                    INGREDIENT_MARK_KITCHENWARE_MULTIPLIER;
        }

        if (!Double.isFinite(amount)) {
            amount = Float.MAX_VALUE;
        }

        amount = Math.max(
                0.0D,
                Math.min(
                        Float.MAX_VALUE,
                        amount
                )
        );

        event.setAmount((float) amount);

        if (amount > 0.0D
                && CurioEquipmentHelper.hasGodsRecognition(
                player
        )) {
            event.getEntity().addEffect(
                    new MobEffectInstance(
                            ModEffects.INGREDIENT_MARK.get(),
                            IngredientMarkEffect
                                    .INGREDIENT_MARK_DURATION_TICKS,
                            0,
                            false,
                            true,
                            true
                    ),
                    player
            );
        }
    }

    private static double getMultiplier(
            Player player,
            Attribute attribute
    ) {
        AttributeInstance instance =
                player.getAttribute(attribute);

        if (instance == null) {
            return 1.0D;
        }

        double value = instance.getValue();

        if (!Double.isFinite(value)) {
            return 1.0D;
        }

        return Math.max(0.0D, value);
    }
}