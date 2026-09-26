package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.SoulBlessingRequestPacket;
import dev.xkmc.l2tabs.tabs.contents.TabInventory;
import dev.xkmc.l2tabs.tabs.core.BaseTab;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabRegistry;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class L2SoulBlessingTabs {
    private static TabToken<BlessingTab> token;

    private L2SoulBlessingTabs() {}

    static void register() {
        if (token == null) {
            token = TabRegistry.registerTab(4000, BlessingTab::new,
                    () -> Items.AIR, Component.translatable("soul_blessing.eternal_career.title"));
        }
    }

    static void addToBlessing(SoulBlessingScreen screen) {
        if (token == null) register();
        new TabManager(screen).init(screen::addTab, token);
    }

    static boolean interceptClick(SoulBlessingScreen screen, double mouseX, double mouseY) {
        for (var child : screen.children()) {
            if (child instanceof BaseTab<?> tab && tab.visible && tab.isMouseOver(mouseX, mouseY)) {
                if (!screen.getMenu().getCarried().isEmpty()) return true;
                if (tab instanceof TabInventory) {
                    ModNetwork.sendToServer(new SoulBlessingRequestPacket(false));
                    return true;
                }
            }
        }
        return false;
    }

    private static final class BlessingTab extends BaseTab<BlessingTab> {
        private BlessingTab(TabToken<BlessingTab> token, TabManager manager,
                            ItemStack stack, Component title) {
            super(token, manager, stack, title);
        }

        @Override
        public void onTabClicked() {
            Minecraft minecraft = Minecraft.getInstance();
            if (manager.selected != token && minecraft.player != null
                    && minecraft.player.containerMenu.getCarried().isEmpty()) {
                ModNetwork.sendToServer(new SoulBlessingRequestPacket(true));
            }
        }

        @Override
        public void renderBackground(GuiGraphics graphics) {
            super.renderBackground(graphics);
            if (visible) {
                graphics.blit(SoulBlessingTabs.BLESSING_ICON, getX() + 5, getY() + 9,
                        0, 0, 16, 16, 16, 16);
            }
        }
    }
}
