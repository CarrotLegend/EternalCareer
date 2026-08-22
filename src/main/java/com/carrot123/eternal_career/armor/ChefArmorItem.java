package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.redemption.RedemptionItemHelper;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/** A chef armor piece with slot-bound career damage modifiers. */
public final class ChefArmorItem extends ArmorItem implements GeoItem {
    public static final double KITCHENWARE_BONUS_PER_PIECE = 0.15D;
    public static final double LUCK_BONUS_PER_PIECE = 10.0D;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public ChefArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private com.carrot123.eternal_career.client.renderer.armor.ChefArmorRenderer renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    HumanoidModel<?> originalModel) {
                if (this.renderer == null) {
                    this.renderer = new com.carrot123.eternal_career.client.renderer.armor.ChefArmorRenderer();
                }
                this.renderer.prepForRender(entity, stack, slot, originalModel);
                return this.renderer;
            }
        });
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
        return super.canEquip(stack, slot, entity)
                && (!(entity instanceof Player player)
                || RedemptionItemHelper.canUseRedemptionItem(player, stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!RedemptionItemHelper.canUseRedemptionItem(player, stack)) {
            return InteractionResultHolder.fail(stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return EternalCareer.MOD_ID + ":textures/armor/chef_armor.png";
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> vanillaModifiers =
                super.getDefaultAttributeModifiers(slot);
        if (slot != getEquipmentSlot()) {
            return vanillaModifiers;
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers =
                ImmutableMultimap.builder();
        modifiers.putAll(vanillaModifiers);
        modifiers.put(ModAttributes.KITCHENWARE_DAMAGE.get(), new AttributeModifier(
                stableModifierId(slot, "kitchenware_damage"),
                "Chef armor kitchenware damage",
                KITCHENWARE_BONUS_PER_PIECE,
                AttributeModifier.Operation.MULTIPLY_BASE));
        modifiers.put(Attributes.LUCK, new AttributeModifier(
                stableModifierId(slot, "luck"),
                "Chef armor luck",
                LUCK_BONUS_PER_PIECE,
                AttributeModifier.Operation.ADDITION));
        return modifiers.build();
    }

    private static UUID stableModifierId(EquipmentSlot slot, String attributePath) {
        String key = EternalCareer.MOD_ID + ":chef_armor/" + slot.getName() + "/" + attributePath;
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // The chef armor currently has no independent GeckoLib animations.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }
}
