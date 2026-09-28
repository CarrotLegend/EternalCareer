package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class FletchingTableEvents {
    private FletchingTableEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND
                || event.getEntity().isSpectator()
                || event.getUseBlock() == Event.Result.DENY
                || !event.getLevel().getBlockState(event.getPos()).is(Blocks.FLETCHING_TABLE)) {
            return;
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        event.setCanceled(true);
        if (event.getEntity() instanceof ServerPlayer player) {
            BlockPos pos = event.getPos();
            NetworkHooks.openScreen(player, new SimpleMenuProvider(
                    (id, inventory, ignored) -> new FletchingTableMenu(id, inventory, pos),
                    Component.translatable("fletching.eternal_career.title")),
                    buffer -> buffer.writeBlockPos(pos));
        }
    }
}
