package com.carrot123.eternal_career.client.renderer;

import com.carrot123.eternal_career.entity.SpinningScytheEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class SpinningScytheRenderer
        extends EntityRenderer<SpinningScytheEntity> {

    private final ItemRenderer itemRenderer;

    public SpinningScytheRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context);

        this.itemRenderer =
                context.getItemRenderer();

        this.shadowRadius =
                0.35F;
    }

    @Override
    public void render(
            SpinningScytheEntity entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        ItemStack stack =
                entity.getItem();

        poseStack.pushPose();

        poseStack.mulPose(
                Axis.XP.rotationDegrees(
                        90.0F
                )
        );

        poseStack.mulPose(
                Axis.ZP.rotation(
                        (entity.tickCount
                                + partialTicks)
                                * 0.9F
                )
        );

        poseStack.scale(
                1.6F,
                1.6F,
                1.6F
        );

        var model =
                itemRenderer.getModel(
                        stack,
                        entity.level(),
                        null,
                        entity.getId()
                );

        itemRenderer.render(
                stack,
                ItemDisplayContext.FIXED,
                false,
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                model
        );

        poseStack.popPose();

        super.render(
                entity,
                yaw,
                partialTicks,
                poseStack,
                buffer,
                packedLight
        );
    }

    @Override
    public ResourceLocation getTextureLocation(
            SpinningScytheEntity entity
    ) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}