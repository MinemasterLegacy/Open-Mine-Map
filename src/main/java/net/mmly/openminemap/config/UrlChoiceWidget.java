package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
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
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

public class UrlChoiceWidget extends TextFieldWidget {

    //private static final Identifier upArrow = Identifier.of("minecraft", "textures/");
    private final SelectArrow upArrowWidget;
    private final SelectArrow downArrowWidget;
    protected int currentUrlId;

    public UrlChoiceWidget(TextRenderer textRenderer, int x, int y, int width, int height) {
        super(textRenderer, x, y, width, height, Text.of(""));
        this.setEditable(false);
        this.setMaxLength(1000);
        this.setUneditableColor(14737632);
        upArrowWidget = new SelectArrow(ArrowDirection.up, this);
        downArrowWidget = new SelectArrow(ArrowDirection.down, this);
        this.setTooltip(Tooltip.of(Text.of("Set a custom URL for tiles to be loaded from. Click for more information.")));
        refreshText();
        this.currentUrlId = TileUrlFile.getCurrentUrlId();
    }

    protected void writeParameterToFile() {
        ConfigFile.writeParameter(ConfigOptions.TILE_MAP_URL, TileUrlFile.getTileUrl(currentUrlId).name);
        TileUrlFile.setCurrentUrl(currentUrlId);
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
        super.renderWidget(context, mouseX, mouseY, delta);
        refreshText();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        FullscreenMapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki/Configuration#tile-source", ConfigScreen.getInstance());
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        upArrowWidget.setY(y);
        downArrowWidget.setY(y);
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

    protected SelectArrow(ArrowDirection direction, UrlChoiceWidget choiceWidget) {
        super(choiceWidget.getX() + choiceWidget.getWidth() + 3, choiceWidget.getY() + ArrowDirection.getHeightMod(direction) + 2, 11, 7, Text.of(""));
        //System.out.println(getY());
        arrow = Identifier.of("openminemap", "arrowselect/"+direction+".png");
        arrowSelected = Identifier.of("openminemap", "arrowselect/"+direction+"selected.png");
        this.direction = direction;
        this.choiceWidget = choiceWidget;
        if (direction == ArrowDirection.up) setTooltip(Tooltip.of(Text.of("Previous Source")));
        if (direction == ArrowDirection.down) setTooltip(Tooltip.of(Text.of("Next Source")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (direction == ArrowDirection.up) choiceWidget.changeToPreviousUrl();
        if (direction == ArrowDirection.down) choiceWidget.changeToNextUrl();
        choiceWidget.refreshText();
    }

    @Override
    public void setY(int y) {
        super.setY(y + ArrowDirection.getHeightMod(direction) + 2);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFFFFFFF);
        context.drawTexture(RenderLayer::getGuiTextured, isHovered() ? arrowSelected : arrow, getX(), getY(),0, 0, getWidth(), getHeight(), 11, 7);
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

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