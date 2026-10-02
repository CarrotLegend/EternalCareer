package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.StableAttributeModifiers;

import java.util.UUID;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class FletchingModificationAttributeEvents {

    private static final ResourceLocation RANGED_DAMAGE =
            new ResourceLocation(
                    "puffish_attributes",
                    "ranged_damage"
            );

    private static final ResourceLocation RANGED_VELOCITY =
            new ResourceLocation(
                    "terra_curio",
                    "ranged_velocity"
            );

    private static final ResourceLocation CHARGE_SPEED =
            new ResourceLocation(
                    "until_eternity",
                    "charge_speed"
            );

    private static final UUID POWER_UUID =
            StableAttributeModifiers.id(
                    "eternal_career:fletching/power"
            );

    private static final UUID VELOCITY_UUID =
            StableAttributeModifiers.id(
                    "eternal_career:fletching/velocity"
            );

    private static final UUID RANGER_UUID =
            StableAttributeModifiers.id(
                    "eternal_career:fletching/ranger"
            );

    private static final UUID END_UUID =
            StableAttributeModifiers.id(
                    "eternal_career:fletching/end"
            );

    private FletchingModificationAttributeEvents() {
    }

    @SubscribeEvent
    public static void onItemAttributes(
            ItemAttributeModifierEvent event
    ) {
        if (event.getSlotType()
                != EquipmentSlot.MAINHAND) {
            return;
        }

        ItemStack stack =
                event.getItemStack();

        if (!BowModificationHelper.isBow(stack)) {
            return;
        }

        int powerLevel =
                BowModificationHelper.getLevel(
                        stack,
                        BowModifications.POWER.id()
                );

        int velocityLevel =
                BowModificationHelper.getLevel(
                        stack,
                        BowModifications.VELOCITY.id()
                );

        int rangerLevel =
                BowModificationHelper.getLevel(
                        stack,
                        BowModifications.RANGER.id()
                );

        int endLevel =
                BowModificationHelper.getLevel(
                        stack,
                        BowModifications.END.id()
                );

        boolean manualProjectile =
                FletchingBowCompat
                        .usesManualProjectileCompatibility(
                                stack
                        );

        if (!manualProjectile) {
            addModifier(
                    event,
                    RANGED_DAMAGE,
                    POWER_UUID,
                    "power",
                    powerLevel
                            * BowModifications.POWER
                            .amountPerLevel()
            );

            addModifier(
                    event,
                    RANGED_VELOCITY,
                    VELOCITY_UUID,
                    "velocity",
                    velocityLevel
                            * BowModifications.VELOCITY
                            .amountPerLevel()
            );

            addModifier(
                    event,
                    RANGED_DAMAGE,
                    END_UUID,
                    "end",
                    endLevel
                            * BowModifications.END
                            .amountPerLevel()
            );
        }

        addModifier(
                event,
                CHARGE_SPEED,
                RANGER_UUID,
                "ranger",
                rangerLevel
                        * BowModifications.RANGER
                        .amountPerLevel()
        );
    }

    private static void addModifier(
            ItemAttributeModifierEvent event,
            ResourceLocation attributeId,
            UUID uuid,
            String name,
            double amount
    ) {
        if (amount <= 0.0D) {
            return;
        }

        Attribute attribute =
                ForgeRegistries.ATTRIBUTES
                        .getValue(attributeId);

        if (attribute == null) {
            return;
        }

        event.addModifier(
                attribute,
                new AttributeModifier(
                        uuid,
                        "eternal_career:fletching/"
                                + name,
                        amount,
                        AttributeModifier.Operation
                                .MULTIPLY_BASE
                )
        );
    }
}