package net.mmly.openminemap.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.mmly.openminemap.draw.UContext;

public class WikiLinkLayer extends AbstractWidget {

    private static final int TEXT_COLOR = 0xFFaa00aa;
    private static final MutableComponent TEXT = Component.translatable("omm.config.gui.omm-wiki");
    private static final MutableComponent UNDERLINED_TEXT = Component.translatable("omm.config.gui.omm-wiki").withStyle(ChatFormatting.UNDERLINE);

    public WikiLinkLayer(int x, int y) {
        super(x, y, 0, 16, Component.empty());
        setWidth(Minecraft.getInstance().font.width(TEXT) + 8);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    public void drawWidget(GuiGraphicsExtractor context, Font textRenderer) {
        UContext.fillWidget(this, MapScreen.backingColor);
        context.text(textRenderer,
                isHovered() ? UNDERLINED_TEXT : TEXT,
                getX() + 4,
                getY() + 4,
                TEXT_COLOR,
                true);
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        //FullscreenMapScreen.openBugReportScreen();
        MapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki", new MapScreen(), true);
    }
}
