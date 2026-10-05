package com.carrot123.eternal_career.client.renderer;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.client.model.NecromancyLaserModel;
import com.carrot123.eternal_career.entity.projectile.NecromancyLaserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public final class NecromancyLaserRenderer extends EntityRenderer<NecromancyLaserEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            EternalCareer.MOD_ID,
            "textures/entity/necromancy_laser.png"
    );

    private final NecromancyLaserModel model;

    public NecromancyLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new NecromancyLaserModel(
                context.bakeLayer(NecromancyLaserModel.LAYER)
        );
        shadowRadius = 0.0F;
    }

    @Override
    public void render(
            NecromancyLaserEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight
    ) {
        poseStack.pushPose();

        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-8D) {
            float yaw = (float) Math.atan2(motion.x, motion.z);
            float pitch = (float) -Math.atan2(
                    motion.y,
                    Math.sqrt(motion.x * motion.x + motion.z * motion.z)
            );
            poseStack.mulPose(Axis.YP.rotation(yaw));
            poseStack.mulPose(Axis.XP.rotation(pitch));
        }

        VertexConsumer buffer = buffers.getBuffer(
                RenderType.entityTranslucentEmissive(TEXTURE)
        );
        model.renderToBuffer(
                poseStack,
                buffer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(NecromancyLaserEntity entity) {
        return TEXTURE;
    }
}
