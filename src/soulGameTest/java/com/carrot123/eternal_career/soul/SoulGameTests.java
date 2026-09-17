package com.carrot123.eternal_career.soul;

import com.carrot123.eternal_career.career.capability.soul.ISoul;
import com.carrot123.eternal_career.career.capability.soul.SoulCapability;
import com.carrot123.eternal_career.event.ScytheDamageEvents;
import com.carrot123.eternal_career.event.SoulCombatEvents;
import com.carrot123.eternal_career.event.SoulLifecycleEvents;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.registry.ModTags;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("eternal_career")
@PrefixGameTestTemplate(false)
public final class SoulGameTests {
    private static ServerPlayer player(GameTestHelper h) { return h.makeMockServerPlayerInLevel(); }
    private static ISoul soul(ServerPlayer p) { return p.getCapability(SoulCapability.SOUL).orElseThrow(() -> new AssertionError("Missing Soul capability")); }
    private static void armor(ServerPlayer p) {
        p.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        p.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
        p.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
        p.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
    }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

    @GameTest(template = "soul_empty")
    public static void tagEquipmentAndAttributes(GameTestHelper h) {
        ServerPlayer p = player(h);
        check(p.getAttribute(ModAttributes.SCYTHE_DAMAGE.get()) != null, "Attribute absent on player");
        check(p.getAttributeValue(ModAttributes.SCYTHE_DAMAGE.get()) == 0, "Incorrect attribute default");
        armor(p);
        check(SoulSetManager.findActiveSoulSet(p).orElseThrow().maxSoul() == 500, "Full set inactive");
        soul(p).setSoul(800);
        p.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        check(SoulSetManager.findActiveSoulSet(p).isEmpty(), "Missing piece activated");
        check(soul(p).getSoul() == 800, "Unequip deleted souls");
        p.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        check(SoulSetManager.findActiveSoulSet(p).isEmpty(), "Mixed set activated");
        armor(p);
        check(soul(p).getSoul() == 800, "Reequip clamped souls");
        var scythe = new ItemStack(Items.IRON_HOE);
        check(scythe.is(ModTags.Items.SCYTHES), "Fixture scythe tag missing");
        check(scythe.is(ItemTags.create(new ResourceLocation("goety", "grave_glove_boost"))), "Goety nested tag missing");
        h.succeed();
    }
    @GameTest(template = "soul_empty")
    public static void damageAndReward(GameTestHelper h) {
        ServerPlayer p = player(h); armor(p);
        p.getAttribute(ModAttributes.SCYTHE_DAMAGE.get()).setBaseValue(.5);
        Zombie target = new Zombie(EntityType.ZOMBIE, h.getLevel());
        p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_HOE));
        var hurt = new LivingHurtEvent(target, p.damageSources().playerAttack(p), 20);
        ScytheDamageEvents.onHurt(hurt);
        check(hurt.getAmount() == 30, "Scythe damage did not multiply");
        var magic = new LivingHurtEvent(target, p.damageSources().indirectMagic(p, p), 20);
        ScytheDamageEvents.onHurt(magic);
        check(magic.getAmount() == 20, "Magic incorrectly amplified");
        p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        var sword = new LivingHurtEvent(target, p.damageSources().playerAttack(p), 20);
        ScytheDamageEvents.onHurt(sword);
        check(sword.getAmount() == 20, "Sword incorrectly amplified");
        p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_HOE));
        target.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(1000);
        var drops = new LivingDropsEvent(target, p.damageSources().playerAttack(p), new ArrayList<>(), 0, true);
        SoulCombatEvents.reward(drops);
        check(soul(p).getSoul() == 10, "Wrong kill reward");
        SoulCombatEvents.reward(drops);
        check(soul(p).getSoul() == 10, "Duplicate reward");
        h.succeed();
    }
    @GameTest(template = "soul_empty")
    public static void revivalAndClone(GameTestHelper h) {
        ServerPlayer p = player(h); armor(p); soul(p).setSoul(100);
        p.setHealth(0);
        var death = new LivingDeathEvent(p, p.damageSources().generic());
        SoulCombatEvents.revive(death);
        check(death.isCanceled() && p.getHealth() == 1 && soul(p).getSoul() == 0, "Revival failed");
        SoulCombatEvents.revive(death);
        check(soul(p).getSoul() == 0, "Duplicate revival charge");
        soul(p).setSoul(99);
        var poor = new LivingDeathEvent(p, p.damageSources().generic());
        SoulCombatEvents.revive(poor);
        check(!poor.isCanceled() && soul(p).getSoul() == 99, "Insufficient balance revived");
        soul(p).setSoul(300);
        var canceled = new LivingDeathEvent(p, p.damageSources().generic()); canceled.setCanceled(true);
        SoulCombatEvents.revive(canceled);
        check(soul(p).getSoul() == 300, "Canceled death charged");
        var kill = new LivingDeathEvent(p, p.damageSources().genericKill());
        SoulCombatEvents.revive(kill);
        check(!kill.isCanceled() && soul(p).getSoul() == 300, "Bypass damage revived");
        ServerPlayer clone = player(h);
        SoulLifecycleEvents.clonePlayer(new PlayerEvent.Clone(clone, p, true));
        check(soul(clone).getSoul() == 300, "Death clone lost souls");
        ServerPlayer endClone = player(h);
        SoulLifecycleEvents.clonePlayer(new PlayerEvent.Clone(endClone, clone, false));
        check(soul(endClone).getSoul() == 300, "Non-death clone lost souls");
        h.succeed();
    }
    @GameTest(template = "soul_empty")
    public static void definitionsAndOverlap(GameTestHelper h) {
        var a = SoulSetDefinition.parse(new ResourceLocation("test", "a"), JsonParser.parseString(
                "{\"armor_tag\":\"until_eternity:soul_sets/test_iron\",\"max_soul\":500}"));
        var b = new SoulSetDefinition(new ResourceLocation("test", "b"), a.armorTag(), 2000);
        var c = new SoulSetDefinition(new ResourceLocation("test", "c"), a.armorTag(), 2000);
        var tags = Set.of(ModTags.Items.SOUL_ARMOR, a.armorTag());
        check(SoulSetManager.selectSet(List.of(c, a, b), List.of(tags, tags, tags, tags)).orElseThrow().equals(b), "Overlap selection unstable");
        check(SoulSetManager.selectSet(List.of(a), List.of(tags, tags, tags, Set.of())).isEmpty(), "Missing slot accepted");
        check(SoulSetManager.selectSet(List.of(), List.of(tags, tags, tags, tags)).isEmpty(), "Deleted definition still active");
        for (String capacity : List.of("0", "-1", "0.5", "2147483648", "\"500\"", "null")) {
            boolean rejected = false;
            try { SoulSetDefinition.parse(a.id(), JsonParser.parseString(
                    "{\"armor_tag\":\"until_eternity:soul_sets/test_iron\",\"max_soul\":" + capacity + "}")); }
            catch (RuntimeException expected) { rejected = true; }
            check(rejected, "Accepted invalid capacity: " + capacity);
        }
        h.succeed();
    }
}
