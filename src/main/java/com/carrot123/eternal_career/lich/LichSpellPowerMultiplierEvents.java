package com.carrot123.eternal_career.lich;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.events.spell.BlockMagicEvent;
import com.Polarice3.Goety.common.events.spell.CastMagicEvent;
import com.Polarice3.Goety.common.events.spell.CastingMagicEvent;
import com.Polarice3.Goety.common.events.spell.StartMagicEvent;
import com.Polarice3.Goety.common.events.spell.StopMagicEvent;
import com.Polarice3.Goety.common.events.spell.TouchMagicEvent;
import com.Polarice3.Goety.utils.WandUtil;
import com.carrot123.eternal_career.EternalCareer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LichSpellPowerMultiplierEvents {
    private static final UUID MODIFIER_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:lich_spell_power_multiplier".getBytes(StandardCharsets.UTF_8));

    private static final String MODIFIER_NAME =
            "eternal_career:lich_spell_power_multiplier";

    private static final ResourceLocation SPELL_POWER_MULTIPLIER_ID =
            new ResourceLocation("goety_revelation", "spell_power_multiplier");

    private static final double NECROMANCY_MULTIPLIER = 0.20D;
    private static final double OTHER_MULTIPLIER = -0.90D;

    private LichSpellPowerMultiplierEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        apply(event.player, WandUtil.getSpell(event.player));
    }

    @SubscribeEvent
    public static void onStartMagic(StartMagicEvent event) {
        apply(event.getEntity(), event.getSpell());
    }

    @SubscribeEvent
    public static void onCastingMagic(CastingMagicEvent event) {
        apply(event.getEntity(), event.getSpell());
    }

    @SubscribeEvent
    public static void onCastMagic(CastMagicEvent event) {
        apply(event.getEntity(), event.getSpell());
    }

    @SubscribeEvent
    public static void onTouchMagic(TouchMagicEvent event) {
        apply(event.getEntity(), event.getSpell());
    }

    @SubscribeEvent
    public static void onBlockMagic(BlockMagicEvent event) {
        apply(event.getCaster(), event.getSpell());
    }

    @SubscribeEvent
    public static void onStopMagic(StopMagicEvent event) {
        apply(event.getEntity(), event.getSpell());
    }

    private static void apply(LivingEntity entity, ISpell spell) {
        if (!(entity instanceof Player player)) {
            return;
        }

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(
                SPELL_POWER_MULTIPLIER_ID);

        if (attribute == null) {
            return;
        }

        AttributeInstance instance = player.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        AttributeModifier current = instance.getModifier(MODIFIER_UUID);

        if (!LichUtils.isCareerLich(player) || spell == null) {
            if (current != null) {
                instance.removeModifier(MODIFIER_UUID);
            }
            return;
        }

        double amount = spell.getSpellType() == SpellType.NECROMANCY
                ? NECROMANCY_MULTIPLIER
                : OTHER_MULTIPLIER;

        if (current != null
                && current.getOperation() == AttributeModifier.Operation.ADDITION
                && Double.compare(current.getAmount(), amount) == 0) {
            return;
        }

        if (current != null) {
            instance.removeModifier(MODIFIER_UUID);
        }

        instance.addTransientModifier(new AttributeModifier(
                MODIFIER_UUID,
                MODIFIER_NAME,
                amount,
                AttributeModifier.Operation.ADDITION));
    }
}