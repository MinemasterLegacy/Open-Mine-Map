package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

//todo add done buton
//todo add preview option

public class ViewSetRastersScreen extends RasterScreen {

    private static ViewSetRastersScreen instance;

    public ViewSetRastersScreen(boolean updateReturnScreen) {
        super(0, updateReturnScreen);
        instance = this;
        if (returnScreen instanceof MapScreen && updateReturnScreen) MapScreen.toggleAltScreenMap(MinecraftClient.getInstance().currentScreen != null);
    }

    public static ViewSetRastersScreen getInstance() {
        return instance;
    }

    @Override
    void populateRasterList() {
        this.addRaster(new RasterLayerWidget(Text.of("Add Overlay"), null, null));

        for (TileUrl url : RasterProvider.getCurrentOverlays().reversed()) {
            if (url.layerType == LayerType.LOCAL_GEN)
                addRaster(new RasterLayerWidget(Text.of("OpenMineMap"), TileUrl.generatedLayerUrl, LayerType.LOCAL_GEN));
            else
                addRaster(new RasterLayerWidget(Text.of(url.name), url, LayerType.OVERLAY));
        }

        addRaster(new RasterLayerWidget(RasterProvider.getCurrentBaseRaster()));
    }

    @Override
    public void close() {
        super.close();
        RasterProvider.writeOverlayInfo();
        MapScreen.toggleAltScreenMap(false);
    }

    public void addOpacitySlider(OpacitySlider slider) {
        this.addDrawableChild(slider);
    }

}
