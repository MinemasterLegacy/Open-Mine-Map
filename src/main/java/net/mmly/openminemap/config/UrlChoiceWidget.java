package net.mmly.openminemap.config;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;

public class UrlChoiceWidget extends TextFieldWidget implements ConfigChoice{

    //private static final Identifier upArrow = Identifier.of("minecraft", "textures/");
    private final SelectArrow upArrowWidget;
    private final SelectArrow downArrowWidget;
    protected int currentUrlId;
    ConfigAnchorWidget anchor;

    public UrlChoiceWidget(TextRenderer textRenderer) {
        super(textRenderer, 0, -100, 200, 20, Text.of(""));
        this.setEditable(false);
        this.setMaxLength(1000);
        this.setUneditableColor(-2039584);
        upArrowWidget = new SelectArrow(ArrowDirection.up, this);
        downArrowWidget = new SelectArrow(ArrowDirection.down, this);
        this.setTooltip(Tooltip.of(Text.translatable("omm.config.tooltip.tile-source")));
        refreshText();
        this.currentUrlId = TileUrlFile.getCurrentUrlId();
    }

    @Override
    public void setAnchor(ConfigAnchorWidget anchor) {
        this.anchor = anchor;
    }

     public void writeParameterToFile() {
         if (currentUrlId != TileUrlFile.getCurrentUrlId()) {
             ConfigFile.writeParameter(ConfigOptions.TILE_MAP_URL, TileUrlFile.getTileUrl(currentUrlId).name);
             TileUrlFile.setCurrentUrl(currentUrlId);
             TileManager.setCacheDir();
             TileManager.themeColor = 0xFF808080;
         }

    }

    protected void changeToNextUrl() {
        if (TileUrlFile.getCurrentIdRange() - 1 == currentUrlId) currentUrlId = 0;
        else currentUrlId++;
    }

    protected void changeToPreviousUrl() {
        if (currentUrlId == 0) currentUrlId = TileUrlFile.getCurrentIdRange() - 1;
        else currentUrlId--;
    }

    protected void refreshText() {
        /*if ((TileUrlFile.getTileUrl(currentUrlId).name != null) && (!isHovered())) this.setText(TileUrlFile.getTileUrl(currentUrlId).name);
        else this.setText(TileUrlFile.getTileUrl(currentUrlId).source_url);*/
        this.setText(TileUrlFile.getTileUrl(currentUrlId).name);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) {return; }
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        refreshText();
        upArrowWidget.refreshPosition();
        downArrowWidget.refreshPosition();
        upArrowWidget.drawWidget(context);
        downArrowWidget.drawWidget(context);
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        FullscreenMapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki/Configuration#tile-source", ConfigScreen.getInstance(), false);
    }

    @Override
    public void setDimensionsAndPosition(int width, int height, int x, int y) {
        super.setDimensionsAndPosition(width, height, x, y);
    }

    public SelectArrow getUpArrowWidget() {
        return upArrowWidget;
    }

    public SelectArrow getDownArrowWidget() {
        return downArrowWidget;
    }
}

class SelectArrow extends ClickableWidget {

    private final Identifier arrow;
    private final Identifier arrowSelected;
    private final ArrowDirection direction;
    private final UrlChoiceWidget choiceWidget;
    private static final int TEXTURE_WIDTH = 11;
    private static final int TEXTURE_HEIGHT = 7;

    protected SelectArrow(ArrowDirection direction, UrlChoiceWidget choiceWidget) {
        super(0,0, TEXTURE_WIDTH, 0, Text.of(""));
        //System.out.println(getY());
        arrow = Identifier.of("openminemap", "arrowselect/"+direction+".png");
        arrowSelected = Identifier.of("openminemap", "arrowselect/"+direction+"selected.png");
        this.direction = direction;
        this.choiceWidget = choiceWidget;
        if (direction == ArrowDirection.up) setTooltip(Tooltip.of(Text.translatable("omm.config.gui.previous-source")));
        if (direction == ArrowDirection.down) setTooltip(Tooltip.of(Text.translatable("omm.config.gui.next-source")));
        refreshPosition();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (direction == ArrowDirection.up) choiceWidget.changeToPreviousUrl();
        if (direction == ArrowDirection.down) choiceWidget.changeToNextUrl();
        choiceWidget.refreshText();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    protected void drawWidget(DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, isHovered() ? arrowSelected : arrow, getX(), getY(),0, 0, width, height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        //context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x800000FF);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    protected void refreshPosition() {
        setPosition(
                choiceWidget.getX() - TEXTURE_WIDTH - 3,
                choiceWidget.getY() + ArrowDirection.getHeightMod(direction) + 2
        );
        setHeight(Math.clamp(ConfigScreen.getConfigListBottom() - getY(), 0, TEXTURE_HEIGHT));
    }
}

enum ArrowDirection{
    up,
    down;

    public static int getHeightMod(ArrowDirection direction) {
        if (direction == ArrowDirection.down) return 9;
        else return 0;
    }
}