package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.Scaling;
import net.minecraft.text.Text;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.RasterApiKeysFile;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

public class BaseRasterScreen extends RasterScreen {
    public BaseRasterScreen() {
        super(40, true);
    }

    public static ButtonWidget confirmButton;

    @Override
    void populateRasterList() {
        //TODO translate
        this.addRaster(new RasterLayerWidget(Text.of("Create New Base Layer"), null, null));

        for (TileUrl url : RasterProvider.getCustomBaseRasters()) {
            if (url.layerType != LayerType.BASE) continue;
            addRaster(new RasterLayerWidget(Text.of(url.name), url, null));
        }

        for (TileUrl url : RasterProvider.getPresetRasters()) {
            addRaster(new RasterLayerWidget(Text.of(url.name), url, null));
        }
    }

    @Override
    protected void init() {
        super.init();

        confirmButton = ButtonWidget.builder(Text.of(""),(buttonWidget) -> {
            try {
                if (getSelectedLayerWidget().url.hasKeyField() && !RasterApiKeysFile.hasApiKey(getSelectedLayerWidget().url.presetID)) return;
                TileManager.setTileUrl(getSelectedLayerWidget().url);
                MinecraftClient.getInstance().currentScreen.close();
            } catch (NullPointerException ignored) {}
        }).width(200).build();
        this.addDrawableChild(confirmButton);

    }

    @Override
    protected void updateWidgetPositions() {
        super.updateWidgetPositions();

        confirmButton.setPosition(width / 2 - confirmButton.getWidth() / 2, height - 30);

        //TODO translate
        confirmButton.active = false;
        if (getSelectedLayerWidget() == null) {
            confirmButton.setMessage(Text.of("Select a Base Raster..."));
            return;
        }
        if (getSelectedLayerWidget().url.hasKeyField() && !RasterApiKeysFile.hasApiKey(getSelectedLayerWidget().url.presetID)) { //TODO replace true with a missing key check
            confirmButton.setMessage(Text.of("Requires API Key"));
            return;
        }
        if (RasterProvider.getCurrentBaseRaster() == getSelectedLayerWidget().url) {
            confirmButton.setMessage(Text.of("Base Already In Use"));
            return;
        }
        confirmButton.setMessage(Text.of("Set Base to " + getSelectedLayerWidget().url.name));
        confirmButton.active = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
