package com.carrot123.eternal_career.curio;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.registry.ModItems;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GodsRecognitionCurioEvents {
    public static final double KITCHENWARE_BONUS = 6.0D;
    public static final UUID KITCHENWARE_MODIFIER_ID = UUID.nameUUIDFromBytes(
            "eternal_career:gods_recognition/kitchenware_damage"
                    .getBytes(StandardCharsets.UTF_8));

    private GodsRecognitionCurioEvents() {
    }

    @SubscribeEvent
    public static void onCurioAttributes(CurioAttributeModifierEvent event) {
        if (!event.getItemStack().is(ModItems.GODS_RECOGNITION.get())) {
            return;
        }

        event.addModifier(ModAttributes.KITCHENWARE_DAMAGE.get(), new AttributeModifier(
                KITCHENWARE_MODIFIER_ID,
                "God of Cookery's Approval kitchenware damage",
                KITCHENWARE_BONUS,
                AttributeModifier.Operation.MULTIPLY_BASE));
    }
}
