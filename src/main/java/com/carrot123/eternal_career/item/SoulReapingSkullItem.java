package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.FoodBookCurio;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class SoulReapingSkullItem extends Item implements ICurioItem {
    public static final double SCYTHE_DAMAGE_BONUS = 0.25D;

    private static final String SCYTHE_DAMAGE_MODIFIER_KEY =
            EternalCareer.MOD_ID + ":soul_reaping_skull/scythe_damage";

    private static final UUID SCYTHE_DAMAGE_MODIFIER_ID =
            StableAttributeModifiers.id(SCYTHE_DAMAGE_MODIFIER_KEY);

    public SoulReapingSkullItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return isFunctionalCharmSlot(slotContext);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            UUID slotUuid,
            ItemStack stack
    ) {
        if (!isFunctionalCharmSlot(slotContext)
                || !(slotContext.entity() instanceof Player)) {
            return ImmutableMultimap.of();
        }

        return ImmutableMultimap.of(
                ModAttributes.SCYTHE_DAMAGE.get(),
                StableAttributeModifiers.create(
                        SCYTHE_DAMAGE_MODIFIER_ID,
                        SCYTHE_DAMAGE_MODIFIER_KEY,
                        SCYTHE_DAMAGE_BONUS,
                        AttributeModifier.Operation.ADDITION
                )
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> list,
            TooltipFlag flag
    ) {
        ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.void"
        );
        ItemLoreHelper.addLocalizedFormattedString(
                list,
                "curios.modifiers.charm",
                ChatFormatting.GOLD
        );
        ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.eternal_career.soul_reaping_skull.scythe_damage",
                ChatFormatting.GOLD,
                "25%"
        );
        ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.eternal_career.soul_reaping_skull.soul_gain",
                ChatFormatting.GOLD,
                "100%"
        );
        ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.eternal_career.soul_reaping_skull.minimum_soul",
                ChatFormatting.GOLD,
                "5"
        );
    }

    @Override
    public List<Component> getAttributesTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }

    private static boolean isFunctionalCharmSlot(SlotContext slotContext) {
        return slotContext != null
                && FoodBookCurio.CHARM_SLOT.equals(slotContext.identifier())
                && !slotContext.cosmetic();
    }
}
