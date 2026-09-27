package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.CurioTooltipHelper;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class ArcheryMasterCoreItem extends Item implements ICurioItem {
    public static final String SLOT = "charm";
    public static final String MODE_KEY = "Mode";
    public static final int BURST = 0;
    public static final int RAPID = 1;
    private static final ResourceLocation RANGED_DAMAGE =
            new ResourceLocation("puffish_attributes", "ranged_damage");
    private static final ResourceLocation RANGED_VELOCITY =
            new ResourceLocation("terra_curio", "ranged_velocity");
    private static final ResourceLocation CHARGE_SPEED =
            new ResourceLocation("until_eternity", "charge_speed");

    public ArcheryMasterCoreItem(Properties properties) {
        super(properties);
    }

    public static int mode(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getInt(MODE_KEY) == RAPID ? RAPID : BURST;
    }

    public static void toggle(ItemStack stack) {
        stack.getOrCreateTag().putInt(MODE_KEY, mode(stack) == BURST ? RAPID : BURST);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null && SLOT.equals(context.identifier()) && !context.cosmetic();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext context, UUID slotUuid, ItemStack stack) {
        if (!canEquip(context, stack)) {
            return ImmutableMultimap.of();
        }
        ImmutableMultimap.Builder<Attribute, AttributeModifier> result = ImmutableMultimap.builder();
        if (mode(stack) == BURST) {
            add(result, RANGED_DAMAGE, slotUuid, "ranged_damage", 0.50D);
            add(result, RANGED_VELOCITY, slotUuid, "ranged_velocity", 0.20D);
        } else {
            add(result, RANGED_VELOCITY, slotUuid, "ranged_velocity", 0.50D);
            add(result, CHARGE_SPEED, slotUuid, "charge_speed", 0.40D);
        }
        return result.build();
    }

    private static void add(ImmutableMultimap.Builder<Attribute, AttributeModifier> result,
            ResourceLocation attributeId, UUID slotUuid, String path, double amount) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            throw new IllegalStateException("Missing archery master core attribute: " + attributeId);
        }
        String key = EternalCareer.MOD_ID + ":archery_master_core/" + slotUuid + "/" + path;
        result.put(attribute, StableAttributeModifiers.create(
                key, amount, AttributeModifier.Operation.MULTIPLY_BASE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        if (mode(stack) == BURST) {
            tooltip.add(Component.translatable("tooltip.eternal_career.archery_master_core.burst")
                    .withStyle(ChatFormatting.RED));
            CurioTooltipHelper.addLocalizedString(tooltip,
                    "tooltip.eternal_career.archery_master_core.burst_effect");
        } else {
            tooltip.add(Component.translatable("tooltip.eternal_career.archery_master_core.rapid")
                    .withStyle(ChatFormatting.AQUA));
            CurioTooltipHelper.addLocalizedString(tooltip,
                    "tooltip.eternal_career.archery_master_core.rapid_effect");
        }
    }
}
