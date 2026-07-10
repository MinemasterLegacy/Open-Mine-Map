package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.map.RegisterableTile;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;
import net.mmly.openminemap.util.nameSupplier;

import java.io.IOException;
import java.util.*;

public abstract class RasterScreen extends Screen {
//TODO disallow modification if load failed
    public static final int ITEM_HEIGHT = 40;
    public final int BOTTOM_PADDING;
    RasterList rasterList;
    ArrayList<RasterLayerWidget> rasterWidgets = new ArrayList<>();
    ArrayList<AnchorWidget> anchorWidgets = new ArrayList<>();
    public static RasterScreen instance;
    public static LinkedList<RegisterableTile> tileRegisteringQueue = new LinkedList<>();
    public static HashMap<String, Identifier> backgroundTiles = new HashMap<>();
    protected Screen returnScreen;

    public RasterScreen(int bottomPadding, boolean updateReturnScreen) {
        super(Text.of(""));
        instance = this;
        this.BOTTOM_PADDING = bottomPadding;
        if (updateReturnScreen) returnScreen = MinecraftClient.getInstance().currentScreen;
        else returnScreen = ((RasterScreen) MinecraftClient.getInstance().currentScreen).returnScreen;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(returnScreen);
    }

    private static void registerQueue() {
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
                MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(new nameSupplier(), nImage));
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
            NativeImage nImage = NativeImage.read(tile.image);
            Identifier identifier = Identifier.of("openminemap-btile", tile.cacheName.toLowerCase(Locale.US));
            //Identifier identifier = Identifier.of("openminemap-btile", "testimage.png");
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(new nameSupplier(), nImage));
            backgroundTiles.put(url.name.toLowerCase(Locale.US), identifier);

            tile.image.close();
            nImage.close();
        } catch (IOException e) {
            //TODO
        }

    }

    public static RasterScreen getInstance() {
        return instance;
    }

    public RasterLayerWidget getSelectedLayerWidget() {
        try {
            return (RasterLayerWidget) Objects.requireNonNull(rasterList.getFocused()).widget;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    abstract void populateRasterList();

    /*
    public void purgeRasterList() {
        System.out.println("purge");
        this.remove(rasterList);
        rasterWidgets.clear();
        anchorWidgets.clear();
        rasterList = new RasterList(MinecraftClient.getInstance(), 0, 0, 0, ITEM_HEIGHT + 4);
        this.addDrawableChild(rasterList);
    }

     */

    @Override
    protected void init() {
        super.init();

        rasterList = new RasterList(MinecraftClient.getInstance(), 0, 0, 0, ITEM_HEIGHT + 4);
        this.addDrawableChild(rasterList);

        //purgeRasterList();
        populateRasterList();

    }

    protected void updateWidgetPositions() {
        rasterList.setWidth(width);
        rasterList.setHeight(height - BOTTOM_PADDING);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        UContext.setContext(context);
        updateWidgetPositions();
        registerQueue();

        super.render(context, mouseX, mouseY, delta);

    }

    protected final void addRaster(RasterLayerWidget widget) {
        rasterWidgets.add(widget);
        AnchorWidget anchor = new AnchorWidget();
        this.addDrawableChild(widget);

        rasterList.addEntry(anchor);
        anchorWidgets.add(anchor);
        widget.setAnchor(anchor);
        anchor.setWidget(widget);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean b = super.mouseReleased(mouseX, mouseY, button);
        for (RasterLayerWidget widget : rasterWidgets) {
            widget.releaseSlider(mouseX, mouseY);
        }
        return b;
    }

}
