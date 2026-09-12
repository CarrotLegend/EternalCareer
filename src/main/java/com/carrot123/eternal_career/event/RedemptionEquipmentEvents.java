package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.puffish.PuffishAttributesHelper;
import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.eternal_career.curio.FoodBookAttributeEvents;
import com.carrot123.eternal_career.curio.GodsRecognitionCurioEvents;
import com.carrot123.eternal_career.item.CookingMagicHandItem;
import com.carrot123.eternal_career.registry.ModAttributes;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/** Server-side fallback for redemption equipment inserted by commands or nonstandard menus. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RedemptionEquipmentEvents {
    private static final UUID BLESSING_SCROLL_LUCK_ID =
            UUID.fromString("d9062cdd-3824-440e-b494-8e12074f02e9");
    private static final UUID BLESSING_SCROLL_ATTACK_SPEED_ID =
            UUID.fromString("6cc37aff-6609-46d1-a1b0-a4895b6655fd");

    private RedemptionEquipmentEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getSlot().getType() == EquipmentSlot.Type.ARMOR
                && !RedemptionAccessController.isRecheckPending(player)) {
            ejectArmorSlot(player, event.getSlot());
        }
    }

    /** Runs before Curios' default-priority LivingTickEvent handler. */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || RedemptionAccessController.hasRedemptionAccess(player)
                || RedemptionAccessController.isRecheckPending(player)) {
            return;
        }

        removeKnownRedemptionModifiers(player);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ejectArmorSlot(player, slot);
            }
        }
        ejectUnauthorizedCurios(player);
    }

    private static void ejectArmorSlot(ServerPlayer player, EquipmentSlot slot) {
        ItemStack equipped = player.getItemBySlot(slot);
        if (!RedemptionAccessController.deny(player, equipped)) {
            return;
        }

        ItemStack returning = equipped.copy();
        player.getAttributes().removeAttributeModifiers(
                equipped.getAttributeModifiers(slot));
        player.setItemSlot(slot, ItemStack.EMPTY);
        returnToPlayer(player, returning);
    }

    private static void ejectUnauthorizedCurios(ServerPlayer player) {
        CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : inventory.getCurios().entrySet()) {
                ICurioStacksHandler slots = entry.getValue();
                ejectFromHandler(player, slots.getStacks());
                ejectFromHandler(player, slots.getCosmeticStacks());
            }
        });
    }

    private static void ejectFromHandler(ServerPlayer player, IDynamicStackHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack equipped = handler.getStackInSlot(slot);
            if (!RedemptionAccessController.deny(player, equipped)) {
                continue;
            }

            ItemStack returning = equipped.copy();
            handler.setStackInSlot(slot, ItemStack.EMPTY);
            returnToPlayer(player, returning);
        }
    }

    private static void returnToPlayer(Player player, ItemStack stack) {
        player.getInventory().add(stack);
        if (!stack.isEmpty()) {
            player.drop(stack, false);
        }
    }

    private static void removeKnownRedemptionModifiers(Player player) {
        FoodBookAttributeEvents.removeAll(player);
        removeModifier(player, ModAttributes.KITCHENWARE_DAMAGE.get(),
                GodsRecognitionCurioEvents.KITCHENWARE_MODIFIER_ID);
        removeModifier(player, ModAttributes.KITCHENWARE_DAMAGE.get(),
                CookingMagicHandItem.KITCHENWARE_DAMAGE_MODIFIER_ID);
        Attribute lifeSteal = PuffishAttributesHelper.resolve(
                PuffishAttributesHelper.LIFE_STEAL);
        if (lifeSteal != null) {
            removeModifier(player, lifeSteal, CookingMagicHandItem.LIFE_STEAL_MODIFIER_ID);
        }
        removeModifier(player, Attributes.LUCK, BLESSING_SCROLL_LUCK_ID);
        removeModifier(player, Attributes.ATTACK_SPEED, BLESSING_SCROLL_ATTACK_SPEED_ID);
    }

    private static void removeModifier(Player player, Attribute attribute, UUID id) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null && instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }
}
