package com.carrot123.eternal_career.client.model.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.armor.WitchArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class WitchArmorModel extends GeoModel<WitchArmorItem> {
    private static final ResourceLocation MODEL =
            new ResourceLocation(EternalCareer.MOD_ID, "geo/armor/witch_armor.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EternalCareer.MOD_ID, "textures/armor/witch_armor.png");

    @Override
    public ResourceLocation getModelResource(WitchArmorItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WitchArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WitchArmorItem animatable) {
        return null;
    }
}
