package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChaosBladeDropEvents {
    private static final TagKey<EntityType<?>> FORGE_BOSSES = bossTag("forge", "bosses");
    private static final TagKey<EntityType<?>> COMMON_BOSSES = bossTag("c", "bosses");

    private ChaosBladeDropEvents() {
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide || victim instanceof Player) {
            return;
        }
        ServerPlayer killer = directPlayerKiller(event.getSource());
        if (killer == null || killer instanceof FakePlayer
                || !CurioEquipmentHelper.hasEquippedCurio(
                        killer, ModItems.CHAOS_BLADE.get(), "charm")) {
            return;
        }

        boolean boss = isBoss(victim);
        double chance = ChaosBladeDropChance.calculate(boss, event.getLootingLevel());
        if (victim.getRandom().nextDouble() >= chance) {
            return;
        }

        ItemStack stack = new ItemStack(
                boss ? ModItems.CHAOS_STEAK.get() : ModItems.CHAOS_MEAT.get(),
                boss ? 1 + victim.getRandom().nextInt(5) : 1);
        ItemEntity drop = new ItemEntity(victim.level(),
                victim.getX(), victim.getY(), victim.getZ(), stack);
        drop.setDefaultPickUpDelay();
        event.getDrops().add(drop);
    }

    private static ServerPlayer directPlayerKiller(DamageSource source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return null;
        }
        Entity direct = source.getDirectEntity();
        if (direct == player) {
            return player;
        }
        if (direct instanceof Projectile projectile && projectile.getOwner() == player) {
            return player;
        }
        return null;
    }

    private static boolean isBoss(LivingEntity entity) {
        return entity instanceof WitherBoss
                || entity instanceof EnderDragon
                || entity.getType().is(FORGE_BOSSES)
                || entity.getType().is(COMMON_BOSSES);
    }

    private static TagKey<EntityType<?>> bossTag(String namespace, String path) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(namespace, path));
    }

}
