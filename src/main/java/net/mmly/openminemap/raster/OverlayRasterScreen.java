package net.mmly.openminemap.raster;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

public class OverlayRasterScreen extends RasterScreen {
    public OverlayRasterScreen(boolean updateReturnScreen) {
        super(40, updateReturnScreen);
    }

    public static Button confirmButton;

    @Override
    void populateRasterList() {
        this.addRaster(new RasterLayerWidget(Component.nullToEmpty("Create New Overlay Layer"), null, null));

        for (TileUrl url : RasterProvider.getCustomRasters()) {
            if (url.layerType != LayerType.OVERLAY) continue;
            addRaster(new RasterLayerWidget(Component.nullToEmpty(url.name), url, null));
        }
    }

    @Override
    protected void init() {
        confirmButton = Button.builder(Component.nullToEmpty(""),(buttonWidget) -> {
            RasterProvider.pushOverlayOnTop(getSelectedLayerWidget().raster);
            Minecraft.getInstance().gui.screen().onClose();
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
            confirmButton.setMessage(Component.translatable("omm.raster.select-overlay"));
            return;
        }
        if (RasterProvider.overlayInUse(getSelectedLayerWidget().raster)) {
            confirmButton.setMessage(Component.translatable("omm.raster.overlay-already-in-use"));
            return;
        }
        confirmButton.setMessage(Component.translatable("omm.raster.add-overlay").append(" " + getSelectedLayerWidget().raster.name));
        confirmButton.active = true;
    }
}
