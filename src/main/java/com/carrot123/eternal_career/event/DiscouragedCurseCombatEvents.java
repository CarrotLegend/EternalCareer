package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.DiscouragedCurseItem;
import com.carrot123.eternal_career.item.PandoraBoxItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class DiscouragedCurseCombatEvents {

    private static final float EXTRA_DAMAGE_CHANCE =
            0.25F;

    private static final float IMMUNITY_CHANCE =
            0.07F;

    private DiscouragedCurseCombatEvents() {
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public static void onLivingAttack(
            LivingAttackEvent event
    ) {
        if (!(event.getEntity()
                instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide) {
            return;
        }

        ItemStack curse =
                findCurse(player);

        if (curse.isEmpty()
                || !DiscouragedCurseItem
                .isReversed(curse)) {
            return;
        }

        if (player.getRandom().nextFloat()
                < IMMUNITY_CHANCE) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onLivingDamage(
            LivingDamageEvent event
    ) {
        if (!(event.getEntity()
                instanceof Player player)) {
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
                findCurse(player);

        if (curse.isEmpty()
                || DiscouragedCurseItem
                .isReversed(curse)) {
            return;
        }

        if (player.getRandom().nextFloat()
                >= EXTRA_DAMAGE_CHANCE) {
            return;
        }

        event.setAmount(
                event.getAmount() * 2.0F
        );
    }

    private static ItemStack findCurse(
            Player player
    ) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler ->
                        handler.findCurios(
                                        ModItems.DISCOURAGED_CURSE.get()
                                )
                                .stream()
                                .filter(result ->
                                        !result.slotContext()
                                                .cosmetic()
                                )
                                .filter(result ->
                                        PandoraBoxItem.CURSE_SPIRIT_SLOT
                                                .equals(
                                                        result.slotContext()
                                                                .identifier()
                                                )
                                )
                                .map(result ->
                                        result.stack()
                                )
                                .findFirst()
                )
                .orElse(ItemStack.EMPTY);
    }
}