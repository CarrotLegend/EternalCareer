package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.StableAttributeModifiers;

import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class FletchingModificationAttributeEvents {

    private static final ResourceLocation RANGED_DAMAGE =
            new ResourceLocation("puffish_attributes", "ranged_damage");

    private static final ResourceLocation RANGED_VELOCITY =
            new ResourceLocation("terra_curio", "ranged_velocity");

    private static final UUID POWER_UUID =
            StableAttributeModifiers.id(
                    "eternal_career:fletching/power"
            );

    private static final UUID VELOCITY_UUID =
            StableAttributeModifiers.id(
                    "eternal_career:fletching/velocity"
            );

    private FletchingModificationAttributeEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();

        int powerLevel = 0;
        int velocityLevel = 0;

        if (BowModificationHelper.isBow(mainHand)) {
            powerLevel = BowModificationHelper.getLevel(
                    mainHand,
                    BowModifications.POWER.id()
            );

            velocityLevel = BowModificationHelper.getLevel(
                    mainHand,
                    BowModifications.VELOCITY.id()
            );
        }

        double powerAmount =
                powerLevel * BowModifications.POWER.amountPerLevel();

        double velocityAmount =
                velocityLevel * BowModifications.VELOCITY.amountPerLevel();

        updateModifier(
                player,
                RANGED_DAMAGE,
                POWER_UUID,
                "eternal_career:fletching/power",
                powerAmount
        );

        updateModifier(
                player,
                RANGED_VELOCITY,
                VELOCITY_UUID,
                "eternal_career:fletching/velocity",
                velocityAmount
        );
    }

    private static void updateModifier(
            ServerPlayer player,
            ResourceLocation attributeId,
            UUID uuid,
            String name,
            double amount
    ) {
        Attribute attribute =
                ForgeRegistries.ATTRIBUTES.getValue(attributeId);

        if (attribute == null) {
            return;
        }

        AttributeInstance instance =
                player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        AttributeModifier current =
                instance.getModifier(uuid);

        if (amount <= 0.0D) {
            if (current != null) {
                instance.removeModifier(uuid);
            }

            return;
        }

        if (current != null
                && current.getOperation()
                == AttributeModifier.Operation.MULTIPLY_BASE
                && Double.compare(
                        current.getAmount(),
                        amount
                ) == 0) {
            return;
        }

        if (current != null) {
            instance.removeModifier(uuid);
        }

        instance.addTransientModifier(
                new AttributeModifier(
                        uuid,
                        name,
                        amount,
                        AttributeModifier.Operation.MULTIPLY_BASE
                )
        );
    }
}