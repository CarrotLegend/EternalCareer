package com.carrot123.eternal_career.mixin.compat.goety_revelation;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

/** Suppresses the blessing scroll's direct Curios attributes without the ring. */
@Pseudo
@Mixin(
        targets = "com.mega.revelationfix.common.item.curios.enigmtic_legacy.BlessingScroll",
        remap = false)
public abstract class BlessingScrollAttributesMixin {
    @Inject(
            method = "getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;"
                    + "Ljava/util/UUID;Lnet/minecraft/world/item/ItemStack;)"
                    + "Lcom/google/common/collect/Multimap;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private void eternalCareer$guardAttributes(
            SlotContext slotContext,
            UUID slotUuid,
            ItemStack stack,
            CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback) {
        if (slotContext.entity() instanceof Player player
                && !RedemptionAccessController.canEquip(player, stack)) {
            callback.setReturnValue(ImmutableMultimap.of());
        }
    }
}
