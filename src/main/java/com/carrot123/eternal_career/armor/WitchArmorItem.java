package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class WitchArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public WitchArmorItem(Type type, Properties properties) {
        super(WitchArmorMaterial.INSTANCE, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private com.carrot123.eternal_career.client.renderer.armor.WitchArmorRenderer renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity,
                    ItemStack stack,
                    EquipmentSlot slot,
                    HumanoidModel<?> originalModel) {
                if (this.renderer == null) {
                    this.renderer = new com.carrot123.eternal_career.client.renderer.armor.WitchArmorRenderer();
                }
                this.renderer.prepForRender(entity, stack, slot, originalModel);
                return this.renderer;
            }
        });
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity) {
        return super.canEquip(stack, slot, entity)
                && (!(entity instanceof Player player) || RedemptionAccessController.canEquip(player, stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!RedemptionAccessController.canEquip(player, stack)) {
            return InteractionResultHolder.fail(stack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return EternalCareer.MOD_ID + ":textures/armor/witch_armor.png";
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> vanilla = super.getDefaultAttributeModifiers(slot);
        if (slot != getEquipmentSlot()) {
            return vanilla;
        }
        String key = EternalCareer.MOD_ID + ":witch_armor/" + slot.getName() + "/brew_damage";
        UUID id = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
        return ImmutableMultimap.<Attribute, AttributeModifier>builder().putAll(vanilla)
                .put(ModAttributes.BREW_DAMAGE.get(), new AttributeModifier(id, "Witch armor brew damage",
                        0.4D, AttributeModifier.Operation.MULTIPLY_BASE)).build();
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        lines.add(Component.translatable("tooltip.eternal_career.witch_armor.brew_damage").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.eternal_career.witch_armor.set_bonus").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }
}
