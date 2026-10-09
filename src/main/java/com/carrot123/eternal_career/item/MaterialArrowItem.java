package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.registry.ModItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public final class MaterialArrowItem extends ArrowItem {
    public static final String MATERIAL_TAG = "EternalCareerArrowMaterial";
    public static final String DAMAGE_APPLIED_TAG = "EternalCareerArrowTypeDamageApplied";

    private final Material material;

    public MaterialArrowItem(Material material, Properties properties) {
        super(properties);
        this.material = material;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        AbstractArrow arrow = super.createArrow(level, stack, shooter);
        arrow.getPersistentData().putString(MATERIAL_TAG, material.id);
        return arrow;
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, bow) > 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines,
            TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);
        lines.add(Component.translatable("tooltip.eternal_career." + material.id)
                .withStyle(ChatFormatting.GRAY));
    }

    public static Material materialOf(AbstractArrow arrow) {
        String id = arrow.getPersistentData().getString(MATERIAL_TAG);
        for (Material material : Material.values()) {
            if (material.id.equals(id)) {
                return material;
            }
        }
        return null;
    }

    public enum Material {
        IRON("iron_arrow", 1.25D),
        DIAMOND("diamond_arrow", 2.0D),
        NETHERITE("netherite_arrow", 4.0D),
        BLOOD_HUNTER("blood_hunter_arrow", 6.0D);

        private final String id;
        private final double multiplier;

        Material(String id, double multiplier) {
            this.id = id;
            this.multiplier = multiplier;
        }

        public double multiplier() {
            return multiplier;
        }

        public ItemStack pickupStack() {
            return switch (this) {
                case IRON -> new ItemStack(ModItems.IRON_ARROW.get());
                case DIAMOND -> new ItemStack(ModItems.DIAMOND_ARROW.get());
                case NETHERITE -> new ItemStack(ModItems.NETHERITE_ARROW.get());
                case BLOOD_HUNTER -> new ItemStack(com.carrot123.eternal_career.bloodbow.BloodBowItems.BLOOD_HUNTER_ARROW.get());
            };
        }
    }
}

