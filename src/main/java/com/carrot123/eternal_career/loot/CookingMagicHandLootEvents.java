package com.carrot123.eternal_career.loot;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Forge-bus integration for rule reload, tag validation, and bonus drops. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CookingMagicHandLootEvents {
    private CookingMagicHandLootEvents() {
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new CookingMagicHandLootReloadListener(
                event.getServerResources().getLootData()));
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            CookingMagicHandLootManager.validateTags(event.getRegistryAccess());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !CurioEquipmentHelper.hasCookingMagicHand(player)) {
            return;
        }

        for (CookingMagicHandLootRule rule : CookingMagicHandLootManager.getMatchingRules(
                event.getEntity().getType(),
                level.registryAccess())) {
            CookingMagicHandLootGenerator.generate(event, level, player, rule);
        }
    }
}
