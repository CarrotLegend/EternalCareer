package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.puffish.PuffishAttributesHelper;
import com.carrot123.eternal_career.item.IgnoranceCurseItem;
import com.carrot123.eternal_career.item.PandoraBoxItem;
import com.carrot123.eternal_career.item.PowerlessCurseItem;
import com.carrot123.eternal_career.item.VulnerabilityCurseItem;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AttributeCurseEffects {

    private static final ResourceLocation SPELL_POWER = new ResourceLocation("irons_spellbooks", "spell_power");
    private static final ResourceLocation CAST_TIME_REDUCTION = new ResourceLocation("irons_spellbooks", "cast_time_reduction");
    private static final ResourceLocation COOLDOWN_REDUCTION = new ResourceLocation("irons_spellbooks", "cooldown_reduction");

    private static final UUID POWERLESS_ALL_DAMAGE = id("powerless/all_damage");
    private static final UUID POWERLESS_SPELL_POWER = id("powerless/spell_power");
    private static final UUID POWERLESS_ATTACK_SPEED = id("powerless/attack_speed");

    private static final UUID VULNERABILITY_ARMOR = id("vulnerability/armor");
    private static final UUID VULNERABILITY_HEALTH = id("vulnerability/health");
    private static final UUID VULNERABILITY_RESISTANCE = id("vulnerability/resistance");

    private static final UUID IGNORANCE_SPELL_POWER = id("ignorance/spell_power");
    private static final UUID IGNORANCE_CAST_TIME = id("ignorance/cast_time");
    private static final UUID IGNORANCE_COOLDOWN = id("ignorance/cooldown");

    private AttributeCurseEffects() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        syncPowerless(event.player);
        syncVulnerability(event.player);
        syncIgnorance(event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        float damage = event.getAmount();
        if (!Float.isFinite(damage) || damage <= 0.0F || damage >= 5.0F) {
            return;
        }
        ItemStack curse = find(player, ModItems.VULNERABILITY_CURSE.get());
        if (!curse.isEmpty() && !VulnerabilityCurseItem.isReversed(curse)) {
            event.setAmount(5.0F);
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(ModItems.POWERLESS_CURSE.get()) && !PowerlessCurseItem.isReversed(stack)) {
            event.getToolTip().add(
                    Component.translatable("tooltip.eternal_career.powerless_curse.attack_speed").withStyle(ChatFormatting.RED)
            );
        } else if (stack.is(ModItems.VULNERABILITY_CURSE.get())
                && !VulnerabilityCurseItem.isReversed(stack)) {
            event.getToolTip().add(
                    Component.translatable("tooltip.eternal_career.vulnerability_curse.minimum_damage").withStyle(ChatFormatting.RED)
            );
        }
    }

    private static void syncPowerless(Player player) {
        ItemStack stack = find(player, ModItems.POWERLESS_CURSE.get());
        boolean normal = !stack.isEmpty() && !PowerlessCurseItem.isReversed(stack);
        syncAttackSpeed(player, normal);

        if (stack.isEmpty()) {
            remove(player, com.carrot123.until_eternity.registry.ModAttributes.ALL_DAMAGE.get(), POWERLESS_ALL_DAMAGE);
            remove(player, SPELL_POWER, POWERLESS_SPELL_POWER);
            return;
        }

        if (PowerlessCurseItem.isReversed(stack)) {
            apply(player, com.carrot123.until_eternity.registry.ModAttributes.ALL_DAMAGE.get(), POWERLESS_ALL_DAMAGE, 0.20D);
            apply(player, SPELL_POWER, POWERLESS_SPELL_POWER, 0.80D);
            return;
        }

        apply(player, com.carrot123.until_eternity.registry.ModAttributes.ALL_DAMAGE.get(), POWERLESS_ALL_DAMAGE, -0.50D);
        remove(player, SPELL_POWER, POWERLESS_SPELL_POWER);
    }

    private static void syncAttackSpeed(Player player, boolean enabled) {
        AttributeInstance instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance == null) {
            return;
        }
        AttributeModifier current = instance.getModifier(POWERLESS_ATTACK_SPEED);
        if (!enabled) {
            if (current != null) {
                instance.removeModifier(POWERLESS_ATTACK_SPEED);
            }
            return;
        }
        if (current != null
                && current.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL
                && Double.compare(current.getAmount(), -0.25D) == 0) {
            return;
        }
        if (current != null) {
            instance.removeModifier(POWERLESS_ATTACK_SPEED);
        }
        instance.addTransientModifier(new AttributeModifier(
                POWERLESS_ATTACK_SPEED,
                EternalCareer.MOD_ID + ":curse/powerless/attack_speed",
                -0.25D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }

    private static void syncVulnerability(Player player) {
        ItemStack stack = find(player, ModItems.VULNERABILITY_CURSE.get());
        if (stack.isEmpty()) {
            remove(player, Attributes.ARMOR, VULNERABILITY_ARMOR);
            remove(player, Attributes.MAX_HEALTH, VULNERABILITY_HEALTH);
            remove(player, PuffishAttributesHelper.RESISTANCE, VULNERABILITY_RESISTANCE);
            clampHealth(player);
            return;
        }
        if (VulnerabilityCurseItem.isReversed(stack)) {
            apply(player, Attributes.ARMOR, VULNERABILITY_ARMOR, 0.20D);
            apply(player, Attributes.MAX_HEALTH, VULNERABILITY_HEALTH, 0.20D);
            apply(player, PuffishAttributesHelper.RESISTANCE, VULNERABILITY_RESISTANCE, 0.10D);
            clampHealth(player);
            return;
        }
        apply(player, Attributes.ARMOR, VULNERABILITY_ARMOR, -0.99D);
        remove(player, Attributes.MAX_HEALTH, VULNERABILITY_HEALTH);
        remove(player, PuffishAttributesHelper.RESISTANCE, VULNERABILITY_RESISTANCE);
        clampHealth(player);
    }

    private static void syncIgnorance(Player player) {
        ItemStack stack = find(player, ModItems.IGNORANCE_CURSE.get());
        if (stack.isEmpty()) {
            remove(player, SPELL_POWER, IGNORANCE_SPELL_POWER);
            remove(player, CAST_TIME_REDUCTION, IGNORANCE_CAST_TIME);
            remove(player, COOLDOWN_REDUCTION, IGNORANCE_COOLDOWN);
            return;
        }
        if (IgnoranceCurseItem.isReversed(stack)) {
            apply(player, SPELL_POWER, IGNORANCE_SPELL_POWER, 0.40D);
            apply(player, CAST_TIME_REDUCTION, IGNORANCE_CAST_TIME, 0.20D);
            apply(player, COOLDOWN_REDUCTION, IGNORANCE_COOLDOWN, 0.20D);
            return;
        }
        apply(player, SPELL_POWER, IGNORANCE_SPELL_POWER, -0.10D);
        apply(player, CAST_TIME_REDUCTION, IGNORANCE_CAST_TIME, -0.10D);
        apply(player, COOLDOWN_REDUCTION, IGNORANCE_COOLDOWN, -0.10D);
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

    private static void apply(Player player, ResourceLocation attributeId, UUID uuid, double amount) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute != null) {
            apply(player, attribute, uuid, amount);
        }
    }

    private static void apply(Player player, Attribute attribute, UUID uuid, double amount) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier current = instance.getModifier(uuid);
        if (current != null
                && current.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE
                && Double.compare(current.getAmount(), amount) == 0) {
            return;
        }
        if (current != null) {
            instance.removeModifier(uuid);
        }
        instance.addTransientModifier(new AttributeModifier(
                uuid,
                EternalCareer.MOD_ID + ":curse",
                amount,
                AttributeModifier.Operation.MULTIPLY_BASE
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

    private static void clampHealth(Player player) {
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static UUID id(String path) {
        return StableAttributeModifiers.id(EternalCareer.MOD_ID + ":curse/" + path);
    }
}
