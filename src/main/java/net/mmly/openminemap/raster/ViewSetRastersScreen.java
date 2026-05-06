package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

//todo add done buton
//todo add preview option

public class ViewSetRastersScreen extends RasterScreen {

    private static ViewSetRastersScreen instance;

    public ViewSetRastersScreen() {
        super(0);
        instance = this;
        if (returnScreen instanceof MapScreen) MapScreen.toggleAltScreenMap(MinecraftClient.getInstance().currentScreen != null);
    }

    public static ViewSetRastersScreen getInstance() {
        return instance;
    }

    @Override
    public void close() {
        super.close();
        MapScreen.toggleAltScreenMap(false);
    }

    @Override
    protected void init() {
        super.init();

        //TODO translate
        this.addRaster(new RasterLayerWidget(Text.of("Add Overlay"), null, null));

        addRaster(new RasterLayerWidget(new TileUrl(
                "Test Overlay",
                "e",
                "e",
                new String[0],
                LayerType.OVERLAY
        )));

        addRaster(new RasterLayerWidget(Text.of("OpenMineMap"), null, LayerType.LOCAL_GEN));

        addRaster(new RasterLayerWidget(TileUrlFile.getCurrentBaseRaster()));

        for (TileUrl url : TileUrlFile.getCurrentOverlays()) {
            addRaster(new RasterLayerWidget(Text.of(url.name), url, LayerType.BASE));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

    }

    public void addOpacitySlider(OpacitySlider slider) {
        this.addDrawableChild(slider);
    }

}
