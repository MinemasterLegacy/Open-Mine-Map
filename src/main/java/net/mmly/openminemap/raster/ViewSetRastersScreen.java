package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mmly.openminemap.config.ConfigScreen;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

public class ViewSetRastersScreen extends RasterScreen {

    private static ViewSetRastersScreen instance;

    public ViewSetRastersScreen(boolean updateReturnScreen) {
        super(0, updateReturnScreen);
        instance = this;
        if (updateReturnScreen) MapScreen.updateAltScreenMap(returnScreen, this);
    }

    public static ViewSetRastersScreen getInstance() {
        return instance;
    }

    @Override
    void populateRasterList() {
        if (!TileUrlFile.loadFailed) this.addRaster(new RasterLayerWidget(Text.of("Add Overlay"), null, null));

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
        TileUrlFile.saveCustomRastersToFile();
        ConfigFile.writeToFile();
        MapScreen.updateAltScreenMap(this, returnScreen);
     }

    public void addOpacitySlider(OpacitySlider slider) {
        this.addDrawableChild(slider);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (TileUrlFile.loadFailed) UContext.drawJustifiedText(
                Text.translatable("omm.error.raster-load-failed").formatted(Formatting.RED).formatted(Formatting.ITALIC),
                Justify.CENTER,
                width / 2,
                height - 25,
                0xFFFFFFFF,
                true
        );
    }
}
