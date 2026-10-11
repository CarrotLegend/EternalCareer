package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SoulDropEvents {

    private static final double SOUL_CORE_FRAGMENT_CHANCE = 0.0005D;

    private SoulDropEvents() {
    }

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(SoulDropManager.INSTANCE);
    }

    @SubscribeEvent
    public static void onDrop(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)
                || !SoulFaction.hasCore(player)) {
            return;
        }

        if (event.getEntity() instanceof Mob
                && hasSoulCoreEquipped(player)
                && player.getRandom().nextDouble() < SOUL_CORE_FRAGMENT_CHANCE
                && ForgeRegistries.ITEMS.containsKey(SoulBoxItem.FRAGMENT)) {
            Item fragment = ForgeRegistries.ITEMS.getValue(SoulBoxItem.FRAGMENT);
            if (fragment != null) {
                event.getDrops().add(new ItemEntity(
                        event.getEntity().level(),
                        event.getEntity().getX(),
                        event.getEntity().getY(),
                        event.getEntity().getZ(),
                        new ItemStack(fragment)
                ));
            }
        }

        ResourceLocation mob = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (mob == null) {
            return;
        }

        for (SoulDropManager.DropRule rule : SoulDropManager.INSTANCE.getRules()) {
            if (!rule.bosses().contains(mob)
                    || player.getRandom().nextDouble() >= rule.chance()
                    || !ForgeRegistries.ITEMS.containsKey(rule.item())) {
                continue;
            }
            Item item = ForgeRegistries.ITEMS.getValue(rule.item());
            if (item == null) {
                continue;
            }
            int count = rule.min() + player.getRandom().nextInt(rule.max() - rule.min() + 1);
            event.getDrops().add(new ItemEntity(
                    event.getEntity().level(),
                    event.getEntity().getX(),
                    event.getEntity().getY(),
                    event.getEntity().getZ(),
                    new ItemStack(item, count)
            ));
        }
    }

    private static boolean hasSoulCoreEquipped(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> handler.findCurios(SoulCoreRegistry.SOUL_CORE.get())
                        .stream()
                        .anyMatch(result -> !result.slotContext().cosmetic()
                                && "ring".equals(result.slotContext().identifier())))
                .orElse(false);
    }
}
