package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.RasterApiKeysFile;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

public class BaseRasterScreen extends RasterScreen {
    public BaseRasterScreen() {
        super(40, true);
    }

    public static ButtonWidget confirmButton;

    @Override
    void populateRasterList() {
        this.addRaster(new RasterLayerWidget(Text.translatable("omm.raster.create-new"), null, null));

        for (TileUrl url : RasterProvider.getCustomRasters()) {
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
                if (getSelectedLayerWidget().raster.hasKeyField() && !RasterApiKeysFile.hasApiKey(getSelectedLayerWidget().raster.presetID)) return;
                TileManager.setTileUrl(getSelectedLayerWidget().raster);
                MinecraftClient.getInstance().currentScreen.close();
            } catch (NullPointerException ignored) {}
        }).width(200).build();
        this.addDrawableChild(confirmButton);

    }

    @Override
    protected void updateWidgetPositions() {
        super.updateWidgetPositions();

        confirmButton.setPosition(width / 2 - confirmButton.getWidth() / 2, height - 30);

        confirmButton.active = false;
        if (getSelectedLayerWidget() == null) {
            confirmButton.setMessage(Text.translatable("omm.raster.select-base"));
            return;
        }
        if (getSelectedLayerWidget().raster.hasKeyField() && !RasterApiKeysFile.hasApiKey(getSelectedLayerWidget().raster.presetID)) {
            confirmButton.setMessage(Text.translatable("omm.raster.requires-api-key"));
            return;
        }
        if (RasterProvider.getCurrentBaseRaster() == getSelectedLayerWidget().raster) {
            confirmButton.setMessage(Text.translatable("omm.raster.already-in-use"));
            return;
        }
        confirmButton.setMessage(Text.translatable("omm.raster.set-base").append(" " + getSelectedLayerWidget().raster.name));
        confirmButton.active = true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
