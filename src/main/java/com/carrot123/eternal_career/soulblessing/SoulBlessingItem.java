package com.carrot123.eternal_career.soulblessing;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SoulBlessingItem extends Item {
    private final SoulBlessingSlotType type;
    private final List<SoulBlessingAttribute> attributes;

    public SoulBlessingItem(Properties properties, SoulBlessingSlotType type,
            List<SoulBlessingAttribute> attributes) {
        super(properties);
        this.type = type;
        this.attributes = List.copyOf(attributes);
    }

    public SoulBlessingSlotType getSoulBlessingType() { return type; }

    public List<SoulBlessingAttribute> getSoulBlessingAttributes(ItemStack stack) {
        return attributes;
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
            String key = entry.operation() == net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
                    ? "soul_blessing.eternal_career.tooltip.add"
                    : "soul_blessing.eternal_career.tooltip.percent";
            String value = entry.operation() == net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION
                    ? String.format(java.util.Locale.ROOT, "%+.2f", amount)
                    : String.format(java.util.Locale.ROOT, "%+.0f%%", amount * 100.0D);
            tooltip.add(Component.translatable(key, value,
                    Component.translatable(entry.attribute().getDescriptionId()))
                    .withStyle(ChatFormatting.BLUE));
        }
    }
}
