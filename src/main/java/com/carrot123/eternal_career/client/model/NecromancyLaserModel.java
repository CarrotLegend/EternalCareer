package com.carrot123.eternal_career.client.model;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.entity.projectile.NecromancyLaserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public final class NecromancyLaserModel extends EntityModel<NecromancyLaserEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            new ResourceLocation(EternalCareer.MOD_ID, "necromancy_laser"),
            "main"
    );

    private final ModelPart root;

    public NecromancyLaserModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "aura",
                CubeListBuilder.create().texOffs(0, 48)
                        .addBox(-1.5F, -1.5F, -16.0F, 3.0F, 3.0F, 32.0F),
                PartPose.ZERO
        );
        root.addOrReplaceChild(
                "core",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, -1.0F, -16.0F, 2.0F, 2.0F, 32.0F),
                PartPose.ZERO
        );

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(
            NecromancyLaserEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        root.render(poseStack, buffer, packedLight, packedOverlay,
                red, green, blue, alpha);
    }
}
