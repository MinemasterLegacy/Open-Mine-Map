package net.mmly.openminemap.raster;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.map.LoadableTile;
import net.mmly.openminemap.map.RegisterableTile;
import net.mmly.openminemap.map.TileLoader;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.TileUrl;

import java.util.Locale;

public class RasterLayerWidget extends ClickableWidget {

    private AnchorWidget anchor;
    private final TileUrl url;

    public RasterLayerWidget(Text message, TileUrl url) {
        super(10, 0, 0, RasterScreen.ITEM_HEIGHT, message);
        this.url = url;
        if (!RasterScreen.backgroundTiles.containsKey(url.name)) {
            RasterScreen.backgroundTiles.put(url.name.toLowerCase(Locale.US), TileManager.getLoadingIdentifier());
            new TileLoader(new LoadableTile[] {
                    new LoadableTile(
                            0, 0, 0, url.name,
                            TileManager.getKey(0, 0, 0)
                    )
            }, RegisterableTile.RASTER_SCREEN).start();

        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;

        setX(anchor.getX());
        setY(anchor.getY());
        setWidth(anchor.getWidth());

        Identifier texture = RasterScreen.backgroundTiles.get(url.name.toLowerCase(Locale.US));
        if (texture == null) texture = TileManager.getErrorIdentifier();

        UContext.drawTexture(
                texture,
                getX(),
                getY(),
                getWidth(),
                getHeight(),
                0,
                (float) getWidth() / 2 - (float) RasterScreen.ITEM_HEIGHT / 2,
                getWidth(),
                RasterScreen.ITEM_HEIGHT,
                getWidth(),
                getWidth()
        );
        UContext.fillWidget(this, 0x7f000000);
        UContext.borderWidget(this, isFocused() ? 0xFFFFFFFF : 0xFF7f7f7f);

        UContext.drawJustifiedText(getMessage(), Justify.CENTER, getX() + getWidth() / 2, getY() + 4, 0xFFFFFFFF, true);
    }

}
