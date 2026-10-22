package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import com.carrot123.until_eternity.registry.ModAttributes;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SoulScarEquipmentEvents {
    private static final UUID GLOVE = StableAttributeModifiers.id("eternal_career:soul_gauntlet/all_damage");
    private static final UUID COWARD = StableAttributeModifiers.id("eternal_career:scar/coward_damage");
    private static final UUID FRAGILE_ARMOR = StableAttributeModifiers.id("eternal_career:scar/fragile_armor");
    private static final UUID FRAGILE_RESIST = StableAttributeModifiers.id("eternal_career:scar/fragile_resistance");
    private static final UUID PESSIMIST = StableAttributeModifiers.id("eternal_career:scar/pessimist_health");
    private static final ResourceLocation RESISTANCE = new ResourceLocation("puffish_attributes", "resistance");
    private SoulScarEquipmentEvents() {}

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || event.getEntity() == player || !SoulFaction.hasCore(player)) return;
        ResourceLocation mobId = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType());
        if (mobId == null) return;
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> handler.findCurios(SoulScarRegistry.SOUL_GAUNTLET.get())
                .stream().filter(result -> !result.slotContext().cosmetic() && "hands".equals(result.slotContext().identifier()))
                .findFirst().ifPresent(result -> {
                    ItemStack upgraded = result.stack().copy();
                    if (SoulGauntletItem.addKill(upgraded, mobId)) {
                        handler.setEquippedCurio(result.slotContext().identifier(), result.slotContext().index(), upgraded);
                        refresh(player, handler);
                    }
                }));
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player) || player.tickCount % 5 != 0) return;
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> { installScars(handler, player); refresh(player, handler); });
    }

    private static boolean isWorn(ICuriosItemHandler handler, Item item, String slot) {
        return handler.findCurios(item).stream().anyMatch(found -> !found.slotContext().cosmetic() && slot.equals(found.slotContext().identifier()));
    }

    private static ItemStack equipped(ICuriosItemHandler handler, Item item, String slot) {
        return handler.findCurios(item).stream().filter(found -> !found.slotContext().cosmetic() && slot.equals(found.slotContext().identifier()))
                .map(found -> found.stack()).findFirst().orElse(ItemStack.EMPTY);
    }

    private static void installScars(ICuriosItemHandler handler, Player player) {
        if (!SoulFaction.hasCore(player)) return;
        ItemStack charm = equipped(handler, SoulScarRegistry.TRANSFORMING_SCAR.get(), "charm");
        if (charm.isEmpty()) return;
        handler.getStacksHandler(TransformingScarItem.SLOT).ifPresent(handlerForSlot -> {
            var stacks = handlerForSlot.getStacks();
            if (stacks.getSlots() < 3) return;
            ListTag saved = TransformingScarItem.savedScars(charm);

            for (int i = 0; i < Math.min(stacks.getSlots(), saved.size()); i++) {
                if (!stacks.getStackInSlot(i).isEmpty()) continue;

                ItemStack restore = ItemStack.of(saved.getCompound(i));

                if (!restore.isEmpty()) {
                    ItemStack carried = takeFromInventory(player, restore.getItem());
                    if (!carried.isEmpty()) {
                        restore = carried;
                    }

                    handler.setEquippedCurio(TransformingScarItem.SLOT, i, restore);
                }
            }

            List<Item> required = List.of(
                    SoulScarRegistry.COWARD.get(),
                    SoulScarRegistry.FRAGILE.get(),
                    SoulScarRegistry.PESSIMIST.get()
            );

            for (Item item : required) {
                if (isWorn(handler, item, TransformingScarItem.SLOT)) continue;

                for (int i = 0; i < stacks.getSlots(); i++) {
                    if (!stacks.getStackInSlot(i).isEmpty()) continue;

                    ItemStack restore = takeFromInventory(player, item);

                    if (restore.isEmpty()) {
                        restore = new ItemStack(item);
                    }

                    handler.setEquippedCurio(TransformingScarItem.SLOT, i, restore);
                    break;
                }
            }
        });
    }

    private static ItemStack takeFromInventory(Player player, Item expected) {
        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);

            if (!stack.is(expected)) {
                continue;
            }

            ItemStack restored = stack.split(1);

            if (stack.isEmpty()) {
                player.getInventory().items.set(i, ItemStack.EMPTY);
            }

            player.getInventory().setChanged();
            return restored;
        }

        return ItemStack.EMPTY;
    }

    private static void refresh(Player player, ICuriosItemHandler handler) {
        boolean active = SoulFaction.hasCore(player);
        ItemStack glove = active ? equipped(handler, SoulScarRegistry.SOUL_GAUNTLET.get(), "hands") : ItemStack.EMPTY;
        double gloveBonus = glove.isEmpty() ? 0 : SoulGauntletItem.damageTenthsPercent(glove) / 1000.0D;
        ItemStack charm = active ? equipped(handler, SoulScarRegistry.TRANSFORMING_SCAR.get(), "charm") : ItemStack.EMPTY;
        ItemStack coward = charm.isEmpty() ? ItemStack.EMPTY : equipped(handler, SoulScarRegistry.COWARD.get(), TransformingScarItem.SLOT);
        ItemStack fragile = charm.isEmpty() ? ItemStack.EMPTY : equipped(handler, SoulScarRegistry.FRAGILE.get(), TransformingScarItem.SLOT);
        ItemStack pessimist = charm.isEmpty() ? ItemStack.EMPTY : equipped(handler, SoulScarRegistry.PESSIMIST.get(), TransformingScarItem.SLOT);
        set(player.getAttribute(ModAttributes.ALL_DAMAGE.get()), GLOVE, gloveBonus, AttributeModifier.Operation.MULTIPLY_BASE);
        set(player.getAttribute(ModAttributes.ALL_DAMAGE.get()), COWARD, coward.isEmpty() ? 0 : SoulScarItem.reversed(coward) ? 1.2 : -0.5, AttributeModifier.Operation.MULTIPLY_BASE);
        set(player.getAttribute(Attributes.ARMOR), FRAGILE_ARMOR, fragile.isEmpty() || SoulScarItem.reversed(fragile) ? 0 : -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        Attribute resist = ForgeRegistries.ATTRIBUTES.getValue(RESISTANCE);
        if (resist != null) set(player.getAttribute(resist), FRAGILE_RESIST, !fragile.isEmpty() && SoulScarItem.reversed(fragile) ? 0.4 : 0, AttributeModifier.Operation.ADDITION);
        set(player.getAttribute(Attributes.MAX_HEALTH), PESSIMIST, pessimist.isEmpty() ? 0 : SoulScarItem.reversed(pessimist) ? 0.6 : -0.4, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    private static void set(AttributeInstance instance, UUID id, double value, AttributeModifier.Operation operation) {
        if (instance == null) return;
        AttributeModifier before = instance.getModifier(id);
        if (before != null && before.getOperation() == operation && Double.compare(before.getAmount(), value) == 0) return;
        if (before != null) instance.removeModifier(id);
        if (value != 0 && Double.isFinite(value)) instance.addTransientModifier(new AttributeModifier(id, "eternal_career:soul_scar/" + id, value, operation));
    }
}
