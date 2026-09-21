package com.carrot123.auto_jigsaw_exporter.block;

import com.carrot123.auto_jigsaw_exporter.blockentity.StartMarkerBlockEntity;
import com.carrot123.auto_jigsaw_exporter.selection.MarkerSelectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public final class StartMarkerBlock extends BaseEntityBlock {
    public StartMarkerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StartMarkerBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof ServerPlayer player) {
            MarkerSelectionManager.selectStart(player, pos);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            MarkerSelectionManager.selectStart(serverPlayer, pos);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof StartMarkerBlockEntity marker) {
                BlockPos endPos = marker.getEndPos();
                if (endPos == null) {
                    serverPlayer.displayClientMessage(
                            Component.translatable("message.auto_jigsaw_exporter.start_waiting_end"),
                            false);
                } else {
                    serverPlayer.displayClientMessage(
                            Component.translatable("message.auto_jigsaw_exporter.selection_ready",
                                    endPos.getX(), endPos.getY(), endPos.getZ()),
                            false);
                }
            }
        }
        return InteractionResult.CONSUME;
    }
}
