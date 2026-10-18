package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.SoulBlessingRequestPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class SoulBlessingTabs {

    private static final ResourceLocation BLESSING_ICON =
            new ResourceLocation(
                    EternalCareer.MOD_ID,
                    "textures/gui/soul_blessing_tab.png"
            );

    private static final int TAB_WIDTH = 26;
    private static final int TAB_HEIGHT = 26;
    private static final int TAB_Y_OFFSET = 7;

    private SoulBlessingTabs() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) {
            return;
        }

        event.addListener(new NavigationButton(
                screen.getGuiLeft() - TAB_WIDTH,
                screen.getGuiTop() + TAB_Y_OFFSET,
                false,
                SoulBlessingTabs::openBlessing
        ));
    }

    public static void addToBlessing(SoulBlessingScreen screen) {
        screen.addTab(new NavigationButton(
                screen.getGuiLeft() - TAB_WIDTH,
                screen.getGuiTop() + TAB_Y_OFFSET,
                true,
                SoulBlessingTabs::returnToInventory
        ));
    }

    private static void openBlessing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || !(minecraft.screen instanceof InventoryScreen)) {
            return;
        }

        ModNetwork.sendToServer(
                new SoulBlessingRequestPacket(true)
        );
    }

    private static void returnToInventory() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || !(minecraft.screen instanceof SoulBlessingScreen)) {
            return;
        }

        SoulBlessingClientState.requestInventoryReturn();

        ModNetwork.sendToServer(
                new SoulBlessingRequestPacket(false)
        );
    }

    private static final class NavigationButton extends AbstractButton {

        private final boolean inventoryButton;
        private final Runnable action;

        private NavigationButton(
                int x,
                int y,
                boolean inventoryButton,
                Runnable action
        ) {
            super(
                    x,
                    y,
                    TAB_WIDTH,
                    TAB_HEIGHT,
                    Component.translatable(
                            inventoryButton
                                    ? "container.inventory"
                                    : "soul_blessing.eternal_career.title"
                    )
            );

            this.inventoryButton = inventoryButton;
            this.action = action;
            setTooltip(Tooltip.create(getMessage()));
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void renderWidget(
                GuiGraphics graphics,
                int mouseX,
                int mouseY,
                float partialTick
        ) {
            int x = getX();
            int y = getY();

            int border = isHoveredOrFocused()
                    ? 0xFFFFFFFF
                    : 0xFF373737;

            int background = isHoveredOrFocused()
                    ? 0xFFE0E0E0
                    : 0xFFC6C6C6;

            graphics.fill(
                    x,
                    y,
                    x + TAB_WIDTH,
                    y + TAB_HEIGHT,
                    border
            );

            graphics.fill(
                    x + 1,
                    y + 1,
                    x + TAB_WIDTH - 1,
                    y + TAB_HEIGHT - 1,
                    background
            );

            graphics.fill(
                    x + 2,
                    y + 2,
                    x + TAB_WIDTH - 2,
                    y + 3,
                    0xFFFFFFFF
            );

            graphics.fill(
                    x + 2,
                    y + TAB_HEIGHT - 3,
                    x + TAB_WIDTH - 2,
                    y + TAB_HEIGHT - 2,
                    0xFF777777
            );

            if (inventoryButton) {
                graphics.renderItem(
                        Items.CHEST.getDefaultInstance(),
                        x + 5,
                        y + 5
                );
            } else {
                graphics.blit(
                        BLESSING_ICON,
                        x + 5,
                        y + 5,
                        0,
                        0,
                        16,
                        16,
                        16,
                        16
                );
            }
        }

        @Override
        protected void updateWidgetNarration(
                NarrationElementOutput output
        ) {
            defaultButtonNarrationText(output);
        }
    }
}
