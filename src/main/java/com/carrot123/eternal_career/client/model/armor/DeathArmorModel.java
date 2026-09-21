package com.carrot123.eternal_career.client.model.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.armor.DeathArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class DeathArmorModel extends GeoModel<DeathArmorItem> {

    private static final ResourceLocation MODEL =
            new ResourceLocation(
                    EternalCareer.MOD_ID,
                    "geo/armor/death_armor.geo.json"
            );

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    EternalCareer.MOD_ID,
                    "textures/armor/death_armor.png"
            );

    @Override
    public ResourceLocation getModelResource(
            DeathArmorItem animatable
    ) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(
            DeathArmorItem animatable
    ) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(
            DeathArmorItem animatable
    ) {
        return null;
    }
}