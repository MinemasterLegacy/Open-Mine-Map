package net.mmly.openminemap.raster;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

public class BaseRasterScreen extends RasterScreen {
    public BaseRasterScreen() {
        super();
    }

    @Override
    protected void init() {
        super.init();

        this.addRaster(new RasterLayerWidget(Text.of("Create New Base Layer"), null, null));

        for (TileUrl url : TileUrlFile.getPresets()) {
            addRaster(new RasterLayerWidget(Text.of(url.name), url, null));
        }

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }
}
