package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.carrot123.eternal_career.registry.ModItems;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class PandoraBoxItem extends Item implements ICurioItem, ICursed {

    public static final String PANDORA_BOX_SLOT =
            "pandora_box";

    public static final String CURSE_SPIRIT_SLOT =
            "curse_spirit";

    private static final String ACTIVATED_TAG =
            "EternalCareerPandoraBoxActivated";

    private static final String STORED_CURSES_TAG =
            "EternalCareerPandoraStoredCurses";

    private static final String INSTALL_PENDING_TAG =
            "EternalCareerPandoraInstallPending";

    private static final UUID PANDORA_BOX_UNLOCK_UUID =
            UUID.fromString(
                    "b1c6c0ef-8518-4ef9-a352-47bd8b9a3327"
            );

    public PandoraBoxItem(Properties properties) {
        super(properties);
    }

    public static boolean isActivated(
            ItemStack stack
    ) {
        return stack.hasTag()
                && stack.getTag() != null
                && stack.getTag()
                .getBoolean(ACTIVATED_TAG);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack heldStack =
                player.getItemInHand(hand);

        if (!SuperpositionHandler
                .isTheCursedOne(player)) {
            return InteractionResultHolder.fail(
                    heldStack
            );
        }

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(
                    heldStack,
                    true
            );
        }

        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> {

                    if (handler.isEquipped(this)) {
                        return InteractionResultHolder
                                .fail(heldStack);
                    }

                    boolean alreadyUnlocked =
                            handler.getModifiers()
                                    .get(PANDORA_BOX_SLOT)
                                    .stream()
                                    .anyMatch(modifier ->
                                            modifier.getId()
                                                    .equals(
                                                            PANDORA_BOX_UNLOCK_UUID
                                                    )
                                    );

                    if (!alreadyUnlocked) {
                        handler.addPermanentSlotModifier(
                                PANDORA_BOX_SLOT,
                                PANDORA_BOX_UNLOCK_UUID,
                                EternalCareerName(),
                                1.0D,
                                AttributeModifier.Operation.ADDITION
                        );
                    }

                    var optionalStacksHandler =
                            handler.getStacksHandler(
                                    PANDORA_BOX_SLOT
                            );

                    if (optionalStacksHandler.isEmpty()) {
                        if (!alreadyUnlocked) {
                            handler.removeSlotModifier(
                                    PANDORA_BOX_SLOT,
                                    PANDORA_BOX_UNLOCK_UUID
                            );
                        }

                        return InteractionResultHolder
                                .fail(heldStack);
                    }

                    var stacksHandler =
                            optionalStacksHandler.get();

                    int slots =
                            stacksHandler.getStacks()
                                    .getSlots();

                    int emptySlot = -1;

                    for (int i = 0; i < slots; i++) {
                        if (stacksHandler.getStacks()
                                .getStackInSlot(i)
                                .isEmpty()) {
                            emptySlot = i;
                            break;
                        }
                    }

                    if (emptySlot < 0) {
                        if (!alreadyUnlocked) {
                            handler.removeSlotModifier(
                                    PANDORA_BOX_SLOT,
                                    PANDORA_BOX_UNLOCK_UUID
                            );
                        }

                        return InteractionResultHolder
                                .fail(heldStack);
                    }

                    ItemStack equippedStack =
                            heldStack.copy();

                    equippedStack.setCount(1);

                    equippedStack.getOrCreateTag()
                            .putBoolean(
                                    ACTIVATED_TAG,
                                    true
                            );

                    player.getPersistentData()
                            .putBoolean(
                                    INSTALL_PENDING_TAG,
                                    true
                            );

                    handler.setEquippedCurio(
                            PANDORA_BOX_SLOT,
                            emptySlot,
                            equippedStack
                    );

                    if (!player.getAbilities()
                            .instabuild) {
                        heldStack.shrink(1);
                    }

                    return InteractionResultHolder
                            .sidedSuccess(
                                    heldStack,
                                    false
                            );
                })
                .orElseGet(() ->
                        InteractionResultHolder
                                .fail(heldStack)
                );
    }

    @Override
    public void onEquip(
            SlotContext context,
            ItemStack prevStack,
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

        if (!isActivated(stack)) {
            return;
        }

        player.getPersistentData()
                .putBoolean(
                        INSTALL_PENDING_TAG,
                        true
                );
    }

    @Override
    public void curioTick(
            SlotContext context,
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

        if (!isActivated(stack)) {
            return;
        }

        if (!player.getPersistentData()
                .getBoolean(
                        INSTALL_PENDING_TAG
                )) {
            return;
        }

        if (installCurses(
                player,
                stack
        )) {
            player.getPersistentData()
                    .putBoolean(
                            INSTALL_PENDING_TAG,
                            false
                    );
        }
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

        if (player.getAbilities().instabuild) {
            saveAndClearCurses(
                    player,
                    stack
            );
        }

        player.getPersistentData()
                .putBoolean(
                        INSTALL_PENDING_TAG,
                        false
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

        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(this)
                                .stream()
                                .noneMatch(result ->
                                        !result.slotContext()
                                                .cosmetic()
                                )
                )
                .orElse(false);
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
    public Multimap<Attribute, AttributeModifier>
    getAttributeModifiers(
            SlotContext context,
            UUID slotUuid,
            ItemStack stack
    ) {
        Multimap<Attribute, AttributeModifier> modifiers =
                ArrayListMultimap.create();

        if (!PANDORA_BOX_SLOT.equals(
                context.identifier()
        )) {
            return modifiers;
        }

        if (!isActivated(stack)) {
            return modifiers;
        }

        CuriosApi.addSlotModifier(
                modifiers,
                CURSE_SPIRIT_SLOT,
                slotUuid,
                8.0D,
                AttributeModifier.Operation.ADDITION
        );

        return modifiers;
    }

    private boolean installCurses(
            ServerPlayer player,
            ItemStack box
    ) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> {

                    var optional =
                            handler.getStacksHandler(
                                    CURSE_SPIRIT_SLOT
                            );

                    if (optional.isEmpty()) {
                        return false;
                    }

                    int slots =
                            optional.get()
                                    .getStacks()
                                    .getSlots();

                    if (slots < 8) {
                        return false;
                    }

                    List<Supplier<? extends Item>> order =
                            curseOrder();

                    ListTag stored =
                            box.getOrCreateTag()
                                    .getList(
                                            STORED_CURSES_TAG,
                                            Tag.TAG_COMPOUND
                                    );

                    for (int i = 0; i < 8; i++) {

                        ItemStack current =
                                optional.get()
                                        .getStacks()
                                        .getStackInSlot(i);

                        if (!current.isEmpty()) {
                            continue;
                        }

                        ItemStack curse =
                                ItemStack.EMPTY;

                        if (stored.size() > i) {
                            curse =
                                    ItemStack.of(
                                            stored.getCompound(i)
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

                    return true;
                })
                .orElse(false);
    }

    private void saveAndClearCurses(
            ServerPlayer player,
            ItemStack box
    ) {
        CuriosApi.getCuriosInventory(player)
                .ifPresent(handler -> {

                    var optional =
                            handler.getStacksHandler(
                                    CURSE_SPIRIT_SLOT
                            );

                    if (optional.isEmpty()) {
                        return;
                    }

                    int slots =
                            optional.get()
                                    .getStacks()
                                    .getSlots();

                    if (slots < 8) {
                        return;
                    }

                    ListTag stored =
                            new ListTag();

                    for (int i = 0; i < 8; i++) {

                        ItemStack curse =
                                optional.get()
                                        .getStacks()
                                        .getStackInSlot(i)
                                        .copy();

                        CompoundTag saved =
                                new CompoundTag();

                        if (!curse.isEmpty()) {
                            curse.save(saved);
                        }

                        stored.add(saved);

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

    private static String EternalCareerName() {
        return "eternal_career:pandora_box_unlock";
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