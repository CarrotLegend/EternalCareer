package com.carrot123.auto_jigsaw_exporter.blockentity;

import com.carrot123.auto_jigsaw_exporter.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public final class StartMarkerBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos endPos;

    public StartMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.START_MARKER.get(), pos, state);
    }

    @Nullable
    public BlockPos getEndPos() {
        return endPos;
    }

    public void setEndPos(@Nullable BlockPos endPos) {
        this.endPos = endPos == null ? null : endPos.immutable();
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (endPos != null) {
            tag.putLong("EndPos", endPos.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        endPos = tag.contains("EndPos") ? BlockPos.of(tag.getLong("EndPos")) : null;
    }
}
