package com.carrot123.eternal_career.client.renderer.armor;

import com.carrot123.eternal_career.armor.ChefArmorItem;
import com.carrot123.eternal_career.client.model.armor.ChefArmorModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/** Slot-aware renderer for all four chef armor items. */
public final class ChefArmorRenderer extends GeoArmorRenderer<ChefArmorItem> {
    public ChefArmorRenderer() {
        super(new ChefArmorModel());
    }
}
