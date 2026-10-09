package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.PandoraBoxItem;
import com.carrot123.eternal_career.registry.ModItems;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PandoraSlotGuardEvents {

    public static final UUID PANDORA_LOCK_UUID =
            UUID.fromString(
                    "7789428f-8ebf-473d-952e-8498bb9389b1"
            );

    public static final UUID CURSE_LOCK_UUID =
            UUID.fromString(
                    "5dfeee43-6cda-4773-bbe8-35561b7a9d26"
            );

    public static final UUID UNLOCK_MARKER_UUID =
            UUID.fromString(
                    "4723c997-a983-4e77-9e10-721ea31abce9"
            );

    public static final UUID LEGACY_UNLOCK_UUID =
            UUID.fromString(
                    "b1c6c0ef-8518-4ef9-a352-47bd8b9a3327"
            );

    private PandoraSlotGuardEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player =
                event.player;

        if (player.level().isClientSide) {
            return;
        }

        CuriosApi.getCuriosInventory(player)
                .resolve()
                .ifPresent(handler ->
                        sync(
                                player,
                                handler
                        )
                );
    }

    public static void sync(
            Player player,
            ICuriosItemHandler handler
    ) {
        migrateOldUnlock(
                handler
        );

        var equippedPandora =
                handler.findCurios(
                                ModItems.PANDORA_BOX.get()
                        )
                        .stream()
                        .filter(result ->
                                PandoraBoxItem.PANDORA_BOX_SLOT
                                        .equals(
                                                result.slotContext()
                                                        .identifier()
                                        )
                        )
                        .filter(result ->
                                !result.slotContext()
                                        .cosmetic()
                        )
                        .filter(result ->
                                PandoraBoxItem.isActivated(
                                        result.stack()
                                )
                        )
                        .findFirst();

        boolean equipped =
                equippedPandora.isPresent();

        boolean unlocked =
                hasModifier(
                        handler,
                        PandoraBoxItem.PANDORA_BOX_SLOT,
                        UNLOCK_MARKER_UUID
                );

        if (equipped && !unlocked) {
            addUnlockMarker(
                    handler
            );

            unlocked = true;
        }

        if (unlocked) {
            removeModifier(
                    handler,
                    PandoraBoxItem.PANDORA_BOX_SLOT,
                    PANDORA_LOCK_UUID
            );
        } else {
            addModifier(
                    handler,
                    PandoraBoxItem.PANDORA_BOX_SLOT,
                    PANDORA_LOCK_UUID,
                    "eternal_career:pandora_box_lock",
                    -1.0D
            );
        }

        forceUpdate(
                handler,
                PandoraBoxItem.PANDORA_BOX_SLOT
        );

        if (equipped) {
            removeModifier(
                    handler,
                    PandoraBoxItem.CURSE_SPIRIT_SLOT,
                    CURSE_LOCK_UUID
            );

            forceUpdate(
                    handler,
                    PandoraBoxItem.CURSE_SPIRIT_SLOT
            );

            equippedPandora.ifPresent(result ->
                    PandoraBoxItem.ensureCursesInstalled(
                            player,
                            result.stack()
                    )
            );

            return;
        }

        if (!hasAnyCurse(
                handler
        )) {
            addModifier(
                    handler,
                    PandoraBoxItem.CURSE_SPIRIT_SLOT,
                    CURSE_LOCK_UUID,
                    "eternal_career:curse_spirit_lock",
                    -8.0D
            );

            forceUpdate(
                    handler,
                    PandoraBoxItem.CURSE_SPIRIT_SLOT
            );
        }
    }

    public static void unlockPandora(
            ICuriosItemHandler handler
    ) {
        addUnlockMarker(
                handler
        );

        removeModifier(
                handler,
                PandoraBoxItem.PANDORA_BOX_SLOT,
                PANDORA_LOCK_UUID
        );

        forceUpdate(
                handler,
                PandoraBoxItem.PANDORA_BOX_SLOT
        );
    }

    private static void migrateOldUnlock(
            ICuriosItemHandler handler
    ) {
        if (!hasModifier(
                handler,
                PandoraBoxItem.PANDORA_BOX_SLOT,
                LEGACY_UNLOCK_UUID
        )) {
            return;
        }

        addUnlockMarker(
                handler
        );

        removeModifier(
                handler,
                PandoraBoxItem.PANDORA_BOX_SLOT,
                LEGACY_UNLOCK_UUID
        );

        forceUpdate(
                handler,
                PandoraBoxItem.PANDORA_BOX_SLOT
        );
    }

    private static boolean hasAnyCurse(
            ICuriosItemHandler handler
    ) {
        return handler.getStacksHandler(
                        PandoraBoxItem.CURSE_SPIRIT_SLOT
                )
                .map(stacksHandler -> {
                    var stacks =
                            stacksHandler.getStacks();

                    for (int i = 0;
                         i < stacks.getSlots();
                         i++) {
                        if (!stacks.getStackInSlot(i)
                                .isEmpty()) {
                            return true;
                        }
                    }

                    return false;
                })
                .orElse(false);
    }

    private static void addUnlockMarker(
            ICuriosItemHandler handler
    ) {
        addModifier(
                handler,
                PandoraBoxItem.PANDORA_BOX_SLOT,
                UNLOCK_MARKER_UUID,
                "eternal_career:pandora_box_unlocked",
                0.0D
        );
    }

    private static void addModifier(
            ICuriosItemHandler handler,
            String slot,
            UUID uuid,
            String name,
            double amount
    ) {
        if (hasModifier(
                handler,
                slot,
                uuid
        )) {
            return;
        }

        handler.addPermanentSlotModifier(
                slot,
                uuid,
                name,
                amount,
                AttributeModifier.Operation.ADDITION
        );
    }

    private static void removeModifier(
            ICuriosItemHandler handler,
            String slot,
            UUID uuid
    ) {
        if (!hasModifier(
                handler,
                slot,
                uuid
        )) {
            return;
        }

        handler.removeSlotModifier(
                slot,
                uuid
        );
    }

    private static boolean hasModifier(
            ICuriosItemHandler handler,
            String slot,
            UUID uuid
    ) {
        return handler.getModifiers()
                .get(slot)
                .stream()
                .anyMatch(modifier ->
                        uuid.equals(
                                modifier.getId()
                        )
                );
    }

    private static void forceUpdate(
            ICuriosItemHandler handler,
            String slot
    ) {
        handler.getStacksHandler(slot)
                .ifPresent(stacksHandler ->
                        stacksHandler
                                .getStacks()
                                .getSlots()
                );
    }
}