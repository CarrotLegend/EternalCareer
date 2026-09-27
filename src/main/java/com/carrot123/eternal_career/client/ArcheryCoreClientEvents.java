package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.archery.ArcheryCoreEvents;
import com.carrot123.eternal_career.item.ArcheryMasterCoreItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, value = Dist.CLIENT)
public final class ArcheryCoreClientEvents {
    private static ItemStack pendingBow = ItemStack.EMPTY;
    private static InteractionHand pendingHand;

    private ArcheryCoreClientEvents() {
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null || minecraft.screen != null
                || !minecraft.options.keyUse.isDown()
                || !ArcheryCoreEvents.hasMode(minecraft.player, ArcheryMasterCoreItem.RAPID)) {
            clearPending();
            return;
        }
        if (!pendingBow.isEmpty()) {
            ItemStack current = minecraft.player.getItemInHand(pendingHand);
            if (current != pendingBow || current.isEmpty()) {
                clearPending();
                return;
            }
            if (!minecraft.player.isUsingItem()) {
                minecraft.gameMode.useItem(minecraft.player, pendingHand);
            }
            clearPending();
            return;
        }
        if (!minecraft.player.isUsingItem()
                || !(minecraft.player.getUseItem().getItem() instanceof BowItem)) {
            return;
        }
        InteractionHand hand = minecraft.player.getUsedItemHand();
        if (BowItem.getPowerForTime(minecraft.player.getTicksUsingItem()) < 1.0F) {
            return;
        }
        pendingBow = minecraft.player.getItemInHand(hand);
        pendingHand = hand;
        minecraft.gameMode.releaseUsingItem(minecraft.player);
    }

    private static void clearPending() {
        pendingBow = ItemStack.EMPTY;
        pendingHand = null;
    }

    @Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        private Registration() {
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> ItemProperties.register(ModItems.ARCHERY_MASTER_CORE.get(),
                    new ResourceLocation(EternalCareer.MOD_ID, "rapid"),
                    (stack, level, entity, seed) ->
                            ArcheryMasterCoreItem.mode(stack) == ArcheryMasterCoreItem.RAPID
                                    ? 1.0F : 0.0F));
        }
    }
}
