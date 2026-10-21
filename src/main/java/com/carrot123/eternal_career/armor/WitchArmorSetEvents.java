package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WitchArmorSetEvents {
    private static final String ROOT = "eternal_career:witch_armor_set";
    private static final ResourceLocation BOTTLING = new ResourceLocation("goety", "bottling");
    private static final ResourceLocation PURE_RESISTANCE = new ResourceLocation("enigmaticaddons", "pure_resistance");
    private static final ResourceLocation[] EFFECTS = {BOTTLING, PURE_RESISTANCE};
    private static boolean updating;

    private WitchArmorSetEvents() {
    }

    @SubscribeEvent
    public static void equipment(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getSlot().getType() == EquipmentSlot.Type.ARMOR) {
            refresh(player);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player
                && player.tickCount % 20 == 0) {
            refresh(player);
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            refresh(player);
        }
    }

    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            refresh(player);
        }
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            refresh(player);
        }
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            data(player).remove(ROOT);
        }
    }

    @SubscribeEvent
    public static void effectAdded(MobEffectEvent.Added event) {
        if (updating || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(event.getEffectInstance().getEffect());
        if (!isSetEffect(id)) {
            return;
        }
        CompoundTag entry = setData(player).getCompound(id.toString());
        if (!entry.getBoolean("Owned")) {
            return;
        }
        MobEffectInstance incoming = event.getEffectInstance();
        MobEffectInstance previous = event.getOldEffectInstance();
        if (previous == null || incoming.getAmplifier() > previous.getAmplifier()
                || (incoming.getAmplifier() == previous.getAmplifier()
                && incoming.getDuration() > previous.getDuration())) {
            entry.remove("Owned");
            entry.remove("External");
        } else {
            entry.put("External", incoming.save(new CompoundTag()));
            entry.putLong("ExternalUntil", player.level().getGameTime() + incoming.getDuration());
        }
    }

    @SubscribeEvent
    public static void effectRemoved(MobEffectEvent.Remove event) {
        if (!updating && event.getEntity() instanceof ServerPlayer player) {
            clearIfTracked(player, event.getEffect());
        }
    }

    @SubscribeEvent
    public static void effectExpired(MobEffectEvent.Expired event) {
        if (!updating && event.getEntity() instanceof ServerPlayer player && event.getEffectInstance() != null) {
            clearIfTracked(player, event.getEffectInstance().getEffect());
        }
    }

    private static void clearIfTracked(ServerPlayer player, MobEffect effect) {
        ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        if (isSetEffect(id)) {
            setData(player).remove(id.toString());
        }
    }

    private static void refresh(ServerPlayer player) {
        boolean complete = player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.WITCH_HELMET.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.WITCH_CHESTPLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.WITCH_LEGGINGS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.WITCH_BOOTS.get());
        for (ResourceLocation id : EFFECTS) {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(id);
            if (effect == null) {
                continue;
            }
            CompoundTag state = setData(player);
            CompoundTag entry = state.getCompound(id.toString());
            MobEffectInstance active = player.getEffect(effect);
            if (!complete) {
                if (entry.getBoolean("Owned")) {
                    updating = true;
                    try {
                        player.removeEffect(effect);
                    } finally {
                        updating = false;
                    }
                    if (entry.contains("External")) {
                        MobEffectInstance external = MobEffectInstance.load(entry.getCompound("External"));
                        long remaining = entry.getLong("ExternalUntil") - player.level().getGameTime();
                        if (external != null && remaining > 0) {
                            player.addEffect(new MobEffectInstance(external.getEffect(),
                                    (int) Math.min(remaining, Integer.MAX_VALUE), external.getAmplifier(),
                                    external.isAmbient(), external.isVisible(), external.showIcon()));
                        }
                    }
                }
                state.remove(id.toString());
                continue;
            }
            if (entry.getBoolean("Owned") && active == null) {
                state.remove(id.toString());
                entry = new CompoundTag();
            }
            if (active == null || (entry.getBoolean("Owned") && active.getDuration() <= 40)) {
                updating = true;
                try {
                    player.addEffect(new MobEffectInstance(effect, 100, 0, false, false, true));
                } finally {
                    updating = false;
                }
                entry.putBoolean("Owned", true);
                state.put(id.toString(), entry);
            }
        }
    }

    private static boolean isSetEffect(ResourceLocation id) {
        return BOTTLING.equals(id) || PURE_RESISTANCE.equals(id);
    }

    private static CompoundTag setData(Player player) {
        return data(player).getCompound(ROOT);
    }

    private static CompoundTag data(Player player) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(Player.PERSISTED_NBT_TAG)) {
            root.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        CompoundTag persisted = root.getCompound(Player.PERSISTED_NBT_TAG);
        if (!persisted.contains(ROOT)) {
            persisted.put(ROOT, new CompoundTag());
        }
        return persisted;
    }
}
