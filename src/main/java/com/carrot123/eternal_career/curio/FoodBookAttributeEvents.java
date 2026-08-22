package com.carrot123.eternal_career.curio;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.solcarrot.SolCarrotHelper;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.until_eternity.compat.PuffishAttributesCompat;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FoodBookAttributeEvents {
    public static final double MAX_HEALTH_BONUS = 10.0D;
    public static final double KITCHENWARE_BONUS_PER_FOOD = 0.001D;
    public static final double ATTACK_SPEED_BONUS_PER_FOOD = 0.0001D;
    public static final double RESISTANCE_BONUS_PER_FOOD = 0.0005D;

    public static final UUID MAX_HEALTH_MODIFIER_ID = stableId("max_health");
    public static final UUID KITCHENWARE_MODIFIER_ID = stableId("kitchenware_damage");
    public static final UUID ATTACK_SPEED_MODIFIER_ID = stableId("attack_speed");
    public static final UUID RESISTANCE_MODIFIER_ID = stableId("normal_resistance");

    private FoodBookAttributeEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && !event.player.level().isClientSide
                && event.player.tickCount % 20 == 0) {
            reconcile(event.player);
        }
    }

    @SubscribeEvent
    public static void onCurioEquip(CurioEquipEvent event) {
        if (!event.getEntity().level().isClientSide
                && event.getEntity() instanceof Player player
                && isFoodBook(event.getStack())) {
            apply(player, SolCarrotHelper.getUniqueFoodsEaten(player));
        }
    }

    @SubscribeEvent
    public static void onCurioUnequip(CurioUnequipEvent event) {
        if (!event.getEntity().level().isClientSide
                && event.getEntity() instanceof Player player
                && isFoodBook(event.getStack())) {
            removeAll(player);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            reconcile(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        reconcile(event.getEntity());
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        reconcile(event.getEntity());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        removeAll(event.getOriginal());
        reconcile(event.getEntity());
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            removeAll(player);
        }
    }

    public static void reconcile(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        if (!CurioEquipmentHelper.hasFoodBook(player)) {
            removeAll(player);
            return;
        }
        apply(player, SolCarrotHelper.getUniqueFoodsEaten(player));
    }

    public static void apply(Player player, int uniqueFoodsEaten) {
        int foodCount = Math.max(0, uniqueFoodsEaten);
        replaceModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_MODIFIER_ID,
                "Food Book max health", MAX_HEALTH_BONUS,
                AttributeModifier.Operation.ADDITION);
        replaceModifier(player, ModAttributes.KITCHENWARE_DAMAGE.get(),
                KITCHENWARE_MODIFIER_ID, "Food Book kitchenware damage",
                foodCount * KITCHENWARE_BONUS_PER_FOOD,
                AttributeModifier.Operation.MULTIPLY_BASE);
        replaceModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID,
                "Food Book attack speed", foodCount * ATTACK_SPEED_BONUS_PER_FOOD,
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        Attribute resistance = PuffishAttributesCompat.resolve(
                PuffishAttributesCompat.RESISTANCE);
        if (resistance != null) {
            replaceModifier(player, resistance, RESISTANCE_MODIFIER_ID,
                    "Food Book normal resistance", foodCount * RESISTANCE_BONUS_PER_FOOD,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
    }

    public static void removeAll(Player player) {
        removeModifier(player, Attributes.MAX_HEALTH, MAX_HEALTH_MODIFIER_ID);
        removeModifier(player, ModAttributes.KITCHENWARE_DAMAGE.get(),
                KITCHENWARE_MODIFIER_ID);
        removeModifier(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID);
        Attribute resistance = PuffishAttributesCompat.resolve(
                PuffishAttributesCompat.RESISTANCE);
        if (resistance != null) {
            removeModifier(player, resistance, RESISTANCE_MODIFIER_ID);
        }
    }

    private static boolean isFoodBook(ItemStack stack) {
        return CurioEquipmentHelper.FOOD_BOOK_ID.equals(
                net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()));
    }

    private static void replaceModifier(Player player, Attribute attribute, UUID id,
                                        String name, double amount,
                                        AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier current = instance.getModifier(id);
        if (amount == 0.0D) {
            if (current != null) {
                instance.removeModifier(id);
            }
            return;
        }
        if (current != null
                && Double.compare(current.getAmount(), amount) == 0
                && current.getOperation() == operation) {
            return;
        }
        if (current != null) {
            instance.removeModifier(id);
        }
        instance.addTransientModifier(new AttributeModifier(id, name, amount, operation));
    }

    private static void removeModifier(Player player, Attribute attribute, UUID id) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null && instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }

    private static UUID stableId(String attributePath) {
        return UUID.nameUUIDFromBytes(
                (EternalCareer.MOD_ID + ":food_book/" + attributePath)
                        .getBytes(StandardCharsets.UTF_8));
    }
}
