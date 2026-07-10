package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.mmly.openminemap.config.ConfigScreen;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonState;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.util.ColorUtil;
import net.mmly.openminemap.util.RasterProvider;

public class MicroButton extends ClickableWidget {

    public final MicroButtonFunction buttonFunction;
    private RasterLayerWidget parentWidget;
    public final LayerType layerType;
    private boolean apiKeyNeeded = false;
    private boolean disabaled = false;

    public MicroButton(int x, int y, MicroButtonFunction function, LayerType layerType) {
        super(x, y, 12, 12, Text.of(""));
        this.buttonFunction = function;
        this.layerType = layerType;
    }

    private void checkDisabled() {
        if (buttonFunction == MicroButtonFunction.UP) {
            disabaled = RasterProvider.isTopOverlay(parentWidget.raster);
        }
        if (buttonFunction == MicroButtonFunction.DOWN) {
            disabaled = RasterProvider.isBottomOverlay(parentWidget.raster);
        }
    }

    public void setParentWidget(RasterLayerWidget parentWidget) {
        this.parentWidget = parentWidget;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void draw(int mouseX, int mouseY) {
        checkDisabled();
        if (!ButtonLayer.texturedButtons) UContext.drawButtonOnWidget(this, disabaled, isMouseOver(mouseX, mouseY));
        UContext.drawTexture(
                buttonFunction.getTexture(disabaled ? ButtonState.LOCKED : isMouseOver(mouseX, mouseY) ? ButtonState.HOVER : ButtonState.DEFAULT),
                getX(), getY(), width, height, 12, 12);

        if (apiKeyNeeded) {
            drawFlash();
            if (parentWidget.isHovered()) UContext.getContext().drawTooltip(
                    MinecraftClient.getInstance().textRenderer,
                    Text.translatable("omm.raster.requires-api-key"),
                    mouseX,
                    mouseY
            );
        }

    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public void setApiKeyNeeded(boolean apiKeyNeeded) {
        this.apiKeyNeeded = apiKeyNeeded;
    }

    private void drawFlash() {
        int alpha = Math.abs(((int) (Util.getEpochTimeMs() >>> 3) % 256) + 128);
        int color = ColorUtil.setAlpha(alpha, 0xFFFFFFFF);
        if (ButtonLayer.texturedButtons) {
            UContext.fillZone(getX() + 1, getY(), getWidth() - 2, getHeight(), color);
            UContext.fillZone(getX(), getY() + 1, 1, getHeight() - 2, color);
            UContext.fillZone(getRight() - 1, getY() + 1, 1, getHeight() - 2, color);
        } else {
            UContext.fillWidget(this, color);
        }

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (disabaled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        switch (buttonFunction) {
            case EDIT: {
                switch (layerType) {
                    case BASE -> client.setScreen(new BaseRasterScreen());
                    case LOCAL_GEN -> {
                        client.setScreen(new ConfigScreen());
                        ConfigScreen.getInstance().scrollToOverlay();
                    }
                    case OVERLAY -> client.setScreen(new CreateRasterScreen(parentWidget.raster));
                }
                break;
            }
            case UP: {
                RasterProvider.moveForward(parentWidget.raster);
                //((ViewSetRastersScreen) MinecraftClient.getInstance().currentScreen).reloadRasterList();
                MinecraftClient.getInstance().setScreen(new ViewSetRastersScreen(false));
                break;
            }
            case DOWN: {
                RasterProvider.moveBackwards(parentWidget.raster);
                //((ViewSetRastersScreen) MinecraftClient.getInstance().currentScreen).reloadRasterList();
                MinecraftClient.getInstance().setScreen(new ViewSetRastersScreen(false));
                break;
            }
            case VISIBILITY: {
                RasterProvider.setVisibilityOf(parentWidget.raster, !RasterProvider.getVisibilityOf(parentWidget.raster));
                break;
            }
            case REMOVE: {
                RasterProvider.extractOverlay(parentWidget.raster);
                MinecraftClient.getInstance().setScreen(new ViewSetRastersScreen(false));
                break;
            }
            case INFO: {
                CreateRasterScreen.layerType = (MinecraftClient.getInstance().currentScreen instanceof BaseRasterScreen ? LayerType.BASE : LayerType.OVERLAY);
                MinecraftClient.getInstance().setScreen(new CreateRasterScreen(parentWidget.raster));
                //TODO
                break;
            }
        }
    }
}
