package net.mmly.openminemap.raster;

import net.minecraft.text.Text;

public class OverlayRasterScreen extends RasterScreen {
    @Override
    protected void init() {
        super.init();

        this.addRaster(new RasterLayerWidget(Text.of("Create New Overlay Layer"), null, null));

    }
}
