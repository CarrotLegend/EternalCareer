package com.carrot123.eternal_career.client.renderer.armor;

import com.carrot123.eternal_career.armor.DeathArmorItem;
import com.carrot123.eternal_career.client.model.armor.DeathArmorModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class DeathArmorRenderer
        extends GeoArmorRenderer<DeathArmorItem> {

    public DeathArmorRenderer() {
        super(new DeathArmorModel());
    }
}