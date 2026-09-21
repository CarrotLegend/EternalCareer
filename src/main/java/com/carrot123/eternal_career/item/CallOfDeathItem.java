package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.FoodBookCurio;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
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
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class CallOfDeathItem
        extends Item
        implements ICurioItem {

    public static final double SCYTHE_DAMAGE_BONUS = 0.20D;
    public static final double NON_SCYTHE_DAMAGE_PENALTY = -0.80D;

    public static final UUID SCYTHE_DAMAGE_MODIFIER_ID =
            stableModifierId("scythe_damage");

    public static final UUID NON_SCYTHE_DAMAGE_MODIFIER_ID =
            stableModifierId("non_scythe_damage");

    public CallOfDeathItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(
            SlotContext slotContext,
            ItemStack stack
    ) {
        return isFunctionalCharmSlot(slotContext);
    }

    @Override
    public boolean canUnequip(
            SlotContext slotContext,
            ItemStack stack
    ) {
        if (slotContext.entity() instanceof Player player
                && SuperpositionHandler.canUnequipBoundRelics(player)) {
            return ICurioItem.super.canUnequip(
                    slotContext,
                    stack
            );
        }

        return false;
    }

    @Override
    public DropRule getDropRule(
            SlotContext slotContext,
            DamageSource source,
            int lootingLevel,
            boolean recentlyHit,
            ItemStack stack
    ) {
        return DropRule.ALWAYS_KEEP;
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
                new AttributeModifier(
                        SCYTHE_DAMAGE_MODIFIER_ID,
                        EternalCareer.MOD_ID
                                + ":call_of_death/scythe_damage",
                        SCYTHE_DAMAGE_BONUS,
                        AttributeModifier.Operation.ADDITION
                ),
                ModAttributes.NON_SCYTHE_DAMAGE.get(),
                new AttributeModifier(
                        NON_SCYTHE_DAMAGE_MODIFIER_ID,
                        EternalCareer.MOD_ID
                                + ":call_of_death/non_scythe_damage",
                        NON_SCYTHE_DAMAGE_PENALTY,
                        AttributeModifier.Operation.MULTIPLY_BASE
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
                "tooltip.eternal_career.call_of_death.scythe_damage",
                ChatFormatting.GOLD,
                "20%"
        );

        ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.eternal_career.call_of_death.non_scythe_damage",
                ChatFormatting.GOLD,
                "80%"
        );

        ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.enigmaticlegacy.void"
        );

        ItemLoreHelper.addLocalizedString(
                list,
                "tooltip.eternal_career.bound_curio"
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

    private static boolean isFunctionalCharmSlot(
            SlotContext slotContext
    ) {
        return slotContext != null
                && FoodBookCurio.CHARM_SLOT.equals(
                        slotContext.identifier()
                )
                && !slotContext.cosmetic();
    }

    private static UUID stableModifierId(
            String attributePath
    ) {
        String key =
                EternalCareer.MOD_ID
                        + ":call_of_death/"
                        + attributePath;

        return UUID.nameUUIDFromBytes(
                key.getBytes(StandardCharsets.UTF_8)
        );
    }
}