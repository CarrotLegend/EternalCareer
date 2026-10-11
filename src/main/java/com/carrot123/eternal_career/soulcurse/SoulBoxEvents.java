package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import com.carrot123.until_eternity.registry.ModAttributes;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SoulBoxEvents {

    private static final UUID DAMAGE_ID =
            StableAttributeModifiers.id("eternal_career:soul_box/all_damage");

    private static final UUID ATTACK_SPEED_ID =
            StableAttributeModifiers.id("eternal_career:soul_box/attack_speed");

    private static final double DAMAGE_PER_FRAGMENT = 0.20D;
    private static final double ATTACK_SPEED_PER_FRAGMENT = 0.10D;
    private static final double DODGE_PER_FRAGMENT = 0.01D;
    private static final double HEAL_PER_FRAGMENT = 0.10D;
    private static final double DODGE_CAP = 0.85D;

    private SoulBoxEvents() {
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && tryUse(event.getEntity())) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && tryUse(event.getEntity())) {
            event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            event.setCanceled(true);
        }
    }

    private static boolean tryUse(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        ItemStack box = main.getItem() instanceof SoulBoxItem ? main
                : off.getItem() instanceof SoulBoxItem ? off : ItemStack.EMPTY;
        ItemStack material = box == main ? off : main;

        if (box.isEmpty() || material.isEmpty() || !SoulFaction.hasCore(player)) {
            return false;
        }

        boolean fragment = SoulBoxItem.matches(material, SoulBoxItem.FRAGMENT);
        int stage = SoulBoxItem.stage(box);
        boolean upgrade = stage < SoulBoxItem.UNLOCK_ITEMS.length
                && SoulBoxItem.matches(material, SoulBoxItem.UNLOCK_ITEMS[stage]);

        if (!fragment && !upgrade) {
            return false;
        }

        if (!player.level().isClientSide()) {
            if (fragment && !SoulBoxItem.addFragment(box)) {
                player.displayClientMessage(
                        Component.translatable("message.eternal_career.soul_box.full", SoulBoxItem.capacity(box)), true);
            } else if (upgrade && SoulBoxItem.fragments(box) < SoulBoxItem.capacity(box)) {
                player.displayClientMessage(
                        Component.translatable("message.eternal_career.soul_box.not_ready", SoulBoxItem.capacity(box) * 20), true);
            } else if (upgrade && SoulBoxItem.unlock(box, material)) {
                consume(player, material);
                player.displayClientMessage(
                        Component.translatable("message.eternal_career.soul_box.unlocked", SoulBoxItem.capacity(box) * 20), true);
                sync(player);
            } else if (fragment && SoulBoxItem.fragments(box) > 0
                    && SoulBoxItem.fragments(box) <= SoulBoxItem.capacity(box)) {
                consume(player, material);
                sync(player);
            }
            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
        }

        return true;
    }

    private static void consume(Player player, ItemStack material) {
        if (!player.getAbilities().instabuild) {
            material.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && event.player instanceof ServerPlayer player
                && player.tickCount % 5 == 0) {
            sync(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        int count = equippedFragments(player);
        if (count <= 0) {
            return;
        }

        double dodge = Math.min(DODGE_CAP, count * DODGE_PER_FRAGMENT);
        if (player.getRandom().nextDouble() < dodge) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        int count = equippedFragments(player);
        if (count <= 0 || event.getAmount() <= 0.0F || !Float.isFinite(event.getAmount())) {
            return;
        }

        event.setAmount((float) (event.getAmount() * (1.0D + count * HEAL_PER_FRAGMENT)));
    }

    private static int equippedFragments(Player player) {
        if (!SoulFaction.hasCore(player)) {
            return 0;
        }

        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(SoulCoreRegistry.SOUL_BOX.get())
                        .stream()
                        .filter(result -> !result.slotContext().cosmetic()
                                && "scroll".equals(result.slotContext().identifier()))
                        .mapToInt(result -> SoulBoxItem.fragments(result.stack()))
                        .max()
                        .orElse(0))
                .orElse(0);
    }

    private static void sync(Player player) {
        int count = equippedFragments(player);
        setBonus(
                player.getAttribute(ModAttributes.ALL_DAMAGE.get()),
                DAMAGE_ID,
                count * DAMAGE_PER_FRAGMENT,
                AttributeModifier.Operation.MULTIPLY_BASE,
                "eternal_career:soul_box/all_damage"
        );
        setBonus(
                player.getAttribute(Attributes.ATTACK_SPEED),
                ATTACK_SPEED_ID,
                count * ATTACK_SPEED_PER_FRAGMENT,
                AttributeModifier.Operation.MULTIPLY_TOTAL,
                "eternal_career:soul_box/attack_speed"
        );
    }

    private static void setBonus(
            AttributeInstance instance,
            UUID id,
            double amount,
            AttributeModifier.Operation operation,
            String name
    ) {
        if (instance == null) {
            return;
        }

        AttributeModifier current = instance.getModifier(id);
        if (current != null && current.getOperation() == operation
                && Double.compare(current.getAmount(), amount) == 0) {
            return;
        }

        if (current != null) {
            instance.removeModifier(id);
        }

        if (amount > 0.0D) {
            instance.addTransientModifier(new AttributeModifier(id, name, amount, operation));
        }
    }
}
