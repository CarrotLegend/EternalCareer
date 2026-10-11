package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.puffish.PuffishAttributesHelper;
import com.carrot123.eternal_career.item.DiscouragedCurseItem;
import com.carrot123.eternal_career.item.HeavyCurseItem;
import com.carrot123.eternal_career.item.HungerCurseItem;
import com.carrot123.eternal_career.item.PandoraBoxItem;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ExtendedCurseEffects {

    private static final ResourceLocation SPELL_POWER = new ResourceLocation("irons_spellbooks", "spell_power");
    private static final ResourceLocation COOLDOWN_REDUCTION = new ResourceLocation("irons_spellbooks", "cooldown_reduction");
    private static final ResourceLocation CAST_TIME_REDUCTION = new ResourceLocation("irons_spellbooks", "cast_time_reduction");
    private static final ResourceLocation MAX_MANA = new ResourceLocation("irons_spellbooks", "max_mana");

    private static final UUID HEAVY_MOVEMENT_SPEED = id("heavy/movement_speed");
    private static final UUID HEAVY_SPELL_POWER = id("heavy/spell_power");
    private static final UUID HEAVY_COOLDOWN = id("heavy/cooldown");
    private static final UUID DISCOURAGED_COOLDOWN = id("discouraged/cooldown");
    private static final UUID DISCOURAGED_CAST_TIME = id("discouraged/cast_time");
    private static final UUID DISCOURAGED_MAX_MANA = id("discouraged/max_mana");
    private static final UUID HUNGER_STAMINA = id("hunger/stamina");
    private static final UUID HUNGER_SPELL_POWER = id("hunger/spell_power");

    private static final String CURSE_HUNGER_MARK = "EternalCareerCurseHungerApplied";

    private ExtendedCurseEffects() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        syncHeavy(event.player);
        syncDiscouraged(event.player);
        syncHunger(event.player);
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(ModItems.HUNGER_CURSE.get()) && !HungerCurseItem.isReversed(stack)) {
            event.getToolTip().add(
                    Component.translatable("tooltip.eternal_career.hunger_curse.hunger_five").withStyle(ChatFormatting.RED)
            );
        }
    }

    private static void syncHeavy(Player player) {
        ItemStack stack = find(player, ModItems.HEAVY_CURSE.get());
        if (stack.isEmpty()) {
            remove(player, Attributes.MOVEMENT_SPEED, HEAVY_MOVEMENT_SPEED);
            remove(player, SPELL_POWER, HEAVY_SPELL_POWER);
            remove(player, COOLDOWN_REDUCTION, HEAVY_COOLDOWN);
            return;
        }
        if (!HeavyCurseItem.isReversed(stack)) {
            apply(player, Attributes.MOVEMENT_SPEED, HEAVY_MOVEMENT_SPEED, -0.20D,
                    AttributeModifier.Operation.MULTIPLY_BASE);
            remove(player, SPELL_POWER, HEAVY_SPELL_POWER);
            remove(player, COOLDOWN_REDUCTION, HEAVY_COOLDOWN);
            return;
        }
        apply(player, Attributes.MOVEMENT_SPEED, HEAVY_MOVEMENT_SPEED, 0.10D,
                AttributeModifier.Operation.MULTIPLY_BASE);
        double movementSpeed = Math.max(0.0D, player.getAttributeValue(Attributes.MOVEMENT_SPEED));
        int stages = (int) Math.floor(movementSpeed / 0.02D + 1.0E-9D);
        double spellPower = stages * 0.10D;
        double cooldown = stages * 0.02D;
        if (spellPower > 0.0D) {
            apply(player, SPELL_POWER, HEAVY_SPELL_POWER, spellPower,
                    AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            remove(player, SPELL_POWER, HEAVY_SPELL_POWER);
        }
        if (cooldown > 0.0D) {
            apply(player, COOLDOWN_REDUCTION, HEAVY_COOLDOWN, cooldown,
                    AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            remove(player, COOLDOWN_REDUCTION, HEAVY_COOLDOWN);
        }
    }

    private static void syncDiscouraged(Player player) {
        ItemStack stack = find(player, ModItems.DISCOURAGED_CURSE.get());
        if (stack.isEmpty() || !DiscouragedCurseItem.isReversed(stack)) {
            remove(player, COOLDOWN_REDUCTION, DISCOURAGED_COOLDOWN);
            remove(player, CAST_TIME_REDUCTION, DISCOURAGED_CAST_TIME);
            remove(player, MAX_MANA, DISCOURAGED_MAX_MANA);
            return;
        }
        apply(player, COOLDOWN_REDUCTION, DISCOURAGED_COOLDOWN, 0.10D,
                AttributeModifier.Operation.MULTIPLY_BASE);
        apply(player, CAST_TIME_REDUCTION, DISCOURAGED_CAST_TIME, 0.10D,
                AttributeModifier.Operation.MULTIPLY_BASE);
        apply(player, MAX_MANA, DISCOURAGED_MAX_MANA, 100.0D,
                AttributeModifier.Operation.ADDITION);
    }

    private static void syncHunger(Player player) {
        ItemStack stack = find(player, ModItems.HUNGER_CURSE.get());
        boolean normal = !stack.isEmpty() && !HungerCurseItem.isReversed(stack);
        if (player.tickCount % 10 == 0) {
            syncHungerEffect(player, normal);
        }
        if (stack.isEmpty()) {
            remove(player, PuffishAttributesHelper.STAMINA, HUNGER_STAMINA);
            remove(player, SPELL_POWER, HUNGER_SPELL_POWER);
            return;
        }
        if (normal) {
            apply(player, PuffishAttributesHelper.STAMINA, HUNGER_STAMINA, -0.50D,
                    AttributeModifier.Operation.MULTIPLY_BASE);
            remove(player, SPELL_POWER, HUNGER_SPELL_POWER);
            return;
        }
        apply(player, PuffishAttributesHelper.STAMINA, HUNGER_STAMINA, 0.25D,
                AttributeModifier.Operation.MULTIPLY_BASE);
        if (player.getFoodData().getFoodLevel() < 20) {
            apply(player, SPELL_POWER, HUNGER_SPELL_POWER, 1.20D,
                    AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            remove(player, SPELL_POWER, HUNGER_SPELL_POWER);
        }
    }

    private static void syncHungerEffect(Player player, boolean active) {
        MobEffectInstance current = player.getEffect(MobEffects.HUNGER);
        if (!active) {
            if (player.getPersistentData().getBoolean(CURSE_HUNGER_MARK)) {
                if (current != null && current.getAmplifier() == 4 && current.getDuration() <= 80) {
                    player.removeEffect(MobEffects.HUNGER);
                }
                player.getPersistentData().remove(CURSE_HUNGER_MARK);
            }
            return;
        }
        if (current == null
                || current.getAmplifier() < 4
                || current.getAmplifier() == 4 && current.getDuration() < 35) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 80, 4, false, false, true));
            player.getPersistentData().putBoolean(CURSE_HUNGER_MARK, true);
        }
    }

    private static ItemStack find(Player player, Item item) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findCurios(item)
                        .stream()
                        .filter(result -> !result.slotContext().cosmetic())
                        .filter(result -> PandoraBoxItem.CURSE_SPIRIT_SLOT.equals(result.slotContext().identifier()))
                        .map(result -> result.stack())
                        .findFirst())
                .orElse(ItemStack.EMPTY);
    }

    private static void apply(Player player, ResourceLocation attributeId, UUID uuid,
                              double amount, AttributeModifier.Operation operation) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute != null) {
            apply(player, attribute, uuid, amount, operation);
        }
    }

    private static void apply(Player player, Attribute attribute, UUID uuid,
                              double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier current = instance.getModifier(uuid);
        if (current != null && current.getOperation() == operation
                && Double.compare(current.getAmount(), amount) == 0) {
            return;
        }
        if (current != null) {
            instance.removeModifier(uuid);
        }
        instance.addTransientModifier(new AttributeModifier(
                uuid, EternalCareer.MOD_ID + ":curse", amount, operation
        ));
    }

    private static void remove(Player player, ResourceLocation attributeId, UUID uuid) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute != null) {
            remove(player, attribute, uuid);
        }
    }

    private static void remove(Player player, Attribute attribute, UUID uuid) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null && instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }

    private static UUID id(String path) {
        return StableAttributeModifiers.id(EternalCareer.MOD_ID + ":curse/" + path);
    }
}
