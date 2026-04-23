package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

public class ViewSetRastersScreen extends RasterScreen {
    public ViewSetRastersScreen() {
        super();
        MapScreen.toggleAltScreenMap(MinecraftClient.getInstance().currentScreen != null);
    }

    @Override
    protected void init() {
        super.init();

        //TODO translate
        this.addRaster(new RasterLayerWidget(Text.of("Add Overlay"), null, null));
        addRaster(new RasterLayerWidget(Text.of("OpenMineMap"), null, LayerType.LOCAL_GEN));

        for (TileUrl url : TileUrlFile.getEnabledRasters()) {
            addRaster(new RasterLayerWidget(Text.of(url.name), url, LayerType.BASE));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

    }
}
