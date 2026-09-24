package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.DarkBootsItem;
import com.carrot123.eternal_career.item.FrostCharmItem;
import com.carrot123.eternal_career.lich.LichUtils;
import com.carrot123.eternal_career.registry.ModItems;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class LichCurioDynamicAttributeEvents {
    private static final ResourceLocation FOCUS_DAMAGE =
            new ResourceLocation(
                    "until_eternity",
                    "focus_damage"
            );

    private static final ResourceLocation IRONS_SPELL_POWER =
            new ResourceLocation(
                    "irons_spellbooks",
                    "spell_power"
            );

    private static final UUID FROST_CHARM_FOCUS_DAMAGE =
            modifierId(
                    "frost_charm/focus_damage"
            );

    private static final UUID DARK_BOOTS_FOCUS_DAMAGE =
            modifierId(
                    "dark_boots/focus_damage"
            );

    private static final UUID DARK_BOOTS_SPELL_POWER =
            modifierId(
                    "dark_boots/spell_power"
            );

    private LichCurioDynamicAttributeEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END
                || event.player.level().isClientSide) {
            return;
        }

        syncFrostCharm(event.player);
        syncDarkBoots(event.player);
    }

    private static void syncFrostCharm(
            Player player
    ) {
        Attribute focusDamageAttribute =
                ForgeRegistries.ATTRIBUTES.getValue(
                        FOCUS_DAMAGE
                );

        if (focusDamageAttribute == null) {
            return;
        }

        AttributeInstance focusDamage =
                player.getAttribute(
                        focusDamageAttribute
                );

        if (focusDamage == null) {
            return;
        }

        boolean equipped =
                isEquipped(
                        player,
                        ModItems.FROST_CHARM.get(),
                        FrostCharmItem.SLOT
                );

        if (!equipped) {
            remove(
                    focusDamage,
                    FROST_CHARM_FOCUS_DAMAGE
            );
            return;
        }

        double amount =
                LichUtils.isLich(player)
                        ? 0.50D
                        : 0.25D;

        apply(
                focusDamage,
                FROST_CHARM_FOCUS_DAMAGE,
                EternalCareer.MOD_ID
                        + ":frost_charm/focus_damage",
                amount,
                AttributeModifier.Operation.MULTIPLY_BASE
        );
    }

    private static void syncDarkBoots(
            Player player
    ) {
        Attribute focusDamageAttribute =
                ForgeRegistries.ATTRIBUTES.getValue(
                        FOCUS_DAMAGE
                );

        AttributeInstance focusDamage =
                focusDamageAttribute == null
                        ? null
                        : player.getAttribute(
                                focusDamageAttribute
                        );

        Attribute spellPowerAttribute =
                ForgeRegistries.ATTRIBUTES.getValue(
                        IRONS_SPELL_POWER
                );

        AttributeInstance spellPower =
                spellPowerAttribute == null
                        ? null
                        : player.getAttribute(
                                spellPowerAttribute
                        );

        boolean equipped =
                isEquipped(
                        player,
                        ModItems.DARK_BOOTS.get(),
                        DarkBootsItem.SLOT
                );

        if (!equipped) {
            if (focusDamage != null) {
                remove(
                        focusDamage,
                        DARK_BOOTS_FOCUS_DAMAGE
                );
            }

            if (spellPower != null) {
                remove(
                        spellPower,
                        DARK_BOOTS_SPELL_POWER
                );
            }

            return;
        }

        double movementSpeed =
                Math.max(
                        0.0D,
                        player.getAttributeValue(
                                Attributes.MOVEMENT_SPEED
                        )
                );

        int stages =
                (int) Math.floor(
                        movementSpeed
                                / 0.05D
                                + 1.0E-9D
                );

        double amount =
                stages * 0.10D;

        if (amount <= 0.0D) {
            if (focusDamage != null) {
                remove(
                        focusDamage,
                        DARK_BOOTS_FOCUS_DAMAGE
                );
            }

            if (spellPower != null) {
                remove(
                        spellPower,
                        DARK_BOOTS_SPELL_POWER
                );
            }

            return;
        }

        if (focusDamage != null) {
            apply(
                    focusDamage,
                    DARK_BOOTS_FOCUS_DAMAGE,
                    EternalCareer.MOD_ID
                            + ":dark_boots/focus_damage",
                    amount,
                    AttributeModifier.Operation.MULTIPLY_BASE
            );
        }

        if (spellPower != null) {
            apply(
                    spellPower,
                    DARK_BOOTS_SPELL_POWER,
                    EternalCareer.MOD_ID
                            + ":dark_boots/spell_power",
                    amount,
                    AttributeModifier.Operation.MULTIPLY_BASE
            );
        }
    }

    private static boolean isEquipped(
            Player player,
            Item item,
            String slot
    ) {
        return CuriosApi
                .getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler
                                .findCurios(item)
                                .stream()
                                .anyMatch(result ->
                                        slot.equals(
                                                result
                                                        .slotContext()
                                                        .identifier()
                                        )
                                                && !result
                                                .slotContext()
                                                .cosmetic()
                                )
                )
                .orElse(false);
    }

    private static void apply(
            AttributeInstance instance,
            UUID uuid,
            String name,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeModifier current =
                instance.getModifier(uuid);

        if (current != null
                && current.getOperation() == operation
                && Double.compare(
                        current.getAmount(),
                        amount
                ) == 0) {
            return;
        }

        if (current != null) {
            instance.removeModifier(uuid);
        }

        instance.addTransientModifier(
                new AttributeModifier(
                        uuid,
                        name,
                        amount,
                        operation
                )
        );
    }

    private static void remove(
            AttributeInstance instance,
            UUID uuid
    ) {
        if (instance.getModifier(uuid) != null) {
            instance.removeModifier(uuid);
        }
    }

    private static UUID modifierId(
            String path
    ) {
        return UUID.nameUUIDFromBytes(
                (
                        EternalCareer.MOD_ID
                                + ":"
                                + path
                ).getBytes(StandardCharsets.UTF_8)
        );
    }
}