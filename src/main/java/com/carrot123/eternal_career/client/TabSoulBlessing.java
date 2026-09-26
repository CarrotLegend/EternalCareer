package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.SoulBlessingRequestPacket;
import dev.xkmc.l2tabs.tabs.core.BaseTab;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class TabSoulBlessing extends BaseTab<TabSoulBlessing> {

    public TabSoulBlessing(
            TabToken<TabSoulBlessing> token,
            TabManager manager,
            ItemStack stack,
            Component title
    ) {
        super(token, manager, stack, title);
    }

    @Override
    public void onTabClicked() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        if (!minecraft.player.containerMenu.getCarried().isEmpty()) {
            return;
        }

        if (manager.selected == token) {
            return;
        }

        ModNetwork.sendToServer(
                new SoulBlessingRequestPacket(true)
        );
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);

        if (visible) {
            graphics.blit(
                    SoulBlessingTabs.BLESSING_ICON,
                    getX() + 5,
                    getY() + 9,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16
            );
        }
    }
}