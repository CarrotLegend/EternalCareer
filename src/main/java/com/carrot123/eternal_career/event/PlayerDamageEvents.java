package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.career.api.DamageCategoryHelper;
import com.carrot123.eternal_career.career.api.PlayerDamageCategory;
import com.carrot123.eternal_career.career.api.RangeDamageHelper;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.effect.IngredientMarkEffect;
import com.carrot123.eternal_career.item.FistBladeItem;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.registry.ModEffects;
import com.carrot123.eternal_career.soul.ScytheCombat;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerDamageEvents {
    public static final double INGREDIENT_MARK_KITCHENWARE_MULTIPLIER = 1.25D;
    private PlayerDamageEvents() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        DamageSource source = event.getSource();
        Player directPlayer = source.getEntity() instanceof Player player ? player : null;
        Player attributedPlayer = directPlayer != null ? directPlayer : findPlayer(source.getEntity(), 0);
        if (attributedPlayer == null) attributedPlayer = findPlayer(source.getDirectEntity(), 0);
        if (attributedPlayer == null) return;
        double amount = event.getAmount();
        if (amount <= 0.0D) return;
        boolean wasIngredientMarked = directPlayer != null && event.getEntity().hasEffect(ModEffects.INGREDIENT_MARK.get());
        if (directPlayer != null) {
            PlayerDamageCategory category = DamageCategoryHelper.classify(directPlayer, source);
            Attribute categoryAttribute = category == PlayerDamageCategory.KITCHENWARE ? ModAttributes.KITCHENWARE_DAMAGE.get() : ModAttributes.NON_KITCHENWARE_DAMAGE.get();
            amount *= getMultiplier(directPlayer, categoryAttribute);
            if (ScytheCombat.directAttacker(source) == null) amount *= getMultiplier(directPlayer, ModAttributes.NON_SCYTHE_DAMAGE.get());
            if (RangeDamageHelper.isNonRanged(source)) amount *= getMultiplier(directPlayer, ModAttributes.NON_RANGED_DAMAGE.get());
            if (category == PlayerDamageCategory.KITCHENWARE && wasIngredientMarked) amount *= INGREDIENT_MARK_KITCHENWARE_MULTIPLIER;
        }
        boolean fistBladeAttack = directPlayer != null
                && source.getDirectEntity() == directPlayer
                && source.is(DamageTypes.PLAYER_ATTACK)
                && directPlayer.getMainHandItem().getItem() instanceof FistBladeItem;
        amount *= getMultiplier(attributedPlayer, fistBladeAttack
                ? ModAttributes.FIST_BLADE_DAMAGE.get() : ModAttributes.NON_FIST_BLADE_DAMAGE.get());
        if (!Double.isFinite(amount)) amount = Float.MAX_VALUE;
        amount = Math.max(0.0D, Math.min(Float.MAX_VALUE, amount));
        event.setAmount((float) amount);
        if (directPlayer != null && amount > 0.0D && CurioEquipmentHelper.hasGodsRecognition(directPlayer)) event.getEntity().addEffect(
            new MobEffectInstance(ModEffects.INGREDIENT_MARK.get(), IngredientMarkEffect.INGREDIENT_MARK_DURATION_TICKS, 0, false, true, true), directPlayer);
    }

    private static Player findPlayer(Entity entity, int depth) {
        if (entity == null || depth >= 8) return null;
        if (entity instanceof Player player) return player;
        if (entity instanceof TraceableEntity traceable) {
            Entity owner = traceable.getOwner();
            if (owner != null && owner != entity) {
                Player player = findPlayer(owner, depth + 1);
                if (player != null) return player;
            }
        }
        if (entity instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner != null && owner != entity) return findPlayer(owner, depth + 1);
        }
        return null;
    }

    private static double getMultiplier(Player player, Attribute attribute) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return 1.0D;
        double value = instance.getValue();
        return Double.isFinite(value) ? Math.max(0.0D, value) : 1.0D;
    }
}
