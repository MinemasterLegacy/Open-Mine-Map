package net.mmly.openminemap.raster;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Scaling;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.map.RegisterableTile;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BooleanSupplier;

public class RasterScreen extends Screen {

    private static final int BOTTOM_SPACE = 40;
    public static final int ITEM_HEIGHT = 40;
    RasterList rasterList;
    ArrayList<RasterLayerWidget> rasterWidgets = new ArrayList<>();
    ArrayList<AnchorWidget> anchorWidgets = new ArrayList<>();
    public static RasterScreen instance;
    public static LinkedList<RegisterableTile> tileRegisteringQueue = new LinkedList<>();
    public static HashMap<String, Identifier> backgroundTiles = new HashMap<>();
    public static boolean returnToHud = false; //if false, return to mapscreen
    private ButtonLayer addRasterLayer;

    public RasterScreen(boolean returnToMapElseHud) {
        super(Text.of(""));
        instance = this;
        RasterScreen.returnToHud = returnToMapElseHud;
        MapScreen.toggleAltScreenMap(!returnToMapElseHud);

    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(returnToHud ? null : new MapScreen());
    }

    private void registerQueue() {

        for (int i = 0; i < tileRegisteringQueue.size(); i++) {
            RegisterableTile tile;
            try {
                tile = tileRegisteringQueue.getFirst();
            } catch (NoSuchElementException e) {
                return;
            }
            try {
                //System.out.println("Registering Tile: " + tile.key);
                NativeImage nImage = NativeImage.read(tile.image);
                //register new dynamic texture and store it again to be referenced later
                Identifier identifier = Identifier.of("openminemap-tile", tile.cacheName.toLowerCase(Locale.US));
                MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(nImage));
                backgroundTiles.put(tile.cacheName.toLowerCase(Locale.US), identifier);
                //System.out.println("New Dynamic tile");

                tile.image.close();
                nImage.close();
            } catch (IOException ignored) {

            } finally {
                tileRegisteringQueue.removeFirst();
            }

        }
    }

    public static void registerBackgroundTile(RegisterableTile tile) {
        TileUrl url = TileUrlFile.getUrlByName(tile.cacheName);
        if (url == null) return;
        try {
            System.out.println("making new tile");
            NativeImage nImage = NativeImage.read(tile.image);
            Identifier identifier = Identifier.of("openminemap-btile", tile.cacheName.toLowerCase(Locale.US));
            //Identifier identifier = Identifier.of("openminemap-btile", "testimage.png");
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(nImage));
            backgroundTiles.put(url.name.toLowerCase(Locale.US), identifier);

            tile.image.close();
            nImage.close();
        } catch (IOException e) {
            System.out.println("oh noes");
            //TODO
        }

    }

    public static RasterScreen getInstance() {
        return instance;
    }

    protected RasterScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        rasterList = new RasterList(MinecraftClient.getInstance(), 0, 0, 0, ITEM_HEIGHT + 4);
        this.addDrawableChild(rasterList);

        addRasterLayer = new ButtonLayer(0, 0, ButtonFunction.ADD, () -> false);
        this.addDrawableChild(addRasterLayer);

        for (TileUrl url : TileUrlFile.getTileUrls()) {
            addRaster(new RasterLayerWidget(Text.of(url.name), url));
        }

        updateWidgetPositions();
    }

    private void updateWidgetPositions() {
        rasterList.setWidth(width);
        rasterList.setHeight(height - BOTTOM_SPACE);

        addRasterLayer.setPosition(10, height - 30);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        UContext.setContext(context);
        updateWidgetPositions();
        registerQueue();

        super.render(context, mouseX, mouseY, delta);

    }

    private void addRaster(RasterLayerWidget widget) {
        rasterWidgets.add(widget);
        AnchorWidget anchor = new AnchorWidget();
        this.addDrawableChild(widget);

        rasterList.addEntry(anchor);
        anchorWidgets.add(anchor);
        widget.setAnchor(anchor);
        anchor.setWidget(widget);
    }

}
