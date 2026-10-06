package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.DiscouragedCurseItem;
import com.carrot123.eternal_career.item.FragileCurseItem;
import com.carrot123.eternal_career.item.HeavyCurseItem;
import com.carrot123.eternal_career.item.HungerCurseItem;
import com.carrot123.eternal_career.item.IgnoranceCurseItem;
import com.carrot123.eternal_career.item.PowerlessCurseItem;
import com.carrot123.eternal_career.item.UndeadCurseItem;
import com.carrot123.eternal_career.item.VulnerabilityCurseItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ModItemProperties {

    private static final ResourceLocation REVERSED =
            new ResourceLocation(
                    EternalCareer.MOD_ID,
                    "reversed"
            );

    private ModItemProperties() {
    }

    @SubscribeEvent
    public static void onClientSetup(
            FMLClientSetupEvent event
    ) {
        event.enqueueWork(() -> {

            ItemProperties.register(
                    ModItems.UNDEAD_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            UndeadCurseItem.isUpgraded(stack)
                                    ? 1.0F
                                    : 0.0F
            );

            ItemProperties.register(
                    ModItems.FRAGILE_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            FragileCurseItem.isReversed(stack)
                                    ? 1.0F
                                    : 0.0F
            );

            ItemProperties.register(
                    ModItems.POWERLESS_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            PowerlessCurseItem.isReversed(stack)
                                    ? 1.0F
                                    : 0.0F
            );

            ItemProperties.register(
                    ModItems.VULNERABILITY_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            VulnerabilityCurseItem.isReversed(stack)
                                    ? 1.0F
                                    : 0.0F
            );

            ItemProperties.register(
                    ModItems.IGNORANCE_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            IgnoranceCurseItem.isReversed(stack)
                                    ? 1.0F
                                    : 0.0F
            );

            ItemProperties.register(
                    ModItems.HEAVY_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            HeavyCurseItem.isReversed(stack)
                                    ? 1.0F
                                    : 0.0F
            );

            ItemProperties.register(
                    ModItems.DISCOURAGED_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            DiscouragedCurseItem.isReversed(stack)
                                    ? 1.0F
                                    : 0.0F
            );

            ItemProperties.register(
                    ModItems.HUNGER_CURSE.get(),
                    REVERSED,
                    (stack, level, entity, seed) ->
                            HungerCurseItem.isReversed(stack)
                                    ? 1.0F
                                    : 0.0F
            );
        });
    }
}