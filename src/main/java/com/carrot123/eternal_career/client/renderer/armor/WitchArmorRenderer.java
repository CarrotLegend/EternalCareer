package com.carrot123.eternal_career.client.renderer.armor;

import com.carrot123.eternal_career.armor.WitchArmorItem;
import com.carrot123.eternal_career.client.model.armor.WitchArmorModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public final class WitchArmorRenderer extends GeoArmorRenderer<WitchArmorItem> {
    public WitchArmorRenderer() {
        super(new WitchArmorModel());
    }
}
