package net.mmly.openminemap.config;

import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.MapScreen;

import static net.mmly.openminemap.config.ConfigScreen.windowScaledHeight;
import static net.mmly.openminemap.config.ConfigScreen.windowScaledWidth;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CreditLayer extends AbstractWidget {

    private static final int TEXT_COLOR = 0xff00AAAA;
    private static final MutableComponent TEXT = Component.literal("By MinemasterLegacy");
    private static final MutableComponent UNDERLINED_TEXT = Component.literal("By MinemasterLegacy").withStyle(ChatFormatting.UNDERLINE);

    public CreditLayer(int x, int y) {
        super(x, y, Minecraft.getInstance().font.width(TEXT) + 4, 12, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0xFFFFFFFF);
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    public void drawWidget(GuiGraphics context, Font textRenderer) {
        context.drawString(textRenderer,
                isHovered() ? UNDERLINED_TEXT : TEXT,
                getX() + 2,
                getY() + 1,
                TEXT_COLOR,
                true);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        //FullscreenMapScreen.openBugReportScreen();
        MapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki", ConfigScreen.getInstance(), false);
    }
}
