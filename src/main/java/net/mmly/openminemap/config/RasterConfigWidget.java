package net.mmly.openminemap.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.raster.ViewSetRastersScreen;

public class RasterConfigWidget extends Button implements ConfigChoice {

    AnchorWidget anchor;

    public RasterConfigWidget(net.minecraft.network.chat.Component message) {
        super(0, -100, 200, 20, message, button -> {}, Button.DEFAULT_NARRATION);
        this.setTooltip(Tooltip.create(net.minecraft.network.chat.Component.translatable(ConfigOptions.TILE_MAP_URL.tooltip)));
    }

    @Override
    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    public void writeParameterToFile() {
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        if (!anchor.drawNow) {return; }
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        this.extractDefaultSprite(context);
        this.extractDefaultLabel(context.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        Minecraft.getInstance().gui.setScreen(
                new ViewSetRastersScreen(true)
        );
    }
}
