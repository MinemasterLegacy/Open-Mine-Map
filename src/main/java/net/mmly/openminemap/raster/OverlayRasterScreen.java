package net.mmly.openminemap.raster;

import net.minecraft.text.Text;

public class OverlayRasterScreen extends RasterScreen {
    public OverlayRasterScreen() {
        super(40);
    }

    @Override
    protected void init() {
        super.init();

        this.addRaster(new RasterLayerWidget(Text.of("Create New Overlay Layer"), null, null));

    }
}
