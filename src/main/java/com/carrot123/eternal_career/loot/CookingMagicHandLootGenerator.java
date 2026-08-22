package com.carrot123.eternal_career.loot;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/** Executes every configured roll as an independent full loot-table draw. */
public final class CookingMagicHandLootGenerator {
    private CookingMagicHandLootGenerator() {
    }

    public static void generate(
            LivingDropsEvent event,
            ServerLevel level,
            ServerPlayer player,
            CookingMagicHandLootRule rule
    ) {
        LivingEntity victim = event.getEntity();
        Entity directKiller = event.getSource().getDirectEntity();
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, victim)
                .withParameter(LootContextParams.ORIGIN, victim.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, event.getSource())
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .withParameter(LootContextParams.KILLER_ENTITY, player)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, directKiller)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.ENTITY);

        LootTable table = level.getServer().getLootData().getElement(
                new LootDataId<>(LootDataType.TABLE, rule.lootTable()));
        if (table == null) {
            return;
        }

        for (int roll = 0; roll < rule.rolls(); roll++) {
            table.getRandomItems(params, stack -> appendDrop(event, victim, stack));
        }
    }

    private static void appendDrop(LivingDropsEvent event, LivingEntity victim, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(
                victim.level(),
                victim.getX(),
                victim.getY(),
                victim.getZ(),
                stack);
        itemEntity.setDefaultPickUpDelay();
        event.getDrops().add(itemEntity);
    }
}
