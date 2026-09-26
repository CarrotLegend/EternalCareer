package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModMenus;
import com.carrot123.eternal_career.soulblessing.SoulBlessingMenu;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlot;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlots;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Fully independent 42-slot page; the vanilla inventory background is never rendered. */
public final class SoulBlessingScreen extends AbstractContainerScreen<SoulBlessingMenu> {
    private static final int PANEL = 0xFFC6C6C6;
    private static final int LIGHT = 0xFFFFFFFF;
    private static final int DARK = 0xFF555555;
    private static final int INNER = 0xFFADADAD;

    public SoulBlessingScreen(SoulBlessingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 194;
        imageHeight = 210;
        inventoryLabelY = 114;
    }

    @Override
    protected void init() {
        super.init();
        SoulBlessingTabs.addToBlessing(this);
    }

    void addTab(AbstractWidget tab) { addRenderableWidget(tab); }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && SoulBlessingTabs.hasL2Tabs()
                && L2SoulBlessingTabs.interceptClick(this, mouseX, mouseY)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        bevel(graphics, x, y, x + imageWidth, y + imageHeight, PANEL);
        // Separate equipment panel and vanilla-style player inventory wells.
        inset(graphics, x + 7, y + 26, x + 187, y + 116, 0xFFB8B8B8);
        inset(graphics, x + 9, y + 122, x + 185, y + 180, PANEL);
        inset(graphics, x + 9, y + 182, x + 185, y + 205, PANEL);
        mannequin(graphics, x + 97, y + 53);
        for (int index = 0; index < menu.slots.size(); index++) {
            var slot = menu.slots.get(index);
            slotWell(graphics, x + slot.x, y + slot.y);
            if (index < SoulBlessingSlots.COUNT && !slot.hasItem()) {
                ghost(graphics, x + slot.x, y + slot.y, index);
            }
        }
    }

    private static void bevel(GuiGraphics graphics, int x1, int y1, int x2, int y2, int fill) {
        graphics.fill(x1, y1, x2, y2, fill);
        graphics.fill(x1, y1, x2, y1 + 2, LIGHT);
        graphics.fill(x1, y1, x1 + 2, y2, LIGHT);
        graphics.fill(x1, y2 - 2, x2, y2, DARK);
        graphics.fill(x2 - 2, y1, x2, y2, DARK);
    }

    private static void inset(GuiGraphics graphics, int x1, int y1, int x2, int y2, int fill) {
        graphics.fill(x1, y1, x2, y2, fill);
        graphics.fill(x1, y1, x2, y1 + 1, DARK);
        graphics.fill(x1, y1, x1 + 1, y2, DARK);
        graphics.fill(x1, y2 - 1, x2, y2, LIGHT);
        graphics.fill(x2 - 1, y1, x2, y2, LIGHT);
    }

    private static void slotWell(GuiGraphics graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF373737);
        graphics.fill(x, y, x + 16, y + 16, 0xFF8B8B8B);
        graphics.fill(x + 1, y + 1, x + 16, y + 16, 0xFF999999);
        graphics.fill(x - 1, y + 16, x + 17, y + 17, LIGHT);
        graphics.fill(x + 16, y - 1, x + 17, y + 17, LIGHT);
    }

    private static void mannequin(GuiGraphics g, int cx, int y) {
        // 8:12:4:12 Minecraft body proportions, rendered as shaded pixel blocks.
        body(g, cx - 7, y, 14, 14, 0xFF616161, 0xFF777777);
        body(g, cx - 7, y + 16, 14, 20, 0xFF686868, 0xFF858585);
        body(g, cx - 15, y + 17, 6, 20, 0xFF595959, 0xFF777777);
        body(g, cx + 9, y + 17, 6, 20, 0xFF595959, 0xFF777777);
        body(g, cx - 7, y + 37, 6, 20, 0xFF595959, 0xFF797979);
        body(g, cx + 1, y + 37, 6, 20, 0xFF595959, 0xFF797979);
        g.fill(cx - 4, y + 5, cx - 2, y + 7, 0xFF383838);
        g.fill(cx + 2, y + 5, cx + 4, y + 7, 0xFF383838);
        g.fill(cx - 1, y + 22, cx + 1, y + 29, 0xFF9BB3AF);
        g.fill(cx - 4, y + 24, cx + 4, y + 26, 0xFF9BB3AF);
        g.fill(cx - 13, y + 34, cx - 10, y + 36, 0xFF9B9B9B);
        g.fill(cx + 10, y + 34, cx + 13, y + 36, 0xFF9B9B9B);
    }

    private static void body(GuiGraphics g, int x, int y, int w, int h, int edge, int fill) {
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF393939);
        g.fill(x, y, x + w, y + h, edge);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, fill);
    }

    private static void ghost(GuiGraphics g, int x, int y, int index) {
        int c = 0xFFBDBDBD;
        switch (index) {
            case 0 -> { // Square head.
                g.fill(x + 5, y + 4, x + 11, y + 5, c);
                g.fill(x + 4, y + 5, x + 5, y + 11, c);
                g.fill(x + 11, y + 5, x + 12, y + 11, c);
                g.fill(x + 5, y + 11, x + 11, y + 12, c);
            }
            case 1 -> { // Necklace chain and pendant.
                g.fill(x + 3, y + 4, x + 5, y + 7, c);
                g.fill(x + 11, y + 4, x + 13, y + 7, c);
                g.fill(x + 5, y + 7, x + 7, y + 10, c);
                g.fill(x + 9, y + 7, x + 11, y + 10, c);
                g.fill(x + 7, y + 10, x + 9, y + 13, c);
            }
            case 2 -> { // Glove.
                g.fill(x + 5, y + 3, x + 7, y + 9, c);
                g.fill(x + 8, y + 4, x + 10, y + 10, c);
                g.fill(x + 4, y + 8, x + 12, y + 12, c);
            }
            case 3 -> { // Ring.
                g.fill(x + 6, y + 3, x + 10, y + 5, c);
                g.fill(x + 4, y + 6, x + 6, y + 11, c);
                g.fill(x + 10, y + 6, x + 12, y + 11, c);
                g.fill(x + 6, y + 11, x + 10, y + 13, c);
            }
            default -> { // Boot.
                g.fill(x + 5, y + 3, x + 9, y + 10, c);
                g.fill(x + 5, y + 10, x + 12, y + 13, c);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        Component name = Component.translatable("soul_blessing.eternal_career.title");
        graphics.drawString(font, name, (imageWidth - font.width(name)) / 2, 12, 0xFF404040, false);
        graphics.drawString(font, playerInventoryTitle, 12, inventoryLabelY, 0xFF404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        if (menu.getCarried().isEmpty() && hoveredSlot instanceof SoulBlessingSlot slot
                && !slot.hasItem()) {
            graphics.renderTooltip(font, Component.translatable(
                    SoulBlessingSlots.type(slot.getSlotIndex()).translationKey()), mouseX, mouseY);
        }
    }

    @Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        private Registration() {}

        @SubscribeEvent
        public static void register(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenus.SOUL_BLESSING.get(), SoulBlessingScreen::new);
                if (SoulBlessingTabs.hasL2Tabs()) L2SoulBlessingTabs.register();
            });
        }
    }
}
