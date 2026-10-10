package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import com.carrot123.until_eternity.registry.ModAttributes;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SoulBoxEvents {
    private static final UUID MODIFIER_ID = StableAttributeModifiers.id("eternal_career:soul_box/all_damage");
    private SoulBoxEvents() {}

    @SubscribeEvent public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && tryUse(event.getEntity())) { event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide())); event.setCanceled(true); }
    }
    @SubscribeEvent public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && tryUse(event.getEntity())) { event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide())); event.setCanceled(true); }
    }
    private static boolean tryUse(Player player) {
        ItemStack main = player.getMainHandItem(), off = player.getOffhandItem();
        ItemStack box = main.getItem() instanceof SoulBoxItem ? main : off.getItem() instanceof SoulBoxItem ? off : ItemStack.EMPTY;
        ItemStack material = box == main ? off : main;
        if (box.isEmpty() || material.isEmpty() || !SoulFaction.hasCore(player)) return false;
        boolean fragment = SoulBoxItem.matches(material, SoulBoxItem.FRAGMENT);
        int level = SoulBoxItem.stage(box);
        boolean upgrade = level < 4 && SoulBoxItem.matches(material, SoulBoxItem.UNLOCK_ITEMS[level]);
        if (!fragment && !upgrade) return false;
        if (!player.level().isClientSide()) {
            if (fragment && !SoulBoxItem.addFragment(box)) player.displayClientMessage(Component.translatable("message.eternal_career.soul_box.full", SoulBoxItem.capacity(box)), true);
            else if (upgrade && SoulBoxItem.fragments(box) < SoulBoxItem.capacity(box)) player.displayClientMessage(Component.translatable("message.eternal_career.soul_box.not_ready", SoulBoxItem.capacity(box) * 20), true);
            else if (upgrade && SoulBoxItem.unlock(box, material)) {
                consume(player, material);
                player.displayClientMessage(Component.translatable("message.eternal_career.soul_box.unlocked", SoulBoxItem.capacity(box) * 20), true);
                sync(player);
            } else if (fragment && SoulBoxItem.fragments(box) > 0 && SoulBoxItem.fragments(box) <= SoulBoxItem.capacity(box)) { consume(player, material); sync(player); }
            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
        }
        return true;
    }
    private static void consume(Player player, ItemStack material) { if (!player.getAbilities().instabuild) material.shrink(1); }

    @SubscribeEvent public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player && player.tickCount % 5 == 0) sync(player);
    }
    private static void sync(Player player) {
        AttributeInstance instance = player.getAttribute(ModAttributes.ALL_DAMAGE.get());
        if (instance == null) return;
        double bonus = SoulFaction.hasCore(player) ? CuriosApi.getCuriosInventory(player).resolve().map(handler -> handler.findCurios(SoulCoreRegistry.SOUL_BOX.get()).stream().filter(result -> !result.slotContext().cosmetic() && "scroll".equals(result.slotContext().identifier())).mapToInt(result -> SoulBoxItem.fragments(result.stack())).max().orElse(0) * 0.20D).orElse(0D) : 0D;
        AttributeModifier current = instance.getModifier(MODIFIER_ID);
        if (current != null && Double.compare(current.getAmount(), bonus) == 0) return;
        if (current != null) instance.removeModifier(MODIFIER_ID);
        if (bonus > 0D) instance.addTransientModifier(new AttributeModifier(MODIFIER_ID, "eternal_career:soul_box", bonus, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
