package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
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
import net.mmly.openminemap.util.ColorUtil;
import net.mmly.openminemap.util.TileUrl;

import java.util.Locale;

public class RasterLayerWidget extends ClickableWidget {

    private AnchorWidget anchor;
    private final TileUrl url;
    private final LayerType layerType;
    //private final MicroButtonFunction[] microButtons;
    private final MicroButton[] microButtons;
    private boolean isAddButton = false;
    private final String textureKey;

    public RasterLayerWidget(Text message, TileUrl url, LayerType type) {
        super(10, 0, 0, RasterScreen.ITEM_HEIGHT, message);
        this.url = url;
        this.layerType = type;
        if (url == null && type == null) isAddButton = true;

        if (!isAddButton) {
            MicroButtonFunction[] functions = LayerType.getMicroButtons(layerType);
            microButtons = new MicroButton[functions.length];
            for (int i = 0; i < functions.length; i++) {
                microButtons[i] = new MicroButton(0, 0, functions[i], layerType);
            }
        } else {
            microButtons = new MicroButton[0];
        }

        if (url != null) {
            textureKey = url.name.toLowerCase(Locale.US);
            if (url.presetID >= 0) RasterScreen.backgroundTiles.put( //is preset
                    textureKey,
                    Identifier.of("openminemap", "rastertiles/" + url.name
                            .replace("Ö", "O")
                            .toLowerCase(Locale.US)
                            .replace(" ", "")
                            + ".png"
                    )
            );
            else if (!RasterScreen.backgroundTiles.containsKey(textureKey)) {
                RasterScreen.backgroundTiles.put(textureKey, TileManager.getLoadingIdentifier());
                new TileLoader(new LoadableTile[] {
                        new LoadableTile(
                                0, 0, 0, url.name,
                                TileManager.getKey(0, 0, 0)
                        )
                }, RegisterableTile.RASTER_SCREEN).start();

            }

        } else {
            textureKey = null;
        }
        /*
        if (!RasterScreen.backgroundTiles.containsKey(url.name)) {
            RasterScreen.backgroundTiles.put(url.name.toLowerCase(Locale.US), TileManager.getLoadingIdentifier());
            new TileLoader(new LoadableTile[] {
                    new LoadableTile(
                            0, 0, 0, url.name,
                            TileManager.getKey(0, 0, 0)
                    )
            }, RegisterableTile.RASTER_SCREEN).setPreset(true).start();

        }

         */
    }

    private Identifier getBackgroundTexture() {
        if (isAddButton) return Identifier.of("openminemap", "customtile.png"); //for the add option
        if (layerType == LayerType.LOCAL_GEN) return Identifier.of("openminemap", "icon-texture.png"); //for the generated overlays option
        Identifier texture = RasterScreen.backgroundTiles.get(textureKey); // for custom layers
        if (texture == null) return TileManager.getErrorIdentifier();
        else return texture;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    private void updatePositions() {
        setX(anchor.getX());
        setY(anchor.getY());
        setWidth(anchor.getWidth());

        int offset = 15;
        for (MicroButton button : microButtons) {
            button.setPosition(
                    getRight() - offset,
                    getBottom() - 15

            );
            offset += 15;
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;

        updatePositions();

        UContext.drawTexture(
                getBackgroundTexture(),
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

        if (isAddButton) {
            UContext.drawJustifiedText(getMessage(), Justify.CENTER, getX() + getWidth() / 2, getY() + (getHeight() / 2) - 4,0xFFFFFCA8, true);
            UContext.outline(this, isHovered() || isFocused() ? 0xFFFFFCA8 : ColorUtil.darken(0xFFFFFCA8, 0.5));
        } else {
            UContext.drawJustifiedText(getMessage(), Justify.CENTER, getX() + getWidth() / 2, getY() + 7, 0xFFFFFFFF, true);
        }

        if (MinecraftClient.getInstance().currentScreen instanceof BaseRasterScreen && isHovered()) UContext.outline(this, 0xFFFFFFFF);
        //TODO Translate

        if (layerType != null) UContext.drawJustifiedText(Text.of(getSubMessage()), Justify.CENTER, getX() + getWidth() / 2, getY() + 24, 0xFFBFBFBF, true);

        if (url != null) if (url.presetID >= 5) {
            UContext.drawTexture(
                    Identifier.of("openminemap", "rasterkey.png"),
                    getX() - 1,
                    getBottom() - 18,
                    16,
                    16,
                    16,
                    16
            );
        }


        for (MicroButton button : microButtons) {
            button.draw(mouseX, mouseY);
        }

        //TODO translate
        if (url != null) if (isHovered() && url.presetID >= 5 && mouseIsOverKey(mouseX, mouseY)) setTooltip(Tooltip.of(Text.of("Requires Api Key")));
        else setTooltip(Tooltip.of(Text.empty()));
    }

    private String getSubMessage() {
        //TODO translate
        return switch (layerType) {
            case BASE -> "Base Layer";
            case OVERLAY -> "Overlay";
            case LOCAL_GEN -> "Generated Overlays";
        };
    }

    private boolean mouseIsOverKey(int mouseX, int mouseY) {
        return mouseX > getX() + 3 && mouseX < getX() + 19 && mouseY < getBottom() - 3 && mouseY > getBottom() - 19;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAddButton) {
            if (MinecraftClient.getInstance().currentScreen instanceof ViewSetRastersScreen) {
                MinecraftClient.getInstance().setScreen(new OverlayRasterScreen());
            }
        }
        for (MicroButton mButton : microButtons) {
            if (mButton.isMouseOver(mouseX, mouseY)) {
                mButton.onClick(mouseX, mouseY);
                return false;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
