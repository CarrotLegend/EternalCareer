package com.carrot123.eternal_career.curio;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.CurioTooltipHelper;
import com.cazsius.solcarrot.tracking.FoodList;

import java.math.BigDecimal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class FoodBookTooltipEvents {

    private static final ResourceLocation FOOD_BOOK_ID =
            FoodBookCurio.FOOD_BOOK_ID;

    private static final double KITCHENWARE_DAMAGE_PER_FOOD = 0.1D;
    private static final double ATTACK_SPEED_PER_FOOD = 0.01D;
    private static final double DAMAGE_RESISTANCE_PER_FOOD = 0.05D;

    private FoodBookTooltipEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation itemId =
                ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());

        if (!FOOD_BOOK_ID.equals(itemId)) {
            return;
        }

        CurioTooltipHelper.addBlank(event.getToolTip());

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.max_health",
                "10"
        );

        CurioTooltipHelper.addBlank(event.getToolTip());

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.unique_food"
        );

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.kitchenware_damage",
                "0.1%"
        );

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.attack_speed",
                "0.01%"
        );

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.damage_resistance",
                "0.05%"
        );


        if (Screen.hasShiftDown()) {
            addCurrentBonuses(event);
        } else {

            CurioTooltipHelper.addLocalizedString(
                    event.getToolTip(),
                    "tooltip.eternal_career.food_book.hold_shift"
            );
        }
    }

    private static void addCurrentBonuses(ItemTooltipEvent event) {
        Player player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        int eatenFoodCount;

        try {
            eatenFoodCount = FoodList.get(player).getEatenFoodCount();
        } catch (Exception exception) {
            return;
        }

        double kitchenwareDamage =
                eatenFoodCount * KITCHENWARE_DAMAGE_PER_FOOD;

        double attackSpeed =
                eatenFoodCount * ATTACK_SPEED_PER_FOOD;

        double damageResistance =
                eatenFoodCount * DAMAGE_RESISTANCE_PER_FOOD;

        CurioTooltipHelper.addBlank(event.getToolTip());

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.current_bonus"
        );

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.current_kitchenware_damage",
                formatPercent(kitchenwareDamage)
        );

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.current_attack_speed",
                formatPercent(attackSpeed)
        );

        CurioTooltipHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.food_book.current_damage_resistance",
                formatPercent(damageResistance)
        );
    }

    private static String formatPercent(double value) {
        return BigDecimal.valueOf(value)
                .stripTrailingZeros()
                .toPlainString() + "%";
    }
}