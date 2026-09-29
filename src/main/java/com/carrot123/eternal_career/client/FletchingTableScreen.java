package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.fletching.BowModification;
import com.carrot123.eternal_career.fletching.BowModificationHelper;
import com.carrot123.eternal_career.fletching.BowModifications;
import com.carrot123.eternal_career.fletching.FletchingTableMenu;
import com.carrot123.eternal_career.network.FletchingCraftPacket;
import com.carrot123.eternal_career.network.FletchingModifyPacket;
import com.carrot123.eternal_career.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class FletchingTableScreen extends AbstractContainerScreen<FletchingTableMenu> {
    private static final ResourceLocation VANILLA_SLOTS =
            new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");
    private static final int PANEL = 0xFFC6C6C6;
    private static final int LIGHT = 0xFFFFFFFF;
    private static final int DARK = 0xFF555555;
    private static final int SLOT = 0xFF8B8B8B;
    private static final int LIST_X = 10;
    private static final int LIST_Y = 32;
    private static final int LIST_WIDTH = 93;
    private static final int LIST_HEIGHT = 78;
    private static final int ROW_HEIGHT = 24;
    private boolean craftPage = true;
    private int selected;
    private int scroll;
    private Button craftButton;
    private Button modifyButton;

    public FletchingTableScreen(FletchingTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 230;
        imageHeight = 248;
        inventoryLabelX = 34;
        inventoryLabelY = 151;
    }

    @Override
    protected void init() {
        super.init();
        craftButton = addRenderableWidget(Button.builder(
                Component.translatable("fletching.eternal_career.craft"),
                button -> ModNetwork.sendToServer(new FletchingCraftPacket(
                        menu.containerId, selectedModification().id())))
                .bounds(leftPos + 25, topPos + 115, 64, 20).build());
        modifyButton = addRenderableWidget(Button.builder(
                Component.translatable("fletching.eternal_career.modify"),
                button -> ModNetwork.sendToServer(new FletchingModifyPacket(menu.containerId)))
                .bounds(leftPos + 151, topPos + 126, 64, 20).build());
        refreshButtons();
    }

    private BowModification selectedModification() {
        return BowModifications.ALL.get(Mth.clamp(selected, 0, BowModifications.ALL.size() - 1));
    }

    private void refreshButtons() {
        if (craftButton != null) {
            craftButton.visible = craftPage;
            craftButton.active = craftPage && hasMaterials(selectedModification());
        }
        if (modifyButton != null) {
            modifyButton.visible = !craftPage;
            modifyButton.active = !craftPage && menu.canModify();
        }
    }

    private boolean hasMaterials(BowModification modification) {
        if (minecraft == null || minecraft.player == null) {
            return false;
        }
        for (BowModification.Material material : modification.materials()) {
            if (owned(material) < material.count()) {
                return false;
            }
        }
        return true;
    }

    private int owned(BowModification.Material material) {
        if (minecraft == null || minecraft.player == null) {
            return 0;
        }
        int count = 0;
        for (int index = 0; index < 36; index++) {
            ItemStack stack = minecraft.player.getInventory().getItem(index);
            if (stack.is(material.item())) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        refreshButtons();
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (craftPage) {
            int row = hoveredRow(mouseX, mouseY);
            if (row >= 0) {
                graphics.renderTooltip(font,
                        Component.translatable(BowModifications.ALL.get(row).nameKey()),
                        mouseX, mouseY);
            }
            BowModification modification = selectedModification();
            for (int index = 0; index < modification.materials().size(); index++) {
                int x = leftPos + 113;
                int y = topPos + 106 + index * 16;
                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    graphics.renderTooltip(font, modification.materials().get(index).icon(),
                            mouseX, mouseY);
                    break;
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, DARK);
        graphics.fill(x + 2, y + 2, x + imageWidth - 2, y + imageHeight - 2, PANEL);
        graphics.fill(x + 2, y + 2, x + imageWidth - 2, y + 3, LIGHT);
        graphics.fill(x + 2, y + 2, x + 3, y + imageHeight - 2, LIGHT);
        drawTab(graphics, x + 8, y - 20, 65,
                Component.translatable("fletching.eternal_career.tab.craft"), craftPage);
        drawTab(graphics, x + 76, y - 20, 65,
                Component.translatable("fletching.eternal_career.tab.modify"), !craftPage);
        if (craftPage) {
            renderCraftPage(graphics);
        } else {
            renderModifyPage(graphics);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(graphics, x + 34 + column * 18, y + 164 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(graphics, x + 34 + column * 18, y + 222);
        }
    }

    private void drawTab(GuiGraphics graphics, int x, int y, int width,
            Component label, boolean active) {
        graphics.fill(x, y, x + width, y + 22, DARK);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 22,
                active ? PANEL : SLOT);
        graphics.fill(x + 2, y + 2, x + width - 2, y + 3, LIGHT);
        graphics.drawCenteredString(font, label, x + width / 2, y + 8, 0x303030);
        if (active) {
            graphics.fill(x + 2, y + 20, x + width - 2, y + 23, PANEL);
        }
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.blit(VANILLA_SLOTS, x - 1, y - 1, 7, 17, 18, 18);
    }

    private void renderCraftPage(GuiGraphics graphics) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x + LIST_X - 2, y + LIST_Y - 2,
                x + LIST_X + LIST_WIDTH + 2, y + LIST_Y + LIST_HEIGHT + 2, DARK);
        graphics.fill(x + LIST_X, y + LIST_Y,
                x + LIST_X + LIST_WIDTH, y + LIST_Y + LIST_HEIGHT, 0xFF999999);
        graphics.enableScissor(x + LIST_X, y + LIST_Y,
                x + LIST_X + LIST_WIDTH, y + LIST_Y + LIST_HEIGHT);
        for (int index = scroll; index < BowModifications.ALL.size(); index++) {
            int rowY = y + LIST_Y + (index - scroll) * ROW_HEIGHT;
            if (rowY >= y + LIST_Y + LIST_HEIGHT) {
                break;
            }
            BowModification modification = BowModifications.ALL.get(index);
            if (index == selected) {
                graphics.fill(x + LIST_X + 1, rowY + 1,
                        x + LIST_X + LIST_WIDTH - 1, rowY + ROW_HEIGHT - 1, PANEL);
            }
            graphics.renderItem(modification.icon(), x + LIST_X + 3, rowY + 3);
            graphics.drawString(font, Component.translatable(modification.nameKey()),
                    x + LIST_X + 23, rowY + 8, 0x303030, false);
        }
        graphics.disableScissor();
        BowModification selectedEntry = selectedModification();
        graphics.pose().pushPose();
        graphics.pose().translate(x + 112, y + 30, 0);
        graphics.pose().scale(2.0F, 2.0F, 1.0F);
        graphics.renderItem(selectedEntry.icon(), 0, 0);
        graphics.pose().popPose();
        graphics.drawString(font, Component.translatable(selectedEntry.nameKey()),
                x + 149, y + 36, 0x303030, false);
        graphics.drawString(font, Component.translatable(selectedEntry.descriptionKey()),
                x + 112, y + 58, 0x404040, false);
        graphics.drawString(font, Component.translatable("fletching.eternal_career.maximum",
                        FletchingBowTooltipEvents.numeral(selectedEntry.maxLevel())),
                x + 112, y + 71, 0x404040, false);
        int effectLine = 0;
        for (var line : font.split(selectedEntry.effect(1), 112)) {
            graphics.drawString(font, line, x + 112, y + 80 + effectLine++ * 9,
                    0x315B31, false);
        }
        for (int index = 0; index < selectedEntry.materials().size(); index++) {
            BowModification.Material material = selectedEntry.materials().get(index);
            int materialY = y + 106 + index * 16;
            graphics.renderItem(material.icon(), x + 113, materialY);
            graphics.drawString(font, Component.translatable("fletching.eternal_career.material",
                    material.count(), owned(material)), x + 134, materialY + 4,
                    owned(material) >= material.count() ? 0x315B31 : 0x9A3030, false);
        }
    }

    private void renderModifyPage(GuiGraphics graphics) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x + 10, y + 31, x + 127, y + 145, DARK);
        graphics.fill(x + 12, y + 33, x + 125, y + 143, 0xFF999999);
        graphics.drawString(font, Component.translatable("fletching.eternal_career.existing"),
                x + 17, y + 37, 0x303030, false);
        boolean found = false;
        int line = 0;
        for (BowModification modification : BowModifications.ALL) {
            int level = BowModificationHelper.getLevel(menu.bow(), modification.id());
            if (level > 0) {
                found = true;
                graphics.drawString(font, Component.translatable("fletching.eternal_career.level",
                        Component.translatable(modification.nameKey()),
                        FletchingBowTooltipEvents.numeral(level)),
                        x + 17, y + 53 + line++ * 13, 0x315B31, false);
            }
        }
        if (!found) {
            graphics.drawString(font, Component.translatable("fletching.eternal_career.none"),
                    x + 17, y + 56, 0x505050, false);
        }
        drawSlot(graphics, x + 182, y + 53);
        drawSlot(graphics, x + 182, y + 91);
        graphics.drawString(font, Component.translatable("fletching.eternal_career.bow"),
                x + 151, y + 37, 0x303030, false);
        graphics.drawString(font, Component.translatable("fletching.eternal_career.upgrade"),
                x + 151, y + 78, 0x303030, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 10, 9, 0x303030, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY,
                0x303030, false);
    }

    public boolean hidesSlot(Slot slot) {
        return craftPage && (slot == menu.slots.get(FletchingTableMenu.BOW_SLOT)
                || slot == menu.slots.get(FletchingTableMenu.UPGRADE_SLOT));
    }

    private int hoveredRow(double mouseX, double mouseY) {
        if (!craftPage || mouseX < leftPos + LIST_X
                || mouseX >= leftPos + LIST_X + LIST_WIDTH
                || mouseY < topPos + LIST_Y
                || mouseY >= topPos + LIST_Y + LIST_HEIGHT) {
            return -1;
        }
        int index = scroll + (int) (mouseY - topPos - LIST_Y) / ROW_HEIGHT;
        return index < BowModifications.ALL.size() ? index : -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseY >= topPos - 20 && mouseY < topPos + 2) {
            if (mouseX >= leftPos + 8 && mouseX < leftPos + 73) {
                craftPage = true;
                refreshButtons();
                return true;
            }
            if (mouseX >= leftPos + 76 && mouseX < leftPos + 141) {
                craftPage = false;
                refreshButtons();
                return true;
            }
        }
        if (button == 0) {
            int row = hoveredRow(mouseX, mouseY);
            if (row >= 0) {
                selected = row;
                refreshButtons();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (craftPage && mouseX >= leftPos + LIST_X
                && mouseX < leftPos + LIST_X + LIST_WIDTH
                && mouseY >= topPos + LIST_Y
                && mouseY < topPos + LIST_Y + LIST_HEIGHT) {
            int visible = Math.max(1, LIST_HEIGHT / ROW_HEIGHT);
            int maximum = Math.max(0, BowModifications.ALL.size() - visible);
            scroll = Mth.clamp(scroll - (int) Math.signum(delta), 0, maximum);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
