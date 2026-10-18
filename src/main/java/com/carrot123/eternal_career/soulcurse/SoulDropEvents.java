package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import com.mojang.logging.LogUtils;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SoulDropEvents {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final double SOUL_CORE_FRAGMENT_CHANCE = 0.0005D;
    private static final Set<ResourceLocation> HYENA_TARGETS = Set.of(
            new ResourceLocation("legendary_monsters", "ancient_guardian"),
            new ResourceLocation("goety", "wither_necromancer")
    );
    private static boolean missingFragmentWarned;

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHyenaDrop(LivingDropsEvent event) {
        if (event.isCanceled()
                || event.getEntity().level().isClientSide()
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !hasHyenaCoreEquipped(player)) {
            return;
        }

        ResourceLocation target = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (target == null || !HYENA_TARGETS.contains(target)) {
            return;
        }

        Item fragment = ForgeRegistries.ITEMS.getValue(SoulBoxItem.FRAGMENT);
        if (fragment == null || fragment == Items.AIR) {
            if (!missingFragmentWarned) {
                LOGGER.warn("Hyena Core fragment item is missing: {}", SoulBoxItem.FRAGMENT);
                missingFragmentWarned = true;
            }
            return;
        }

        int count = 1 + player.getRandom().nextInt(4);
        event.getDrops().add(new ItemEntity(
                event.getEntity().level(),
                event.getEntity().getX(),
                event.getEntity().getY(),
                event.getEntity().getZ(),
                new ItemStack(fragment, count)
        ));
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

    private static boolean hasHyenaCoreEquipped(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> handler.findCurios(SoulCoreRegistry.HYENA_CORE.get())
                        .stream()
                        .anyMatch(result -> !result.slotContext().cosmetic()
                                && "ring".equals(result.slotContext().identifier())))
                .orElse(false);
    }
}
