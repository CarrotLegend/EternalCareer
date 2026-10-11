package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class SoulCoreItem extends Item implements ICurioItem {

    private static final ResourceLocation RANGED_DAMAGE =
            new ResourceLocation("puffish_attributes", "ranged_damage");

    private final boolean bloodHunter;

    public SoulCoreItem(Properties properties, boolean bloodHunter) {
        super(properties.stacksTo(1));
        this.bloodHunter = bloodHunter;
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null
                && !context.cosmetic()
                && "ring".equals(context.identifier())
                && context.entity() instanceof Player player
                && SoulFaction.canJoin(player);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return canEquip(context, stack);
    }

    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        return context != null
                && context.entity() instanceof Player player
                && player.getAbilities().instabuild;
    }

    @Override
    public ICurio.DropRule getDropRule(
            SlotContext context,
            DamageSource source,
            int lootingLevel,
            boolean recentlyHit,
            ItemStack stack
    ) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext context,
            UUID slotUuid,
            ItemStack stack
    ) {
        if (!bloodHunter || context == null || context.cosmetic()
                || !"ring".equals(context.identifier())) {
            return ImmutableMultimap.of();
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> result =
                ImmutableMultimap.builder();
        Attribute ranged = ForgeRegistries.ATTRIBUTES.getValue(RANGED_DAMAGE);
        if (ranged != null) {
            result.put(ranged, modifier(slotUuid, "ranged_damage", 0.20D));
        }
        result.put(ModAttributes.NON_RANGED_DAMAGE.get(),
                modifier(slotUuid, "non_ranged_damage", -0.80D));
        return result.build();
    }

    private static AttributeModifier modifier(UUID slotUuid, String id, double value) {
        return StableAttributeModifiers.create(
                EternalCareer.MOD_ID + ":blood_hunter_core/" + slotUuid + "/" + id,
                value,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltip, ItemStack stack) {
        if (bloodHunter || stack.is(SoulCoreRegistry.SOUL_CORE.get())) {
            tooltip.clear();
        }
        return tooltip;
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltip, ItemStack stack) {
        if (bloodHunter || stack.is(SoulCoreRegistry.SOUL_CORE.get())) {
            tooltip.clear();
        }
        return tooltip;
    }
}
