package com.carrot123.eternal_career.effect;

import com.Polarice3.Goety.common.entities.util.BrewEffectCloud;
import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WitchBrewEvents {
    private static final String OWNER_KEY = "eternal_career:witch_domain_owner";

    private WitchBrewEvents() {
    }

    @SubscribeEvent
    public static void onAdded(MobEffectEvent.Added event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || event.getEffectInstance().getEffect() != ModEffects.WITCH_DOMAIN.get()) {
            return;
        }

        Player owner = resolvePlayer(event.getEffectSource());
        CompoundTag data = data(target);
        if (owner == null) {
            data.remove(OWNER_KEY);
        } else {
            data.putUUID(OWNER_KEY, owner.getUUID());
        }
    }

    @SubscribeEvent
    public static void onRemoved(MobEffectEvent.Remove event) {
        if (!event.getEntity().level().isClientSide && event.getEffect() == ModEffects.WITCH_DOMAIN.get()) {
            data(event.getEntity()).remove(OWNER_KEY);
        }
    }

    @SubscribeEvent
    public static void onExpired(MobEffectEvent.Expired event) {
        if (!event.getEntity().level().isClientSide && event.getEffectInstance() != null
                && event.getEffectInstance().getEffect() == ModEffects.WITCH_DOMAIN.get()) {
            data(event.getEntity()).remove(OWNER_KEY);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompoundTag original = data(event.getOriginal());
        if (original.hasUUID(OWNER_KEY) && event.getOriginal().hasEffect(ModEffects.WITCH_DOMAIN.get())) {
            data(event.getEntity()).putUUID(OWNER_KEY, original.getUUID(OWNER_KEY));
        }
    }

    public static ServerPlayer owner(LivingEntity holder) {
        if (!(holder.level() instanceof ServerLevel level)) {
            return null;
        }
        CompoundTag data = data(holder);
        if (!data.hasUUID(OWNER_KEY)) {
            return null;
        }
        return level.getServer().getPlayerList().getPlayer(data.getUUID(OWNER_KEY));
    }

    public static ServerPlayer resolvePlayer(Entity source) {
        Entity current = source;
        for (int depth = 0; depth < 8 && current != null; depth++) {
            if (current instanceof ServerPlayer player) {
                return player.server.getPlayerList().getPlayer(player.getUUID()) == player ? player : null;
            }
            Entity next = null;
            if (current instanceof Projectile projectile) {
                next = projectile.getOwner();
            } else if (current instanceof BrewEffectCloud cloud) {
                next = cloud.getOwner();
            } else if (current instanceof BrewGas gas) {
                next = gas.getOwner();
            } else if (current instanceof OwnableEntity ownable) {
                next = ownable.getOwner();
            }
            if (next == current) {
                break;
            }
            current = next;
        }
        return null;
    }

    private static CompoundTag data(LivingEntity entity) {
        CompoundTag root = entity.getPersistentData();
        if (!(entity instanceof Player)) {
            return root;
        }
        if (!root.contains(Player.PERSISTED_NBT_TAG)) {
            root.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return root.getCompound(Player.PERSISTED_NBT_TAG);
    }
}
