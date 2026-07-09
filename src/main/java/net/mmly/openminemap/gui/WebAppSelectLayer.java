package net.mmly.openminemap.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.WebIcon;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class WebAppSelectLayer extends ClickableWidget {



    public WebAppSelectLayer() {
        super(0, 0, 14, 2, Text.of(""));

    }


    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        /*if (!RightClickMenu.selectingSite) {
            setX(-100);
            return;
        }
        selection = (int) Math.floor((double) (mouseY - getY() - 1) / 16);
        if (selection > webIcons.size() - 1 || selection < 0 || mouseX < getX() || mouseX > getX() + getWidth() - 1) {
            selection = -1;
            setTooltip(null);
        } else {
            //TODO accurate tooltip rendering
            //setTooltip(webIcons.get(selection).tooltip);
        }
    }

    protected void drawWidget(DrawContext context) {
        if (!RightClickMenu.selectingSite) return;

        context.fill(getX(), getY(), getX() + getWidth(), getY()+getHeight(), MapScreen.backingColor);

        if (selection != -1) UContext.drawTexture(webIcons.get(selection).highlight, getX() + 2 - 1, getY() + 2 + (selection * 16) - 1, 12, 16, 12, 16);

        for (int i = 0; i < webIcons.size(); i++) {
            UContext.drawTexture(webIcons.get(i).icon, getX() + 2, getY() + 2 + (i * 16), 10, 14, 10, 14);
        }*/
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }


}
