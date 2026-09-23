package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.career.capability.soul.SoulCapability;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.item.SoulTankItem;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.soul.ReaperSkillManager;
import com.carrot123.eternal_career.soul.ScytheCombat;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class ScytheDamageEvents {

    private ScytheDamageEvents() {
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide
                || event.getAmount() <= 0) {
            return;
        }

        var player =
                ScytheCombat.directAttacker(
                        event.getSource()
                );

        if (player == null) {
            return;
        }

        double multiplier =
                player.getAttributeValue(
                        ModAttributes.SCYTHE_DAMAGE.get()
                );

        if (CurioEquipmentHelper.hasSoulTank(player)) {
            int soul =
                    player.getCapability(
                                    SoulCapability.SOUL
                            )
                            .map(value -> value.getSoul())
                            .orElse(0);

            multiplier +=
                    SoulTankItem.getScytheDamageBonus(soul);
        }

        float damage =
                ScytheCombat.scaledDamage(
                        event.getAmount(),
                        multiplier
                );

        if (ReaperSkillManager.isActive(player)) {
            damage =
                    ReaperSkillManager
                            .boostScytheDamage(damage);
        }

        event.setAmount(damage);
    }
}