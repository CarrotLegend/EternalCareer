package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.DiscouragedCurseItem;
import com.carrot123.eternal_career.item.FragileCurseItem;
import com.carrot123.eternal_career.item.HeavyCurseItem;
import com.carrot123.eternal_career.item.HungerCurseItem;
import com.carrot123.eternal_career.item.IgnoranceCurseItem;
import com.carrot123.eternal_career.item.PowerlessCurseItem;
import com.carrot123.eternal_career.item.UndeadCurseItem;
import com.carrot123.eternal_career.item.VulnerabilityCurseItem;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class TwistedHeartEffects {

    private static final ResourceLocation SPELL_POWER =
            new ResourceLocation(
                    "irons_spellbooks",
                    "spell_power"
            );

    private static final UUID SPELL_POWER_UUID =
            StableAttributeModifiers.id(
                    EternalCareer.MOD_ID
                            + ":twisted_heart/spell_power"
            );

    private static final double SPELL_POWER_PER_CURSE =
            0.20D;

    private TwistedHeartEffects() {
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase
                != TickEvent.Phase.END) {
            return;
        }

        Player player =
                event.player;

        if (player.level().isClientSide) {
            return;
        }

        CuriosApi.getCuriosInventory(player)
                .resolve()
                .ifPresent(handler ->
                        sync(
                                player,
                                handler
                        )
                );
    }

    private static void sync(
            Player player,
            ICuriosItemHandler handler
    ) {
        if (!hasTwistedHeart(
                handler
        )) {
            removeModifier(
                    player
            );

            return;
        }

        int reversed =
                countReversedCurses(
                        handler
                );

        if (reversed <= 0) {
            removeModifier(
                    player
            );

            return;
        }

        double amount =
                reversed
                        * SPELL_POWER_PER_CURSE;

        applyModifier(
                player,
                amount
        );
    }

    private static boolean hasTwistedHeart(
            ICuriosItemHandler handler
    ) {
        return handler.findCurios(
                        ModItems.TWISTED_HEART.get()
                )
                .stream()
                .anyMatch(result ->
                        !result.slotContext()
                                .cosmetic()
                );
    }

    private static int countReversedCurses(
            ICuriosItemHandler handler
    ) {
        int count = 0;

        if (hasCurse(
                handler,
                ModItems.UNDEAD_CURSE.get(),
                UndeadCurseItem::isUpgraded
        )) {
            count++;
        }

        if (hasCurse(
                handler,
                ModItems.FRAGILE_CURSE.get(),
                FragileCurseItem::isReversed
        )) {
            count++;
        }

        if (hasCurse(
                handler,
                ModItems.POWERLESS_CURSE.get(),
                PowerlessCurseItem::isReversed
        )) {
            count++;
        }

        if (hasCurse(
                handler,
                ModItems.VULNERABILITY_CURSE.get(),
                VulnerabilityCurseItem::isReversed
        )) {
            count++;
        }

        if (hasCurse(
                handler,
                ModItems.IGNORANCE_CURSE.get(),
                IgnoranceCurseItem::isReversed
        )) {
            count++;
        }

        if (hasCurse(
                handler,
                ModItems.HEAVY_CURSE.get(),
                HeavyCurseItem::isReversed
        )) {
            count++;
        }

        if (hasCurse(
                handler,
                ModItems.DISCOURAGED_CURSE.get(),
                DiscouragedCurseItem::isReversed
        )) {
            count++;
        }

        if (hasCurse(
                handler,
                ModItems.HUNGER_CURSE.get(),
                HungerCurseItem::isReversed
        )) {
            count++;
        }

        return count;
    }

    private static boolean hasCurse(
            ICuriosItemHandler handler,
            Item item,
            Predicate<ItemStack> reversed
    ) {
        return handler.findCurios(item)
                .stream()
                .filter(result ->
                        !result.slotContext()
                                .cosmetic()
                )
                .map(result ->
                        result.stack()
                )
                .anyMatch(
                        reversed
                );
    }

    private static void applyModifier(
            Player player,
            double amount
    ) {
        Attribute attribute =
                ForgeRegistries.ATTRIBUTES
                        .getValue(
                                SPELL_POWER
                        );

        if (attribute == null) {
            return;
        }

        AttributeInstance instance =
                player.getAttribute(
                        attribute
                );

        if (instance == null) {
            return;
        }

        AttributeModifier current =
                instance.getModifier(
                        SPELL_POWER_UUID
                );

        if (current != null
                && current.getOperation()
                == AttributeModifier.Operation.MULTIPLY_BASE
                && Double.compare(
                        current.getAmount(),
                        amount
                ) == 0) {
            return;
        }

        if (current != null) {
            instance.removeModifier(
                    SPELL_POWER_UUID
            );
        }

        instance.addTransientModifier(
                new AttributeModifier(
                        SPELL_POWER_UUID,
                        EternalCareer.MOD_ID
                                + ":twisted_heart/spell_power",
                        amount,
                        AttributeModifier.Operation.MULTIPLY_BASE
                )
        );
    }

    private static void removeModifier(
            Player player
    ) {
        Attribute attribute =
                ForgeRegistries.ATTRIBUTES
                        .getValue(
                                SPELL_POWER
                        );

        if (attribute == null) {
            return;
        }

        AttributeInstance instance =
                player.getAttribute(
                        attribute
                );

        if (instance == null) {
            return;
        }

        if (instance.getModifier(
                SPELL_POWER_UUID
        ) != null) {
            instance.removeModifier(
                    SPELL_POWER_UUID
            );
        }
    }
}