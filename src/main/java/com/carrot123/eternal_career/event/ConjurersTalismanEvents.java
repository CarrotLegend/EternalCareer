package com.carrot123.eternal_career.event;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ConjurersTalismanEvents {
    private static final ResourceLocation TALISMAN_ID = new ResourceLocation("irons_spellbooks", "conjurers_talisman");
    private static final ResourceLocation GREATER_TALISMAN_ID = new ResourceLocation("irons_spellbooks", "greater_conjurers_talisman");
    private static final String NECKLACE_SLOT = "necklace";
    private static final String PROCESSED_KEY = "eternal_career:conjurers_talisman_health_processed";
    private static final UUID HEALTH_MODIFIER_ID = StableAttributeModifiers.id(
            "eternal_career:conjurers_talisman/summoned_health");

    private ConjurersTalismanEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCurioAttributes(CurioAttributeModifierEvent event) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        double amount;
        if (TALISMAN_ID.equals(itemId)) {
            amount = 0.45D;
        } else if (GREATER_TALISMAN_ID.equals(itemId)) {
            amount = 0.90D;
        } else {
            return;
        }

        if (!NECKLACE_SLOT.equals(event.getSlotContext().identifier())
                || event.getSlotContext().cosmetic()) {
            return;
        }

        event.clearModifiers();
        String key = EternalCareer.MOD_ID + ":" + itemId.getPath()
                + "/" + NECKLACE_SLOT + "/" + event.getUuid() + "/servant_damage";
        event.addModifier(ModAttributes.SERVANT_DAMAGE.get(), StableAttributeModifiers.create(
                key, amount, AttributeModifier.Operation.MULTIPLY_BASE));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSummonJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()
                || !(event.getEntity() instanceof LivingEntity summoned)
                || !(summoned instanceof IServant servant)) {
            return;
        }

        if (!(servant.getTrueOwner() instanceof Player owner)) {
            return;
        }

        if (summoned.getPersistentData().getBoolean(PROCESSED_KEY)) {
            return;
        }

        AttributeInstance health = summoned.getAttribute(Attributes.MAX_HEALTH);
        if (health == null || health.getModifier(HEALTH_MODIFIER_ID) != null) {
            return;
        }

        double amount = equipped(owner, GREATER_TALISMAN_ID) ? 3.0D
                : equipped(owner, TALISMAN_ID) ? 1.0D : 0.0D;
        if (amount == 0.0D) {
            return;
        }

        health.addPermanentModifier(StableAttributeModifiers.create(HEALTH_MODIFIER_ID,
                "eternal_career:conjurers_talisman/summoned_health", amount,
                AttributeModifier.Operation.MULTIPLY_TOTAL));
        summoned.getPersistentData().putBoolean(PROCESSED_KEY, true);
        summoned.setHealth(summoned.getMaxHealth());
    }

    private static boolean equipped(Player player, ResourceLocation itemId) {
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        return item != null && CuriosApi.getCuriosInventory(player).resolve()
                .map(inventory -> inventory.findCurios(item).stream()
                        .anyMatch(result -> NECKLACE_SLOT.equals(result.slotContext().identifier())
                                && !result.slotContext().cosmetic()))
                .orElse(false);
    }
}
