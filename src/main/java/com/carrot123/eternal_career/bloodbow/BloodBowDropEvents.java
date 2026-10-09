package com.carrot123.eternal_career.bloodbow;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.soulcurse.SoulCoreRegistry;
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

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BloodBowDropEvents {
    private BloodBowDropEvents() {}

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) { event.addListener(BloodBowDropManager.INSTANCE); }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player) || !hasBloodHunter(player)) return;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (id == null) return;
        for (BloodBowDropManager.Rule rule : BloodBowDropManager.INSTANCE.rules()) {
            if (!rule.bosses().contains(id) || player.getRandom().nextDouble() >= rule.chance() || !ForgeRegistries.ITEMS.containsKey(rule.item())) continue;
            Item item = ForgeRegistries.ITEMS.getValue(rule.item());
            if (item == null) continue;
            int count = rule.min() + player.getRandom().nextInt(rule.max() - rule.min() + 1);
            ItemEntity drop = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), new ItemStack(item, count));
            event.getDrops().add(drop);
        }
    }

    private static boolean hasBloodHunter(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player).resolve().map(handler -> handler.findCurios(SoulCoreRegistry.BLOOD_HUNTER_CORE.get()).stream().anyMatch(result ->
                !result.slotContext().cosmetic() && "ring".equals(result.slotContext().identifier()))).orElse(false);
    }
}
