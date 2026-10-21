package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ServantDamageEvents {
    private static final double SPIDER_DAMAGE = 0.15D;
    private static final double SPIDER_DARKMAGE_DAMAGE = 4.50D;

    private ServantDamageEvents() {}

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (id == null || !"goety_revelation".equals(id.getNamespace())) return;

        EquipmentSlot requiredSlot;
        double amount;

        switch (id.getPath()) {
            case "spider_helmet" -> {
                requiredSlot = EquipmentSlot.HEAD;
                amount = SPIDER_DAMAGE;
            }
            case "spider_chestplate" -> {
                requiredSlot = EquipmentSlot.CHEST;
                amount = SPIDER_DAMAGE;
            }
            case "spider_leggings" -> {
                requiredSlot = EquipmentSlot.LEGS;
                amount = SPIDER_DAMAGE;
            }
            case "spider_boots" -> {
                requiredSlot = EquipmentSlot.FEET;
                amount = SPIDER_DAMAGE;
            }
            case "spider_darkmage_helmet" -> {
                requiredSlot = EquipmentSlot.HEAD;
                amount = SPIDER_DARKMAGE_DAMAGE;
            }
            case "spider_darkmage_chestplate" -> {
                requiredSlot = EquipmentSlot.CHEST;
                amount = SPIDER_DARKMAGE_DAMAGE;
            }
            case "spider_darkmage_leggings" -> {
                requiredSlot = EquipmentSlot.LEGS;
                amount = SPIDER_DARKMAGE_DAMAGE;
            }
            case "spider_darkmage_boots" -> {
                requiredSlot = EquipmentSlot.FEET;
                amount = SPIDER_DARKMAGE_DAMAGE;
            }
            default -> {
                return;
            }
        }

        if (event.getSlotType() != requiredSlot) return;

        String key = EternalCareer.MOD_ID + ":goety_revelation/" + id.getPath() + "/servant_damage";
        event.addModifier(ModAttributes.SERVANT_DAMAGE.get(), StableAttributeModifiers.create(key, amount, AttributeModifier.Operation.MULTIPLY_BASE));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServantDamage(LivingHurtEvent event) {
        Player owner = servantOwner(event.getSource().getEntity());
        if (owner == null) return;

        double multiplier = owner.getAttributeValue(ModAttributes.SERVANT_DAMAGE.get());
        if (!Double.isFinite(multiplier) || multiplier <= 0.0D || multiplier == 1.0D) return;

        event.setAmount((float) (event.getAmount() * multiplier));
    }

    static Player servantOwner(Entity attacker) {
        if (!(attacker instanceof OwnableEntity pet)) return null;
        if (attacker instanceof TamableAnimal tamable && !tamable.isTame()) return null;
        return pet.getOwner() instanceof Player owner ? owner : null;
    }
}
