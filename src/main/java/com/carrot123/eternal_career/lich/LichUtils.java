package com.carrot123.eternal_career.lich;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.network.LichEligibilitySyncPacket;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public final class LichUtils {
    public static final TagKey<Item> LICH_ITEMS = TagKey.create(Registries.ITEM,
            new ResourceLocation(EternalCareer.MOD_ID, "lich_items"));
    public static final TagKey<Item> NON_LICH_ITEMS = TagKey.create(Registries.ITEM,
            new ResourceLocation(EternalCareer.MOD_ID, "non_lich_items"));
    private static final String USED_PANACEA_KEY = "eternal_career:used_panacea_before_lich";
    private static final String STAGE_KEY = "eternal_career:lich_stage";

    private LichUtils() {
    }

    public static boolean isCareerLich(Player player) {
        if (player == null || hasUsedPanaceaBeforeLich(player)) {
            return false;
        }
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(ModItems.LICH_RESEARCH_NOTES.get()).stream()
                        .anyMatch(result -> "charm".equals(result.slotContext().identifier())
                                && !result.slotContext().cosmetic()))
                .orElse(false);
    }

    public static LichStage getLichStage(Player player) {
        if (player == null) {
            return LichStage.PLAYER;
        }
        return LichStage.fromName(player.getPersistentData()
                .getCompound(Player.PERSISTED_NBT_TAG).getString(STAGE_KEY));
    }

    public static boolean isAtLeastLichStage(Player player, LichStage stage) {
        return player != null && getLichStage(player).isAtLeast(stage);
    }

    public static boolean isNoviceLich(Player player) {
        return player != null && getLichStage(player) == LichStage.NOVICE_LICH;
    }

    public static boolean isLich(Player player) {
        return isAtLeastLichStage(player, LichStage.NOVICE_LICH);
    }

    public static boolean canAdvanceTo(Player player, LichStage required, LichStage target) {
        return player != null && required != null && target != null && isCareerLich(player)
                && !hasUsedPanaceaBeforeLich(player)
                && target.ordinal() == required.ordinal() + 1
                && getLichStage(player) == required;
    }

    public static boolean setLichStage(ServerPlayer player, LichStage target) {
        LichStage current = getLichStage(player);
        if (target == null || !canAdvanceTo(player, current, target)) {
            return false;
        }
        persistedTag(player).putString(STAGE_KEY, target.name());
        LichStageAbilities.applyStage(player);
        sync(player);
        return true;
    }

    public static boolean hasUsedPanaceaBeforeLich(Player player) {
        return player != null && player.getPersistentData()
                .getCompound(Player.PERSISTED_NBT_TAG).getBoolean(USED_PANACEA_KEY);
    }

    public static void markPanaceaUsed(ServerPlayer player) {
        if (hasUsedPanaceaBeforeLich(player)) {
            return;
        }
        persistedTag(player).putBoolean(USED_PANACEA_KEY, true);
        sync(player);
    }

    public static void copyPersistentState(Player original, Player clone) {
        if (hasUsedPanaceaBeforeLich(original)) {
            persistedTag(clone).putBoolean(USED_PANACEA_KEY, true);
        }
        persistedTag(clone).putString(STAGE_KEY, getLichStage(original).name());
    }

    public static void setClientState(Player player, boolean used, LichStage stage) {
        persistedTag(player).putBoolean(USED_PANACEA_KEY, used);
        persistedTag(player).putString(STAGE_KEY, stage.name());
    }

    public static void sync(ServerPlayer player) {
        ModNetwork.send(player, new LichEligibilitySyncPacket(
                hasUsedPanaceaBeforeLich(player), getLichStage(player)));
    }

    public static boolean isLichItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(LICH_ITEMS);
    }

    public static boolean isNonLichItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(NON_LICH_ITEMS);
    }

    public static boolean deny(Player player, ItemStack stack) {
        return player != null && (isLichItem(stack) && hasUsedPanaceaBeforeLich(player)
                || isNonLichItem(stack) && (isCareerLich(player) || isLich(player)));
    }

    private static CompoundTag persistedTag(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(Player.PERSISTED_NBT_TAG)) {
            data.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return data.getCompound(Player.PERSISTED_NBT_TAG);
    }
}
