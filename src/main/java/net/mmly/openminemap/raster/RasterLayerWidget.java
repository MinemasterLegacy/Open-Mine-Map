package net.mmly.openminemap.raster;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.map.LoadableTile;
import net.mmly.openminemap.map.RegisterableTile;
import net.mmly.openminemap.map.TileLoader;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ColorUtil;
import net.mmly.openminemap.util.RasterApiKeysFile;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

import java.util.Locale;

public class RasterLayerWidget extends AbstractWidget {

    private AnchorWidget anchor;
    protected final TileUrl raster;
    private final LayerType layerType;
    private final MicroButton[] microButtons;
    private boolean isAddButton = false;
    private final String textureKey;
    private OpacitySlider opacitySlider;
    private boolean showKey = false;
    private int outlineFocusColor;
    private int outlineBaseColor;
    private float deleteProgressPercent = 0f;

    private static final int CUSTOM_RASTER_OUTLINE_COLOR = 0xFFa8afff;
    private static final int ADD_BUTTON_OUTLINE_COLOR = 0xFFFFFCA8;
    private static final int NORMAL_RASTER_OUTLINE_COLOR = 0xFFFFFFFF;

    public RasterLayerWidget(TileUrl url) {
        this(
                url == null ? Component.literal("null") : Component.literal(url.name),
                url,
                url == null ? null : url.layerType
        );
    }

    public RasterLayerWidget(Component message, TileUrl url, LayerType type) {
        super(10, 0, 0, RasterScreen.ITEM_HEIGHT, message);
        this.raster = url;
        this.layerType = type;
        if (url == null && type == null) isAddButton = true;

        chooseOutlineColor();

        if (!isAddButton) {
            MicroButtonFunction[] functions = LayerType.getMicroButtons(layerType, url != null && !url.isPreset());
            microButtons = new MicroButton[functions.length];
            for (int i = 0; i < functions.length; i++) {
                microButtons[i] = new MicroButton(0, 0, functions[i], layerType);
                microButtons[i].setParentWidget(this);
            }
        } else {
            microButtons = new MicroButton[0];
        }

        if (url != null) {
            if (url.hasKeyField()) {
                if (Minecraft.getInstance().screen instanceof BaseRasterScreen) showKey = true;
                if (!RasterApiKeysFile.hasApiKey(url.presetID)) microButtons[0].setApiKeyNeeded(true);
            }

            if (layerType == LayerType.OVERLAY && Minecraft.getInstance().screen instanceof ViewSetRastersScreen) {
                opacitySlider = new OpacitySlider(0, 0, RasterProvider.getOpacityOf(raster));
                opacitySlider.setParentWidget(this);
                ViewSetRastersScreen.getInstance().addOpacitySlider(opacitySlider);
            }

            textureKey = url.name.toLowerCase(Locale.US);
            if (RasterScreen.backgroundTiles.containsKey(textureKey)) return; //already loaded texture
            if (layerType == LayerType.LOCAL_GEN) return; //if local gen, use hard-coded texture

            if (url.isPreset()) RasterScreen.backgroundTiles.put( //if preset, load from assets
                    textureKey,
                    url.presetIdentifier
            );
            else if (!RasterScreen.backgroundTiles.containsKey(textureKey)) { //if custom, load from cache files
                RasterScreen.backgroundTiles.put(textureKey, TileManager.getLoadingIdentifier());
                new TileLoader(new LoadableTile[] {
                        new LoadableTile(
                                0, 0, 0, url,
                                TileManager.getKey(0, 0, 0, raster.identifierString)
                        )
                }, RegisterableTile.RASTER_SCREEN).updateBackgoundColor(false).setFileMayBeNull(true).start();

            }

        } else {
            textureKey = null;
        }

    }

    private void chooseOutlineColor() {
        if (layerType == null && raster == null) {
            outlineBaseColor = ColorUtil.darken(ADD_BUTTON_OUTLINE_COLOR, 0.5);
            outlineFocusColor = ADD_BUTTON_OUTLINE_COLOR;
            return;
        }

        if (raster == null || layerType == LayerType.LOCAL_GEN) {
            outlineBaseColor = ColorUtil.darken(NORMAL_RASTER_OUTLINE_COLOR, 0.5);
            outlineFocusColor = NORMAL_RASTER_OUTLINE_COLOR;
            return;
        }

        if (raster.isPreset()) {
            outlineBaseColor = ColorUtil.darken(NORMAL_RASTER_OUTLINE_COLOR, 0.5);
            outlineFocusColor = NORMAL_RASTER_OUTLINE_COLOR;
        } else {
            outlineBaseColor = ColorUtil.darken(CUSTOM_RASTER_OUTLINE_COLOR, 0.5);
            outlineFocusColor = CUSTOM_RASTER_OUTLINE_COLOR;
        }
    }

