package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.witch.WitchHeartMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class WitchHeartScreen extends AbstractContainerScreen<WitchHeartMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public WitchHeartScreen(WitchHeartMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 132;
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 38;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, 176, 35);
        // The vanilla nine-slot row is covered; only the real storage slot is shown.
        graphics.fill(leftPos + 7, topPos + 17, leftPos + 169, topPos + 35, 0xFFC6C6C6);
        graphics.blit(TEXTURE, leftPos + 79, topPos + 17, 7, 17, 18, 18);
        graphics.blit(TEXTURE, leftPos, topPos + 35, 0, 126, 176, 96);
    }
}
