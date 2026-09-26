package com.carrot123.eternal_career.soulblessing;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class SoulBlessingModifiers {
    private SoulBlessingModifiers() {}

    public static void reconcile(ServerPlayer player) {
        SoulBlessingInventory inventory = SoulBlessingHelper.inventory(player);
        for (int i = 0; i < SoulBlessingSlots.COUNT; i++) reconcileSlot(player, inventory, i);
        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
    }

    private static void reconcileSlot(ServerPlayer player, SoulBlessingInventory inventory, int slot) {
        ItemStack oldStack = inventory.previous(slot);
        ItemStack newStack = inventory.getStackInSlot(slot);
        if (!ItemStack.matches(oldStack, newStack)) {
            if (oldStack.getItem() instanceof SoulBlessingItem oldItem) {
                remove(player, oldItem, oldStack, slot);
                oldItem.onUnequipped(player, slot, oldStack);
            }
            if (newStack.getItem() instanceof SoulBlessingItem newItem) {
                newItem.onEquipped(player, slot, newStack);
            }
            inventory.remember(slot, newStack);
        }
        if (newStack.getItem() instanceof SoulBlessingItem item) {
            for (int ordinal = 0; ordinal < item.getSoulBlessingAttributes(newStack).size(); ordinal++) {
                SoulBlessingAttribute entry = item.getSoulBlessingAttributes(newStack).get(ordinal);
                AttributeInstance instance = player.getAttribute(entry.attribute());
                if (instance == null) continue;
                String key = key(item, slot, entry, ordinal);
                AttributeModifier existing = instance.getModifier(StableAttributeModifiers.id(key));
                if (existing != null && existing.getOperation() == entry.operation()
                        && Double.compare(existing.getAmount(), entry.amount()) == 0) continue;
                if (existing != null) instance.removeModifier(existing.getId());
                instance.addTransientModifier(StableAttributeModifiers.create(key,
                        entry.amount(), entry.operation()));
            }
        }
    }

    private static void remove(ServerPlayer player, SoulBlessingItem item, ItemStack stack, int slot) {
        for (int ordinal = 0; ordinal < item.getSoulBlessingAttributes(stack).size(); ordinal++) {
            SoulBlessingAttribute entry = item.getSoulBlessingAttributes(stack).get(ordinal);
            AttributeInstance instance = player.getAttribute(entry.attribute());
            if (instance != null) instance.removeModifier(
                    StableAttributeModifiers.id(key(item, slot, entry, ordinal)));
        }
    }

    private static String key(SoulBlessingItem item, int slot, SoulBlessingAttribute entry,
            int ordinal) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        ResourceLocation attributeId = ForgeRegistries.ATTRIBUTES.getKey(entry.attribute());
        if (itemId == null || attributeId == null) {
            throw new IllegalStateException("Unregistered Soul Blessing item or attribute");
        }
        return EternalCareer.MOD_ID + ":soul_blessing/" + itemId + "/"
                + SoulBlessingSlots.type(slot).id() + "/" + slot + "/"
                + attributeId + "/" + ordinal;
    }
}
