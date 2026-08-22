package com.carrot123.eternal_career.client.model.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.armor.ChefArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/** Shared GeckoLib model and texture definition for every chef armor piece. */
public final class ChefArmorModel extends GeoModel<ChefArmorItem> {
    private static final ResourceLocation MODEL =
            new ResourceLocation(EternalCareer.MOD_ID, "geo/armor/chef_armor.geo.json");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EternalCareer.MOD_ID, "textures/armor/chef_armor.png");

    @Override
    public ResourceLocation getModelResource(ChefArmorItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ChefArmorItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ChefArmorItem animatable) {
        return null;
    }
}
