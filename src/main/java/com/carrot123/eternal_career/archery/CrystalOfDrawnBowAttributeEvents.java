package com.carrot123.eternal_career.archery;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CrystalOfDrawnBowAttributeEvents {
    private static final ResourceLocation RANGED_DAMAGE =
            new ResourceLocation("puffish_attributes", "ranged_damage");
    private static final ResourceLocation CHARGE_SPEED =
            new ResourceLocation("until_eternity", "charge_speed");
    private static final ResourceLocation PROJECTILE_SPEED =
            new ResourceLocation("terra_curio", "ranged_velocity");

    private static final String RANGED_DAMAGE_KEY =
            "eternal_career:crystal_of_drawn_bow/ranged_damage";
    private static final String CHARGE_SPEED_KEY =
            "eternal_career:crystal_of_drawn_bow/charge_speed";
    private static final String PROJECTILE_SPEED_KEY =
            "eternal_career:crystal_of_drawn_bow/projectile_speed";

    private static final Set<UUID> PENDING = new HashSet<>();

    private CrystalOfDrawnBowAttributeEvents() {
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide
                && "charm".equals(event.getIdentifier())
                && (event.getFrom().is(ModItems.CRYSTAL_OF_DRAWN_BOW.get())
                || event.getTo().is(ModItems.CRYSTAL_OF_DRAWN_BOW.get()))) {
            reconcile(player);
            PENDING.add(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.END && !player.level().isClientSide
                && (PENDING.remove(player.getUUID()) || player.tickCount % 20 == 0)) {
            reconcile(player);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        reconcileAndQueue(event.getEntity());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        reconcileAndQueue(event.getEntity());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        reconcileAndQueue(event.getEntity());
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        reconcileAndQueue(event.getEntity());
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING.remove(event.getEntity().getUUID());
    }

    private static void reconcileAndQueue(Player player) {
        if (!player.level().isClientSide) {
            reconcile(player);
            PENDING.add(player.getUUID());
        }
    }

    public static void reconcile(Player player) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        boolean equipped = CurioEquipmentHelper.hasCrystalOfDrawnBow(player);
        reconcileModifier(player, equipped, RANGED_DAMAGE, RANGED_DAMAGE_KEY, 3.00D);
        reconcileModifier(player, equipped, CHARGE_SPEED, CHARGE_SPEED_KEY, 0.40D);
        reconcileModifier(player, equipped, PROJECTILE_SPEED, PROJECTILE_SPEED_KEY, 0.50D);
    }

    private static void reconcileModifier(Player player, boolean equipped,
            ResourceLocation attributeId, String key, double amount) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
        if (attribute == null) {
            if (equipped) {
                throw new IllegalStateException("Missing crystal attribute: " + attributeId);
            }
            return;
        }

        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            if (equipped) {
                throw new IllegalStateException("Player lacks crystal attribute: " + attributeId);
            }
            return;
        }

        UUID id = StableAttributeModifiers.id(key);
        AttributeModifier existing = instance.getModifier(id);
        if (!equipped) {
            if (existing != null) {
                instance.removeModifier(id);
            }
            return;
        }

        if (existing != null && existing.getAmount() == amount
                && existing.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE
                && key.equals(existing.getName())) {
            return;
        }

        if (existing != null) {
            instance.removeModifier(id);
        }
        instance.addTransientModifier(StableAttributeModifiers.create(
                id, key, amount, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
