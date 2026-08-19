package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.raster.ViewSetRastersScreen;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

public class RasterConfigWidget extends ButtonWidget implements ConfigChoice {

    AnchorWidget anchor;

    public RasterConfigWidget(net.minecraft.text.Text message) {
        super(0, -100, 200, 20, message, button -> {}, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.setTooltip(Tooltip.of(net.minecraft.text.Text.translatable(ConfigOptions.TILE_MAP_URL.tooltip)));
    }

    @Override
    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    public void writeParameterToFile() {
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!anchor.drawNow) {return; }
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        this.drawButton(context);
        this.drawLabel(context.getHoverListener(this, DrawContext.HoverType.NONE));
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        MinecraftClient.getInstance().setScreen(
                new ViewSetRastersScreen(true)
        );
    }
}
