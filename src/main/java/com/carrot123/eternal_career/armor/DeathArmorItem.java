package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.client.renderer.armor.DeathArmorRenderer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class DeathArmorItem extends ArmorItem implements GeoItem {

    public static final double CRITICAL_CHANCE_PER_PIECE = 0.075D;
    public static final double CRITICAL_DAMAGE_PER_PIECE = 0.10D;

    private static final ResourceLocation CRITICAL_CHANCE =
            new ResourceLocation("terra_curio", "crit_chance");

    private static final ResourceLocation CRITICAL_DAMAGE =
            new ResourceLocation("obscure_api", "critical_damage");

    private final AnimatableInstanceCache animationCache =
            GeckoLibUtil.createInstanceCache(this);

    public DeathArmorItem(
            ArmorMaterial material,
            Type type,
            Properties properties
    ) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DeathArmorRenderer renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    HumanoidModel<?> originalModel
            ) {
                if (renderer == null) {
                    renderer = new DeathArmorRenderer();
                }

                renderer.prepForRender(
                        entity,
                        stack,
                        slot,
                        originalModel
                );

                return renderer;
            }
        });
    }

    @Override
    public String getArmorTexture(
            ItemStack stack,
            Entity entity,
            EquipmentSlot slot,
            String type
    ) {
        return EternalCareer.MOD_ID
                + ":textures/armor/death_armor.png";
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(
            EquipmentSlot slot
    ) {
        Multimap<Attribute, AttributeModifier> vanilla =
                super.getDefaultAttributeModifiers(slot);

        if (slot != getEquipmentSlot()) {
            return vanilla;
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder =
                ImmutableMultimap.builder();

        builder.putAll(vanilla);

        Attribute criticalChance =
                ForgeRegistries.ATTRIBUTES.getValue(CRITICAL_CHANCE);

        Attribute criticalDamage =
                ForgeRegistries.ATTRIBUTES.getValue(CRITICAL_DAMAGE);

        if (criticalChance != null) {
            builder.put(
                    criticalChance,
                    new AttributeModifier(
                            createModifierId(
                                    slot,
                                    "critical_chance"
                            ),
                            "Death armor critical chance",
                            CRITICAL_CHANCE_PER_PIECE,
                            AttributeModifier.Operation.ADDITION
                    )
            );
        }

        if (criticalDamage != null) {
            builder.put(
                    criticalDamage,
                    new AttributeModifier(
                            createModifierId(
                                    slot,
                                    "critical_damage"
                            ),
                            "Death armor critical damage",
                            CRITICAL_DAMAGE_PER_PIECE,
                            AttributeModifier.Operation.MULTIPLY_BASE
                    )
            );
        }

        return builder.build();
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    private static UUID createModifierId(
            EquipmentSlot slot,
            String attribute
    ) {
        String key =
                EternalCareer.MOD_ID
                        + ":death_armor/"
                        + slot.getName()
                        + "/"
                        + attribute;

        return UUID.nameUUIDFromBytes(
                key.getBytes(StandardCharsets.UTF_8)
        );
    }
}