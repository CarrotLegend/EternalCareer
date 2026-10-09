package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.carrot123.eternal_career.event.PandoraSlotGuardEvents;
import com.carrot123.eternal_career.registry.ModItems;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class PandoraBoxItem
        extends Item
        implements ICurioItem, ICursed {

    public static final String PANDORA_BOX_SLOT =
            "pandora_box";

    public static final String CURSE_SPIRIT_SLOT =
            "curse_spirit";

    private static final String ACTIVATED_TAG =
            "EternalCareerPandoraBoxActivated";

    private static final String STORED_CURSES_TAG =
            "EternalCareerPandoraStoredCurses";

    public PandoraBoxItem(
            Properties properties
    ) {
        super(properties);
    }

    public static boolean isActivated(
            ItemStack stack
    ) {
        return stack.hasTag()
                && stack.getTag() != null
                && stack.getTag()
                .getBoolean(
                        ACTIVATED_TAG
                );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack heldStack =
                player.getItemInHand(
                        hand
                );

        if (!SuperpositionHandler
                .isTheCursedOne(player)) {
            return InteractionResultHolder.fail(
                    heldStack
            );
        }

        if (level.isClientSide) {
            return InteractionResultHolder
                    .sidedSuccess(
                            heldStack,
                            true
                    );
        }

        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> {

                    if (handler.findCurios(this)
                            .stream()
                            .anyMatch(result ->
                                    PANDORA_BOX_SLOT
                                            .equals(
                                                    result.slotContext()
                                                            .identifier()
                                            )
                                            && !result.slotContext()
                                            .cosmetic()
                            )) {
                        return InteractionResultHolder
                                .fail(
                                        heldStack
                                );
                    }

                    PandoraSlotGuardEvents
                            .unlockPandora(
                                    handler
                            );

                    var optional =
                            handler.getStacksHandler(
                                    PANDORA_BOX_SLOT
                            );

                    if (optional.isEmpty()) {
                        return InteractionResultHolder
                                .fail(
                                        heldStack
                                );
                    }

                    var stacks =
                            optional.get()
                                    .getStacks();

                    int emptySlot =
                            -1;

                    for (int i = 0;
                         i < stacks.getSlots();
                         i++) {
                        if (stacks.getStackInSlot(i)
                                .isEmpty()) {
                            emptySlot = i;
                            break;
                        }
                    }

                    if (emptySlot < 0) {
                        return InteractionResultHolder
                                .fail(
                                        heldStack
                                );
                    }

                    ItemStack equipped =
                            heldStack.copy();

                    equipped.setCount(
                            1
                    );

                    equipped.getOrCreateTag()
                            .putBoolean(
                                    ACTIVATED_TAG,
                                    true
                            );

                    handler.setEquippedCurio(
                            PANDORA_BOX_SLOT,
                            emptySlot,
                            equipped
                    );

                    PandoraSlotGuardEvents.sync(
                            player,
                            handler
                    );

                    ensureCursesInstalled(
                            player,
                            equipped
                    );

                    if (!player.getAbilities()
                            .instabuild) {
                        heldStack.shrink(
                                1
                        );
                    }

                    return InteractionResultHolder
                            .sidedSuccess(
                                    heldStack,
                                    false
                            );
                })
                .orElseGet(() ->
                        InteractionResultHolder
                                .fail(
                                        heldStack
                                )
                );
    }

    @Override
    public boolean canEquip(
            SlotContext context,
            ItemStack stack
    ) {
        if (!isActivated(stack)) {
            return false;
        }

        if (!PANDORA_BOX_SLOT.equals(
                context.identifier()
        )) {
            return false;
        }

        if (context.cosmetic()) {
            return false;
        }

        if (!(context.entity()
                instanceof Player player)) {
            return false;
        }

        if (!SuperpositionHandler
                .isTheCursedOne(player)) {
            return false;
        }

        return CurseEquipHelper.canEquipSingle(
                player,
                this,
                context
        );
    }

    @Override
    public boolean canEquipFromUse(
            SlotContext context,
            ItemStack stack
    ) {
        return false;
    }

    @Override
    public boolean canUnequip(
            SlotContext context,
            ItemStack stack
    ) {
        return context.entity()
                instanceof Player player
                && player.getAbilities()
                .instabuild;
    }

    @Override
    public void onUnequip(
            SlotContext context,
            ItemStack newStack,
            ItemStack stack
    ) {
        if (!(context.entity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!PANDORA_BOX_SLOT.equals(
                context.identifier()
        )) {
            return;
        }

        if (newStack.is(this)) {
            return;
        }

        if (!player.getAbilities()
                .instabuild) {
            return;
        }

        saveAndClearCurses(
                player,
                stack
        );
    }

    public static void ensureCursesInstalled(
            Player player,
            ItemStack box
    ) {
        CuriosApi.getCuriosInventory(player)
                .resolve()
                .ifPresent(handler -> {

                    var optional =
                            handler.getStacksHandler(
                                    CURSE_SPIRIT_SLOT
                            );

                    if (optional.isEmpty()) {
                        return;
                    }

                    var stacks =
                            optional.get()
                                    .getStacks();

                    if (stacks.getSlots() < 8) {
                        return;
                    }

                    List<Supplier<? extends Item>> order =
                            curseOrder();

                    ListTag stored =
                            box.getOrCreateTag()
                                    .getList(
                                            STORED_CURSES_TAG,
                                            Tag.TAG_COMPOUND
                                    );

                    for (int i = 0;
                         i < 8;
                         i++) {

                        if (!stacks.getStackInSlot(i)
                                .isEmpty()) {
                            continue;
                        }

                        ItemStack curse =
                                ItemStack.EMPTY;

                        if (stored.size() > i) {
                            curse =
                                    ItemStack.of(
                                            stored.getCompound(
                                                    i
                                            )
                                    );
                        }

                        if (curse.isEmpty()) {
                            curse =
                                    new ItemStack(
                                            order.get(i)
                                                    .get()
                                    );
                        }

                        handler.setEquippedCurio(
                                CURSE_SPIRIT_SLOT,
                                i,
                                curse
                        );
                    }
                });
    }

    private static void saveAndClearCurses(
            ServerPlayer player,
            ItemStack box
    ) {
        CuriosApi.getCuriosInventory(player)
                .resolve()
                .ifPresent(handler -> {

                    var optional =
                            handler.getStacksHandler(
                                    CURSE_SPIRIT_SLOT
                            );

                    if (optional.isEmpty()) {
                        return;
                    }

                    var stacks =
                            optional.get()
                                    .getStacks();

                    if (stacks.getSlots() < 8) {
                        return;
                    }

                    ListTag stored =
                            new ListTag();

                    for (int i = 0;
                         i < 8;
                         i++) {

                        ItemStack curse =
                                stacks.getStackInSlot(i)
                                        .copy();

                        CompoundTag saved =
                                new CompoundTag();

                        if (!curse.isEmpty()) {
                            curse.save(
                                    saved
                            );
                        }

                        stored.add(
                                saved
                        );

                        handler.setEquippedCurio(
                                CURSE_SPIRIT_SLOT,
                                i,
                                ItemStack.EMPTY
                        );
                    }

                    box.getOrCreateTag()
                            .put(
                                    STORED_CURSES_TAG,
                                    stored
                            );
                });
    }

    private static List<Supplier<? extends Item>>
    curseOrder() {
        return List.of(
                ModItems.UNDEAD_CURSE,
                ModItems.FRAGILE_CURSE,
                ModItems.POWERLESS_CURSE,
                ModItems.VULNERABILITY_CURSE,
                ModItems.IGNORANCE_CURSE,
                ModItems.HEAVY_CURSE,
                ModItems.DISCOURAGED_CURSE,
                ModItems.HUNGER_CURSE
        );
    }

    @Override
    public List<Component> getAttributesTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public List<Component> getSlotsTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public ICurio.DropRule getDropRule(
            SlotContext slotContext,
            DamageSource source,
            int lootingLevel,
            boolean recentlyHit,
            ItemStack stack
    ) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        if (isActivated(stack)) {
            tooltip.add(
                    Component.translatable(
                                    "tooltip.eternal_career.pandora_box.released"
                            )
                            .withStyle(
                                    ChatFormatting.DARK_PURPLE
                            )
            );
        } else {
            tooltip.add(
                    Component.translatable(
                                    "tooltip.eternal_career.pandora_box.warning"
                            )
                            .withStyle(
                                    ChatFormatting.DARK_RED
                            )
            );
        }
    }
}