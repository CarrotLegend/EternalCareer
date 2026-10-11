package com.carrot123.eternal_career.soulblessing;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class SoulBlessingItem extends Item {
    private static final ResourceLocation GOETY_SPELL_COOLDOWN =
            new ResourceLocation("goety_revelation", "spell_cooldown");
    private static final ResourceLocation GOETY_SPELL_POWER_MULTIPLIER =
            new ResourceLocation("goety_revelation", "spell_power_multiplier");
    private final SoulBlessingSlotType type;
    private final Supplier<List<SoulBlessingAttribute>> attributes;
    private final int requiredSlot;

    public SoulBlessingItem(Properties properties, SoulBlessingSlotType type,
            List<SoulBlessingAttribute> attributes) {
        this(properties, type, attributes, -1);
    }

    public SoulBlessingItem(Properties properties, SoulBlessingSlotType type,
            List<SoulBlessingAttribute> attributes, int requiredSlot) {
        this(properties, type, fixed(attributes), requiredSlot);
    }

    private static Supplier<List<SoulBlessingAttribute>> fixed(
            List<SoulBlessingAttribute> attributes) {
        List<SoulBlessingAttribute> copy = List.copyOf(attributes);
        return () -> copy;
    }

    public SoulBlessingItem(Properties properties, SoulBlessingSlotType type,
            Supplier<List<SoulBlessingAttribute>> attributes, int requiredSlot) {
        super(properties);
        this.type = type;
        this.attributes = attributes;
        this.requiredSlot = requiredSlot;
    }

    public SoulBlessingSlotType getSoulBlessingType() { return type; }

    public boolean canEquipInSlot(int slot) {
        return slot >= 0 && slot < SoulBlessingSlots.COUNT
                && SoulBlessingSlots.type(slot) == type
                && (requiredSlot < 0 || requiredSlot == slot);
    }

    public List<SoulBlessingAttribute> getSoulBlessingAttributes(ItemStack stack) {
        return attributes.get();
    }

    public void onEquipped(Player player, int slot, ItemStack stack) {}

    public void onUnequipped(Player player, int slot, ItemStack stack) {}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("soul_blessing.eternal_career.title")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("soul_blessing.eternal_career.tooltip.type",
                Component.translatable(type.translationKey())).withStyle(ChatFormatting.GRAY));
        for (SoulBlessingAttribute entry : getSoulBlessingAttributes(stack)) {
            double amount = entry.amount();
            ResourceLocation attributeId = ForgeRegistries.ATTRIBUTES.getKey(entry.attribute());
            boolean cooldownReduction = GOETY_SPELL_COOLDOWN.equals(attributeId);
            boolean addition = entry.operation()
                    == net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;
            String key = addition && !cooldownReduction
                    ? "soul_blessing.eternal_career.tooltip.add"
                    : "soul_blessing.eternal_career.tooltip.percent";
            String value = addition && !cooldownReduction
                    ? String.format(java.util.Locale.ROOT,
                            GOETY_SPELL_POWER_MULTIPLIER.equals(attributeId) ? "%+.0f" : "%+.2f", amount)
                    : String.format(java.util.Locale.ROOT, "%+.0f%%", amount * 100.0D);
            tooltip.add(Component.translatable(key, value,
                    Component.translatable(entry.attribute().getDescriptionId()))
                    .withStyle(ChatFormatting.BLUE));
        }
    }
}
