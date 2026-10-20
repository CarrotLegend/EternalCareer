package com.carrot123.eternal_career.witch;

import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.utils.BrewUtils;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.WitchHeartItem;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.witch.WitchHeartMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WitchHeartTickEvents {
    private static final long PERIOD = 20L * 60L;
    private WitchHeartTickEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        CuriosApi.getCuriosInventory(player).ifPresent(inventory -> inventory.findCurios(ModItems.WITCH_HEART.get()).forEach(result -> {
            if (result.slotContext().cosmetic() || !"charm".equals(result.slotContext().identifier())) return;
            useIfReady(player, result.stack());
        }));
    }

    private static void useIfReady(ServerPlayer player, ItemStack heart) {
        if (heart.isEmpty() || !(heart.getItem() instanceof WitchHeartItem)) return;
        long now = player.serverLevel().getGameTime();
        CompoundTag tag = heart.getOrCreateTag();
        if (!tag.contains(WitchHeartItem.NEXT_USE_KEY, 4)) {
            tag.putLong(WitchHeartItem.NEXT_USE_KEY, now + PERIOD);
            return;
        }
        if (now < tag.getLong(WitchHeartItem.NEXT_USE_KEY)) return;
        tag.putLong(WitchHeartItem.NEXT_USE_KEY, now + PERIOD);
        ItemStack brew = WitchHeartItem.getStoredBrew(heart);
        if (brew.isEmpty() || !(brew.getItem() instanceof BrewItem)) return;

        // Match Goety's drink application without granting free glass bottles or damaging the stored stack.
        for (MobEffectInstance effect : PotionUtils.getMobEffects(brew)) {
            if (effect.getEffect().isInstantenous()) {
                effect.getEffect().applyInstantenousEffect(player, player, player, effect.getAmplifier(), 1.0D);
            } else {
                player.addEffect(new MobEffectInstance(effect));
            }
        }
        for (BrewEffectInstance effect : BrewUtils.getBrewEffects(brew)) {
            effect.getEffect().drinkBlockEffect(player, player, player, effect.getAmplifier(), BrewUtils.getAreaOfEffect(brew));
        }
        if (brew.getCount() < brew.getMaxStackSize()) {
            brew.shrink(1);
            if (player.containerMenu instanceof WitchHeartMenu menu && menu.stores(heart)) menu.updateStoredBrew(brew);
            else WitchHeartItem.setStoredBrew(heart, brew);
        }
    }
}
