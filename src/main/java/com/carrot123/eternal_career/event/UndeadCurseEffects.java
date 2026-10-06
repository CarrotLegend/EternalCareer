package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.puffish.PuffishAttributesHelper;
import com.carrot123.eternal_career.item.UndeadCurseItem;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class UndeadCurseEffects {

    private static final UUID DAY_RESISTANCE_UUID =
            StableAttributeModifiers.id(
                    EternalCareer.MOD_ID
                            + ":undead_curse/day_resistance"
            );

    private static final UUID NIGHT_REGENERATION_UUID =
            StableAttributeModifiers.id(
                    EternalCareer.MOD_ID
                            + ":undead_curse/night_regeneration"
            );

    private UndeadCurseEffects() {
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player =
                event.player;

        if (player.level().isClientSide) {
            return;
        }

        ItemStack curse =
                findEquippedCurse(player);

        if (curse.isEmpty()) {
            removeDynamicAttributes(player);
            return;
        }

        if (!UndeadCurseItem.isUpgraded(curse)) {
            removeDynamicAttributes(player);
            tickSunBurn(player);
            return;
        }

        boolean daytime =
                player.level().isDay();

        syncModifier(
                player,
                PuffishAttributesHelper.RESISTANCE,
                DAY_RESISTANCE_UUID,
                daytime
        );

        syncModifier(
                player,
                PuffishAttributesHelper.NATURAL_REGENERATION,
                NIGHT_REGENERATION_UUID,
                !daytime
        );
    }

    private static ItemStack findEquippedCurse(
            Player player
    ) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler ->
                        handler.findCurios(
                                        ModItems.UNDEAD_CURSE.get()
                                )
                                .stream()
                                .filter(result ->
                                        UndeadCurseItem.SLOT.equals(
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

    private static void tickSunBurn(
            Player player
    ) {
        if (!player.level().isDay()) {
            return;
        }

        float brightness =
                player.getLightLevelDependentMagicValue();

        if (brightness <= 0.5F) {
            return;
        }

        if (player.getRandom().nextFloat() * 30.0F
                >= (brightness - 0.4F) * 2.0F) {
            return;
        }

        if (player.isInWaterRainOrBubble()) {
            return;
        }

        BlockPos position =
                BlockPos.containing(
                        player.getX(),
                        player.getEyeY(),
                        player.getZ()
                );

        if (!player.level().canSeeSky(position)) {
            return;
        }

        ItemStack helmet =
                player.getItemBySlot(
                        EquipmentSlot.HEAD
                );

        if (!helmet.isEmpty()) {
            damageHelmet(
                    player,
                    helmet
            );

            return;
        }

        player.setSecondsOnFire(8);
    }

    private static void damageHelmet(
            Player player,
            ItemStack helmet
    ) {
        if (!helmet.isDamageableItem()) {
            return;
        }

        if (player.getRandom().nextBoolean()) {
            return;
        }

        helmet.hurtAndBreak(
                1,
                player,
                entity ->
                        entity.broadcastBreakEvent(
                                EquipmentSlot.HEAD
                        )
        );
    }

    private static void removeDynamicAttributes(
            Player player
    ) {
        syncModifier(
                player,
                PuffishAttributesHelper.RESISTANCE,
                DAY_RESISTANCE_UUID,
                false
        );

        syncModifier(
                player,
                PuffishAttributesHelper.NATURAL_REGENERATION,
                NIGHT_REGENERATION_UUID,
                false
        );
    }

    private static void syncModifier(
            Player player,
            ResourceLocation attributeId,
            UUID modifierId,
            boolean enabled
    ) {
        Attribute attribute =
                PuffishAttributesHelper.resolve(
                        attributeId
                );

        if (attribute == null) {
            return;
        }

        AttributeInstance instance =
                player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        AttributeModifier current =
                instance.getModifier(
                        modifierId
                );

        if (!enabled) {
            if (current != null) {
                instance.removeModifier(
                        modifierId
                );
            }

            return;
        }

        if (current != null
                && current.getOperation()
                == AttributeModifier.Operation.MULTIPLY_BASE
                && Double.compare(
                        current.getAmount(),
                        0.125D
                ) == 0) {
            return;
        }

        if (current != null) {
            instance.removeModifier(
                    modifierId
            );
        }

        instance.addTransientModifier(
                new AttributeModifier(
                        modifierId,
                        EternalCareer.MOD_ID
                                + ":undead_curse",
                        0.125D,
                        AttributeModifier.Operation.MULTIPLY_BASE
                )
        );
    }
}