package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.Scaling;
import net.minecraft.text.Text;
import net.mmly.openminemap.config.ConfigChoice;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

public class BaseRasterScreen extends RasterScreen {
    public BaseRasterScreen() {
        super(40);
    }

    public static ButtonWidget confirmButton;

    @Override
    protected void init() {
        super.init();

        //TODO translate
        this.addRaster(new RasterLayerWidget(Text.of("Create New Base Layer"), null, null));

        for (TileUrl url : TileUrlFile.getPresets()) {
            addRaster(new RasterLayerWidget(Text.of(url.name), url, null));
        }

        confirmButton = ButtonWidget.builder(Text.of(""),(buttonWidget) -> {
            try {
                TileManager.setTileUrl(getSelectedLayerWidget().url);
                MinecraftClient.getInstance().currentScreen.close(); //todo verify api key for rasters that need it
            } catch (NullPointerException ignored) {}
        }).width(200).build();
        this.addDrawableChild(confirmButton);

    }

    @Override
    protected void updateWidgetPositions() {
        super.updateWidgetPositions();

        confirmButton.setPosition(width / 2 - confirmButton.getWidth() / 2, height - 30);

        //TODO translate
        confirmButton.active = getSelectedLayerWidget() != null;
        confirmButton.setMessage(confirmButton.active ?
                Text.of("Set Base to " + getSelectedLayerWidget().url.name) :
                Text.of("Select a Base Raster..."));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
