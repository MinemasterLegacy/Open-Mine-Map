package net.mmly.openminemap.raster;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.RasterApiKeysFile;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

public class BaseRasterScreen extends RasterScreen {
    public BaseRasterScreen(boolean updateReturnScreen) {
        super(40, updateReturnScreen);
    }

    public static Button confirmButton;

    @Override
    void populateRasterList() {
        this.addRaster(new RasterLayerWidget(Component.translatable("omm.raster.create-new"), null, null));

        for (TileUrl url : RasterProvider.getCustomRasters()) {
            if (url.layerType != LayerType.BASE) continue;
            addRaster(new RasterLayerWidget(Component.nullToEmpty(url.name), url, null));
        }

        for (TileUrl url : RasterProvider.getPresetRasters()) {
            addRaster(new RasterLayerWidget(Component.nullToEmpty(url.name), url, null));
        }
    }

    @Override
    protected void init() {
        confirmButton = Button.builder(Component.nullToEmpty(""),(buttonWidget) -> {
            try {
                if (getSelectedLayerWidget().raster.hasKeyField() && !RasterApiKeysFile.hasApiKey(getSelectedLayerWidget().raster.presetID)) return;
                RasterProvider.setCurrentBaseRaster(getSelectedLayerWidget().raster);
                Minecraft.getInstance().screen.onClose();
            } catch (NullPointerException ignored) {}
        }).width(200).build();
        this.addRenderableWidget(confirmButton);

        super.init();
    }

    @Override
    protected void updateWidgetPositions() {
        super.updateWidgetPositions();

        confirmButton.setPosition(width / 2 - confirmButton.getWidth() / 2, height - 30);

        confirmButton.active = false;
        if (getSelectedLayerWidget() == null) {
            confirmButton.setMessage(Component.translatable("omm.raster.select-base"));
            return;
        }
        if (getSelectedLayerWidget().raster.hasKeyField() && !RasterApiKeysFile.hasApiKey(getSelectedLayerWidget().raster.presetID)) {
            confirmButton.setMessage(Component.translatable("omm.raster.requires-api-key"));
            return;
        }
        if (RasterProvider.getCurrentBaseRaster() == getSelectedLayerWidget().raster) {
            confirmButton.setMessage(Component.translatable("omm.raster.base-already-in-use"));
            return;
        }
        confirmButton.setMessage(Component.translatable("omm.raster.set-base").append(" " + getSelectedLayerWidget().raster.name));
        confirmButton.active = true;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
