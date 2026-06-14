package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

public class OverlayRasterScreen extends RasterScreen {
    public OverlayRasterScreen() {
        super(40, true);
    }

    public static ButtonWidget confirmButton;

    @Override
    void populateRasterList() {
        this.addRaster(new RasterLayerWidget(Text.of("Create New Overlay Layer"), null, null));

        for (TileUrl url : RasterProvider.getCustomRasters()) {
            if (url.layerType != LayerType.OVERLAY) continue;
            addRaster(new RasterLayerWidget(Text.of(url.name), url, null));
        }
    }

    @Override
    protected void init() {
        super.init();

        confirmButton = ButtonWidget.builder(Text.of(""),(buttonWidget) -> { 
            RasterProvider.insertOverlayOnTop(getSelectedLayerWidget().raster);
            MinecraftClient.getInstance().currentScreen.close();
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
            confirmButton.setMessage(Text.of("Select an Overlay Raster..."));
            return;
        }
        if (RasterProvider.overlayInUse(getSelectedLayerWidget().raster)) {
            confirmButton.setMessage(Text.of("Overlay Already In Use"));
            return;
        }
        confirmButton.setMessage(Text.of("Add Overlay " + getSelectedLayerWidget().raster.name));
        confirmButton.active = true;
    }
}
