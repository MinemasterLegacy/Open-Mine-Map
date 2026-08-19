package net.mmly.openminemap.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.raster.ViewSetRastersScreen;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

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
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        if (!anchor.drawNow) {return; }
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        this.renderDefaultSprite(context);
        this.renderDefaultLabel(context.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        Minecraft.getInstance().setScreen(
                new ViewSetRastersScreen(true)
        );
    }
}
