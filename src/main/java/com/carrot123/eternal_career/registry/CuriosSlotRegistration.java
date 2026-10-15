package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.PandoraBoxItem;
import com.carrot123.eternal_career.soulcurse.TransformingScarItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CuriosSlotRegistration {
    private CuriosSlotRegistration() {}

    @SubscribeEvent
    @SuppressWarnings("removal")
    public static void onInterModEnqueue(InterModEnqueueEvent event) {
        register(PandoraBoxItem.PANDORA_BOX_SLOT, 0);
        register(PandoraBoxItem.CURSE_SPIRIT_SLOT, 0);
        register(TransformingScarItem.SLOT, 0);
    }

    @SuppressWarnings("removal")
    private static void register(String slot, int size) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder(slot).size(size).build());
    }
}
