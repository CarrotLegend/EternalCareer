package com.carrot123.eternal_career.curse;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.PandoraBoxItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class CurseSpiritDropEvents {

    private CurseSpiritDropEvents() {
    }

    @SubscribeEvent
    public static void onAddReloadListener(
            AddReloadListenerEvent event
    ) {
        event.addListener(
                CurseSpiritDropManager.INSTANCE
        );
    }

    @SubscribeEvent
    public static void onLivingDrops(
            LivingDropsEvent event
    ) {
        if (!(event.getSource().getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!hasPandoraBox(player)) {
            return;
        }

        ResourceLocation entityId =
                ForgeRegistries.ENTITY_TYPES.getKey(
                        event.getEntity().getType()
                );

        if (entityId == null) {
            return;
        }

        for (CurseSpiritDropManager.DropRule rule
                : CurseSpiritDropManager.INSTANCE
                .getRules()) {

            if (!rule.matches(entityId)) {
                continue;
            }

            if (player.getRandom().nextDouble()
                    >= rule.chance()) {
                continue;
            }

            Item item =
                    ForgeRegistries.ITEMS.getValue(
                            rule.itemId()
                    );

            if (item == null) {
                continue;
            }

            int count =
                    rule.minCount();

            if (rule.maxCount()
                    > rule.minCount()) {
                count +=
                        player.getRandom().nextInt(
                                rule.maxCount()
                                        - rule.minCount()
                                        + 1
                        );
            }

            ItemStack stack =
                    new ItemStack(
                            item,
                            count
                    );

            ItemEntity itemEntity =
                    new ItemEntity(
                            event.getEntity().level(),
                            event.getEntity().getX(),
                            event.getEntity().getY(),
                            event.getEntity().getZ(),
                            stack
                    );

            event.getDrops().add(
                    itemEntity
            );
        }
    }

    private static boolean hasPandoraBox(
            ServerPlayer player
    ) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(
                                        ModItems.PANDORA_BOX.get()
                                )
                                .stream()
                                .anyMatch(result ->
                                        PandoraBoxItem.PANDORA_BOX_SLOT
                                                .equals(
                                                        result
                                                                .slotContext()
                                                                .identifier()
                                                )
                                                && !result
                                                .slotContext()
                                                .cosmetic()
                                                && PandoraBoxItem
                                                .isActivated(
                                                        result.stack()
                                                )
                                )
                )
                .orElse(false);
    }
}