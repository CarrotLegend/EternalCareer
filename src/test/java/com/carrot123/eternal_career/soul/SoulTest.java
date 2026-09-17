package com.carrot123.eternal_career.soul;

import com.carrot123.eternal_career.career.capability.soul.Soul;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class SoulTest {
    @Test void damagePercentages() {
        assertEquals(20F, ScytheCombat.scaledDamage(20, 0));
        assertEquals(25F, ScytheCombat.scaledDamage(20, .25));
        assertEquals(30F, ScytheCombat.scaledDamage(20, .5));
        assertEquals(0F, ScytheCombat.scaledDamage(20, -1));
        assertEquals(Float.MAX_VALUE, ScytheCombat.scaledDamage(Float.MAX_VALUE, 1024));
    }
    @Test void capacityDoesNotOwnTheBalance() {
        Soul soul = new Soul(() -> {});
        soul.setSoul(800);
        soul.addSoul(10, 500);
        assertEquals(800, soul.getSoul());
        soul.addSoul(10, 2000);
        assertEquals(810, soul.getSoul());
    }
    @Test void gainClampsWithoutOverflow() {
        Soul soul = new Soul(() -> {});
        soul.setSoul(495);
        soul.addSoul(10, 500);
        assertEquals(500, soul.getSoul());
        soul.setSoul(Integer.MAX_VALUE - 1);
        soul.addSoul(1000, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, soul.getSoul());
    }
    @Test void consumesExactlyOnceAndOnlyWithFunds() {
        Soul soul = new Soul(() -> {});
        soul.setSoul(99);
        assertFalse(soul.consumeSoul(100));
        assertEquals(99, soul.getSoul());
        soul.setSoul(100);
        assertTrue(soul.consumeSoul(100));
        assertFalse(soul.consumeSoul(100));
        assertEquals(0, soul.getSoul());
    }
    @Test void noInvalidGainOrConsumption() {
        Soul soul = new Soul(() -> {});
        soul.setSoul(300);
        soul.addSoul(-10, 500);
        assertFalse(soul.consumeSoul(-100));
        assertFalse(soul.consumeSoul(0));
        assertEquals(300, soul.getSoul());
        soul.setSoul(-10);
        assertEquals(0, soul.getSoul());
    }
    @Test void persistenceDoesNotClampToEquipmentOrSendChanges() {
        AtomicInteger changed = new AtomicInteger();
        Soul soul = new Soul(changed::incrementAndGet);
        soul.setSoul(800);
        Soul restored = new Soul(changed::incrementAndGet);
        restored.deserializeNBT(soul.serializeNBT());
        assertEquals(800, restored.getSoul());
        assertEquals(1, changed.get());
        restored.setSoul(800);
        assertEquals(1, changed.get());
        restored.consumeSoul(100);
        assertEquals(2, changed.get());
    }
    @Test void missingAndNegativeNbtAreSafe() {
        Soul soul = new Soul(() -> {});
        soul.deserializeNBT(new CompoundTag());
        assertEquals(0, soul.getSoul());
        CompoundTag bad = new CompoundTag();
        bad.putInt("soul", -50);
        soul.deserializeNBT(bad);
        assertEquals(0, soul.getSoul());
    }
    @ParameterizedTest @CsvSource({"20,1", "99,1", "100,1", "199,1", "200,2", "1000,10", "100000,1000", "200000,1000"})
    void rewardUsesMaximumHealth(float maxHealth, int expected) {
        assertEquals(expected, ScytheCombat.reward(maxHealth));
    }
}
