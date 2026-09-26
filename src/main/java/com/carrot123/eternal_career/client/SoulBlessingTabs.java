package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.SoulBlessingRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/** Fallback tabs only. L2Tabs installations use its own registry and manager. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, value = Dist.CLIENT)
public final class SoulBlessingTabs {
    static final ResourceLocation BLESSING_ICON = new ResourceLocation(
            EternalCareer.MOD_ID, "textures/gui/soul_blessing_tab.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation(
            "textures/gui/container/creative_inventory/tabs.png");
    private static final int WIDTH = 26;
    private static final int HEIGHT = 32;
    private static final int GAP = 2;

    private SoulBlessingTabs() {}

    static boolean hasL2Tabs() {
        return ModList.get().isLoaded("l2tabs");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void addToInventory(ScreenEvent.Init.Post event) {
        if (hasL2Tabs() || event.getScreen().getClass() != InventoryScreen.class
                || Minecraft.getInstance().player == null) return;
        AbstractContainerScreen<?> container = (AbstractContainerScreen<?>) event.getScreen();
        Tab inventory = new Tab(false, true);
        Tab blessing = new Tab(true, false);
        event.addListener(inventory);
        event.addListener(blessing);
        layout(event.getScreen(), container, inventory, blessing);
    }

    public static void addToBlessing(SoulBlessingScreen screen) {
        if (hasL2Tabs()) {
            L2SoulBlessingTabs.addToBlessing(screen);
            return;
        }
        Tab inventory = new Tab(false, false);
        Tab blessing = new Tab(true, true);
        screen.addTab(inventory);
        screen.addTab(blessing);
        layout(screen, screen, inventory, blessing);
    }

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Pre event) {
        if (hasL2Tabs() || !(event.getScreen() instanceof AbstractContainerScreen<?> container)) return;
        relayout(event.getScreen(), container);
    }

    private static void relayout(Screen screen, AbstractContainerScreen<?> container) {
        Tab inventory = null;
        Tab blessing = null;
        for (var child : screen.children()) {
            if (child instanceof Tab tab) {
                if (tab.blessing) blessing = tab;
                else inventory = tab;
            }
        }
        if (inventory != null && blessing != null) layout(screen, container, inventory, blessing);
    }

    private static void layout(Screen screen, AbstractContainerScreen<?> container,
                               Tab inventory, Tab blessing) {
        int left = container.getGuiLeft();
        int top = container.getGuiTop();
        int y = top - 28;
        int x = left;
        for (var child : screen.children()) {
            if (!(child instanceof AbstractWidget widget) || child == inventory || child == blessing
                    || !widget.visible) continue;
            // Only actual widgets in the top tab strip affect placement. Curios' side button does not.
            if (widget.getY() < top && widget.getY() + widget.getHeight() > y
                    && widget.getX() < left + container.getXSize()
                    && widget.getX() + widget.getWidth() > left) {
                x = Math.max(x, widget.getX() + widget.getWidth() + GAP);
            }
        }
        boolean fits = y >= 0 && x + WIDTH * 2 + GAP <= Math.min(screen.width,
                left + container.getXSize());
        inventory.visible = blessing.visible = fits;
        if (!fits) return;
        inventory.setX(x);
        inventory.setY(y);
        blessing.setX(x + WIDTH + GAP);
        blessing.setY(y);
    }

    private static final class Tab extends AbstractWidget {
        private final boolean blessing;
        private final boolean selected;

        private Tab(boolean blessing, boolean selected) {
            super(0, 0, WIDTH, HEIGHT, Component.translatable(blessing
                    ? "soul_blessing.eternal_career.title"
                    : "soul_blessing.eternal_career.back"));
            this.blessing = blessing;
            this.selected = selected;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null) return;
            active = !selected && minecraft.player.containerMenu.getCarried().isEmpty();
            graphics.blit(TAB_TEXTURE, getX(), getY(), blessing ? 26 : 0,
                    selected ? 32 : 0, WIDTH, HEIGHT);
            if (blessing) {
                graphics.blit(BLESSING_ICON, getX() + 5, getY() + 9, 0, 0,
                        16, 16, 16, 16);
            } else {
                graphics.renderItem(Items.CHEST.getDefaultInstance(), getX() + 5, getY() + 9);
            }
            if (isHovered) graphics.renderTooltip(minecraft.font, getMessage(), mouseX, mouseY);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (active) ModNetwork.sendToServer(new SoulBlessingRequestPacket(blessing));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