    private Identifier getBackgroundTexture() {
        if (isAddButton) return Identifier.fromNamespaceAndPath("openminemap", "customtile.png"); //for the add option
        if (layerType == LayerType.LOCAL_GEN) return Identifier.fromNamespaceAndPath("openminemap", "icon-texture.png"); //for the generated overlays option
        Identifier texture = RasterScreen.backgroundTiles.get(textureKey); // for custom layers
        if (texture == null) return TileManager.getErrorIdentifier(layerType);
        else return texture;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    protected void setDeleteProgressPercent(float percent) {
        deleteProgressPercent = percent;
    }

    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    private void updatePositions(int mouseX) {
        setX(anchor.getX());
        setY(anchor.getY());
        setWidth(anchor.getWidth());

        int offset = 15;
        for (MicroButton button : microButtons) {
            if (button.buttonFunction == MicroButtonFunction.UP) {
                button.setPosition(
                        getX() + 3, getY() + 3
                );
                continue;
            }
            if (button.buttonFunction == MicroButtonFunction.DOWN) {
                button.setPosition(
                        getX() + 3, getBottom() - 15
                );
                continue;
            }

            button.setPosition(
                    getRight() - offset,
                    getBottom() - 15
            );
            offset += 15;
        }

        if (opacitySlider != null) {
            opacitySlider.setPosition(getRight() - 45, getY() + 3);
            opacitySlider.setRecordedMouseX(mouseX);
        }

    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;

        updatePositions(mouseX);

        drawBackground(getBackgroundTexture());

        UContext.borderWidget(this, isFocused() ? outlineFocusColor : outlineBaseColor);
        if (raster != null) if (!raster.isPreset()) UContext.borderWidget(this, isFocused() ? outlineFocusColor : outlineBaseColor);

        if (Minecraft.getInstance().screen instanceof BaseRasterScreen && isHovered()) UContext.borderWidget(this, 0xFFFFFFFF);

        if (isAddButton) {
            UContext.drawJustifiedText(getMessage(), Justify.CENTER, getX() + getWidth() / 2, getY() + (getHeight() / 2) - 4,0xFFFFFCA8, true);
            UContext.borderWidget(this, isHovered() || isFocused() ? outlineFocusColor : outlineBaseColor);
        } else {
            UContext.drawJustifiedText(getMessage(), Justify.CENTER, getX() + getWidth() / 2, getY() + 7, 0xFFFFFFFF, true);
        }

        if (layerType != null) UContext.drawJustifiedText(getSubMessage(), Justify.CENTER, getX() + getWidth() / 2, getY() + 24, 0xFFBFBFBF, true);

        if (showKey) {
            UContext.drawTexture(
                    Identifier.fromNamespaceAndPath("openminemap", "rasterkey.png"),
                    getX() - 1,
                    getBottom() - 18,
                    16,
                    16,
                    16,
                    16
            );
        }

        if (opacitySlider != null) if (opacitySlider.dragging()) UContext.setTextureAlpha(32);
        for (MicroButton button : microButtons) {
            button.draw(mouseX, mouseY);
        }
        UContext.resetTextureAlpha();

        if (isHovered() && showKey && mouseIsOverKey(mouseX, mouseY)) setTooltip(Tooltip.create(Component.nullToEmpty(
                Component.translatable("omm.raster.requires-api-key").toString() +
                "\n" +
                Component.translatable("omm.raster.click-info").toString()
        )));
        else setTooltip(Tooltip.create(Component.empty()));

        UContext.borderWidget(this,
                isFocused() || (isHovered() && (Minecraft.getInstance().screen instanceof BaseRasterScreen || isAddButton)) ?
                    outlineFocusColor :
                    outlineBaseColor
        );
        if (deleteProgressPercent != 0f) UContext.borderWidget(
                this,
                ColorUtil.setAlpha((int) (127 * deleteProgressPercent), 0xFFFF5555)
        );
        if (isAddButton && isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    private void drawBackground(Identifier texture) {
        UContext.fillWidget(this, TileManager.getThemeColor());

        UContext.drawTexture(
                texture,
                getX(),
                getY(),
                getWidth() / 2,
                getHeight(),
                0,
                (float) getWidth() / 2 - (float) RasterScreen.ITEM_HEIGHT / 2,
                getWidth() / 2,
                RasterScreen.ITEM_HEIGHT,
                getWidth(),
                getWidth()
        );

        if (opacitySlider != null) UContext.setTextureAlpha((int) (opacitySlider.getValue() * 255));
        UContext.drawTexture(
                texture,
                getX() + (getWidth() / 2),
                getY(),
                getWidth() / 2,
                getHeight(),
                (float) getWidth() / 2,
                (float) getWidth() / 2 - (float) RasterScreen.ITEM_HEIGHT / 2,
                getWidth() / 2,
                RasterScreen.ITEM_HEIGHT,
                getWidth(),
                getWidth()
        );
        UContext.resetTextureAlpha();

        if (opacitySlider != null) if (opacitySlider.dragging()) return;
        UContext.fillWidget(this, 0x7f000000);

        if (raster != null) if (layerType == LayerType.OVERLAY && !RasterProvider.getVisibilityOf(raster)) {
            UContext.setTextureAlpha(127);
            UContext.drawTexture(
                    Identifier.fromNamespaceAndPath("openminemap", "unvisible.png"),
                    getX() + (getWidth() / 2) - 10 - 40,
                    getY() + (getHeight() / 2) - 10,
                    20, 20, 0, 0, 20, 20
            );
            UContext.drawTexture(
                    Identifier.fromNamespaceAndPath("openminemap", "unvisible.png"),
                    getX() + (getWidth() / 2) - 10 + 40,
                    getY() + (getHeight() / 2) - 10,
                    20, 20, 0, 0, 20, 20
            );
            UContext.resetTextureAlpha();
        }

        if (deleteProgressPercent != 0f) {
            UContext.fillZone(getX(), getY(), (int) (getWidth() * deleteProgressPercent), getHeight(), 0x7fFF0000);
        }

    }

    private Component getSubMessage() {
        return Component.translatable("omm.raster.type." + switch (layerType) {
            case BASE -> "base-layer";
            case OVERLAY -> "overlay";
            case LOCAL_GEN -> "local-gen";
        });
    }

    private boolean mouseIsOverKey(int mouseX, int mouseY) {
        return mouseX > getX() + 3 && mouseX < getX() + 19 && mouseY < getBottom() - 3 && mouseY > getBottom() - 19;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof BaseRasterScreen) {
            if (click.y() > BaseRasterScreen.getInstance().rasterList.getBottom()) {
                BaseRasterScreen.confirmButton.mouseClicked(click, doubled);
                return false;
            }
        }

        if (isAddButton) {
            if (currentScreen instanceof ViewSetRastersScreen) {
                Minecraft.getInstance().setScreen(new OverlayRasterScreen(true));
            }
            if (currentScreen instanceof BaseRasterScreen || currentScreen instanceof OverlayRasterScreen) {
                CreateRasterScreen.layerType = (currentScreen instanceof BaseRasterScreen ? LayerType.BASE : LayerType.OVERLAY);
                if (ConfigOptions._RASTER_WARNING_ACCEPTED.getAsBoolean()) Minecraft.getInstance().setScreen(new CreateRasterScreen(null)); //conditional should be a config setting for if the warning screen was passed already
                else Minecraft.getInstance().setScreen(new RasterWarningScreen());
            }
        }

        for (MicroButton mButton : microButtons) {
            if (mButton.isMouseOver(click.x(), click.y())) {
                mButton.onClick(click, doubled);
                return false;
            }
        }

        if (opacitySlider != null) if (opacitySlider.isMouseOver(click.x(), click.y())) {
            opacitySlider.onClick(click, doubled);
            return false;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        if (isHovered() && showKey && mouseIsOverKey((int) click.x(), (int) click.y())) {
            MapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki/Rasters#api-keys", Minecraft.getInstance().screen, false);
        }
        super.onClick(click, doubled);
    }

    public void releaseSlider(double mouseX, double mouseY) {
        if (opacitySlider == null) return;
        opacitySlider.onRelease(null);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (opacitySlider != null) {
            opacitySlider.onRelease(null);
            return false;
        }

        for (MicroButton mButton : microButtons) {
            if (mButton.buttonFunction != MicroButtonFunction.DELETE) continue;
            if (mButton.isMouseOver(click.x(), click.y())) {
                mButton.onRelease(click);
                return false;
            }
        }

        return super.mouseReleased(click);
    }
}
