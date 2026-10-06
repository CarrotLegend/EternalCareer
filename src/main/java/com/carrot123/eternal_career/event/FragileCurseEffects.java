package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.FragileCurseItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class FragileCurseEffects {

    private static final int EFFECT_DURATION =
            20 * 5;

    private FragileCurseEffects() {
    }

    @SubscribeEvent(
            priority = EventPriority.NORMAL
    )
    public static void onLivingAttack(
            LivingAttackEvent event
    ) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide) {
            return;
        }

        if (event.getAmount() <= 0.0F) {
            return;
        }

        if (event.getSource().getEntity() == null
                && event.getSource().getDirectEntity() == null) {
            return;
        }

        ItemStack curse =
                findEquippedCurse(player);

        if (curse.isEmpty()) {
            return;
        }

        if (FragileCurseItem.isReversed(curse)) {
            applyReversedEffect(player);
            return;
        }

        if (player.getRandom().nextFloat() >= 0.25F) {
            return;
        }

        applyNormalCurse(player);
    }

    private static ItemStack findEquippedCurse(
            Player player
    ) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler ->
                        handler.findCurios(
                                        ModItems.FRAGILE_CURSE.get()
                                )
                                .stream()
                                .filter(result ->
                                        FragileCurseItem.SLOT.equals(
                                                result.slotContext()
                                                        .identifier()
                                        )
                                )
                                .filter(result ->
                                        !result.slotContext()
                                                .cosmetic()
                                )
                                .map(result ->
                                        result.stack()
                                )
                                .findFirst()
                )
                .orElse(ItemStack.EMPTY);
    }

    private static void applyNormalCurse(
            Player player
    ) {
        player.addEffect(
                new MobEffectInstance(
                        MobEffects.DARKNESS,
                        EFFECT_DURATION,
                        0,
                        false,
                        true,
                        true
                )
        );

        player.addEffect(
                new MobEffectInstance(
                        MobEffects.WEAKNESS,
                        EFFECT_DURATION,
                        0,
                        false,
                        true,
                        true
                )
        );
    }

    private static void applyReversedEffect(
            Player player
    ) {
        player.addEffect(
                new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        EFFECT_DURATION,
                        3,
                        false,
                        true,
                        true
                )
        );
    }
}