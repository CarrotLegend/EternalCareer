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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class SoulBlessingScreen extends AbstractContainerScreen<SoulBlessingMenu> {

    private static final ResourceLocation MANNEQUIN_TEXTURE = new ResourceLocation(
            EternalCareer.MOD_ID,
            "textures/gui/soul_blessing_mannequin.png"
    );

    private static final ResourceLocation SLOT_ICON_TEXTURE = new ResourceLocation(
            EternalCareer.MOD_ID,
            "textures/gui/soul_blessing_slot_icons.png"
    );

    private static final int PANEL = 0xFFC6C6C6;
    private static final int LIGHT = 0xFFFFFFFF;
    private static final int DARK = 0xFF555555;

    public SoulBlessingScreen(
            SoulBlessingMenu menu,
            Inventory inventory,
            Component title
    ) {
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

    void addTab(AbstractWidget tab) {
        addRenderableWidget(tab);
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int x = leftPos;
        int y = topPos;

        bevel(graphics, x, y, x + imageWidth, y + imageHeight, PANEL);

        inset(graphics, x + 7, y + 26, x + 187, y + 116, 0xFFB8B8B8);
        inset(graphics, x + 9, y + 122, x + 185, y + 180, PANEL);
        inset(graphics, x + 9, y + 182, x + 185, y + 205, PANEL);

        graphics.blit(
                MANNEQUIN_TEXTURE,
                x + 73,
                y + 34,
                0,
                0,
                48,
                72,
                48,
                72
        );

        for (int index = 0; index < menu.slots.size(); index++) {
            var slot = menu.slots.get(index);

            slotWell(
                    graphics,
                    x + slot.x,
                    y + slot.y
            );

            if (index < SoulBlessingSlots.COUNT && !slot.hasItem()) {
                renderSlotIcon(
                        graphics,
                        x + slot.x,
                        y + slot.y,
                        index
                );
            }
        }
    }

    private static void renderSlotIcon(
            GuiGraphics graphics,
            int x,
            int y,
            int index
    ) {
        int icon;

        switch (index) {
            case 0 -> icon = 0;
            case 1 -> icon = 1;
            case 2 -> icon = 2;
            case 3 -> icon = 3;
            default -> icon = 4;
        }

        graphics.blit(
                SLOT_ICON_TEXTURE,
                x,
                y,
                icon * 16,
                0,
                16,
                16,
                80,
                16
        );
    }

    private static void bevel(
            GuiGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int fill
    ) {
        graphics.fill(x1, y1, x2, y2, fill);
        graphics.fill(x1, y1, x2, y1 + 2, LIGHT);
        graphics.fill(x1, y1, x1 + 2, y2, LIGHT);
        graphics.fill(x1, y2 - 2, x2, y2, DARK);
        graphics.fill(x2 - 2, y1, x2, y2, DARK);
    }

    private static void inset(
            GuiGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int fill
    ) {
        graphics.fill(x1, y1, x2, y2, fill);
        graphics.fill(x1, y1, x2, y1 + 1, DARK);
        graphics.fill(x1, y1, x1 + 1, y2, DARK);
        graphics.fill(x1, y2 - 1, x2, y2, LIGHT);
        graphics.fill(x2 - 1, y1, x2, y2, LIGHT);
    }

    private static void slotWell(
            GuiGraphics graphics,
            int x,
            int y
    ) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF373737);
        graphics.fill(x, y, x + 16, y + 16, 0xFF8B8B8B);
        graphics.fill(x + 1, y + 1, x + 16, y + 16, 0xFF999999);
        graphics.fill(x - 1, y + 16, x + 17, y + 17, LIGHT);
        graphics.fill(x + 16, y - 1, x + 17, y + 17, LIGHT);
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        Component name = Component.translatable(
                "soul_blessing.eternal_career.title"
        );

        graphics.drawString(
                font,
                name,
                (imageWidth - font.width(name)) / 2,
                12,
                0xFF404040,
                false
        );

        graphics.drawString(
                font,
                playerInventoryTitle,
                12,
                inventoryLabelY,
                0xFF404040,
                false
        );
    }

    @Override
    protected void renderTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        super.renderTooltip(graphics, mouseX, mouseY);

        if (!menu.getCarried().isEmpty()) {
            return;
        }

        if (!(hoveredSlot instanceof SoulBlessingSlot slot)) {
            return;
        }

        if (slot.hasItem()) {
            return;
        }

        graphics.renderTooltip(
                font,
                Component.translatable(
                        SoulBlessingSlots
                                .type(slot.getSlotIndex())
                                .translationKey()
                ),
                mouseX,
                mouseY
        );
    }

    @Mod.EventBusSubscriber(
            modid = EternalCareer.MOD_ID,
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static final class Registration {

        private Registration() {
        }

        @SubscribeEvent
        public static void register(FMLClientSetupEvent event) {
            event.enqueueWork(() ->
                    MenuScreens.register(
                            ModMenus.SOUL_BLESSING.get(),
                            SoulBlessingScreen::new
                    )
            );
        }
    }
}