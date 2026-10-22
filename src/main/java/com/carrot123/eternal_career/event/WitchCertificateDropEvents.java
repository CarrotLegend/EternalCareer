package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.FoodBookCurio;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WitchCertificateDropEvents {
    private static final int MAX_OWNER_DEPTH = 8;
    private static final ResourceLocation MYSTIC_CORE = new ResourceLocation("goety", "mystic_core");
    private static final ResourceLocation SAVAGE_TOOTH = new ResourceLocation("goety", "savage_tooth");

    private WitchCertificateDropEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }

        Player killer = findKillingPlayer(event);
        if (killer == null || !hasWitchCertificate(killer)) {
            return;
        }

        if (victim.getType() == EntityType.EVOKER) {
            if (victim.getRandom().nextFloat() < 0.15F) {
                addRegisteredDrop(event, MYSTIC_CORE, 1);
            }
        } else if (victim.getType() == EntityType.WITCH) {
            addDrop(event, Items.NETHER_WART, 1);
            addDrop(event, Items.CRIMSON_FUNGUS, 2);
            addDrop(event, Items.WARPED_FUNGUS, 2);
        } else if (victim.getType() == EntityType.VINDICATOR) {
            if (victim.getRandom().nextFloat() < 0.25F) {
                addRegisteredDrop(event, SAVAGE_TOOTH, 1);
            }
        } else if (victim.getType() == EntityType.ENDERMAN) {
            if (victim.getRandom().nextFloat() < 0.50F) {
                addDrop(event, Items.CHORUS_FLOWER, 1);
            }
        }
    }

    private static boolean hasWitchCertificate(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(inventory -> inventory.findCurios(ModItems.WITCH_CERTIFICATE.get()).stream()
                        .anyMatch(result -> FoodBookCurio.CHARM_SLOT.equals(result.slotContext().identifier())
                                && !result.slotContext().cosmetic()))
                .orElse(false);
    }

    private static void addRegisteredDrop(LivingDropsEvent event, ResourceLocation itemId, int count) {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item != null && item != Items.AIR) {
            addDrop(event, item, count);
        }
    }

    private static void addDrop(LivingDropsEvent event, Item item, int count) {
        LivingEntity victim = event.getEntity();
        ItemEntity drop = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(),
                new ItemStack(item, count));
        drop.setDefaultPickUpDelay();
        event.getDrops().add(drop);
    }

    private static Player findKillingPlayer(LivingDropsEvent event) {
        Player player = findPlayer(event.getSource().getEntity(), 0);
        if (player != null) {
            return player;
        }

        player = findPlayer(event.getSource().getDirectEntity(), 0);
        if (player != null) {
            return player;
        }

        LivingEntity killCredit = event.getEntity().getKillCredit();
        if (killCredit instanceof Player creditedPlayer) {
            return creditedPlayer;
        }
        return findPlayer(killCredit, 0);
    }

    private static Player findPlayer(Entity entity, int depth) {
        if (entity == null || depth >= MAX_OWNER_DEPTH) {
            return null;
        }
        if (entity instanceof Player player) {
            return player;
        }
        if (entity instanceof TraceableEntity traceable) {
            Entity owner = traceable.getOwner();
            if (owner != null && owner != entity) {
                Player player = findPlayer(owner, depth + 1);
                if (player != null) {
                    return player;
                }
            }
        }
        if (entity instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner != null && owner != entity) {
                Player player = findPlayer(owner, depth + 1);
                if (player != null) {
                    return player;
                }
            }
        }
        return null;
    }
}
