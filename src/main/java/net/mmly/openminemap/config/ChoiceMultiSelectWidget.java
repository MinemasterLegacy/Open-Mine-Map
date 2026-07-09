package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.WebIcon;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.util.ConfigFile;

import java.util.ArrayList;
import java.util.List;

public class ChoiceMultiSelectWidget extends ClickableWidget implements ConfigChoice{

    AnchorWidget anchor;
    private final ArrayList<WebIcon> highlighted = new ArrayList<>();
    private int[] iconPositions;
    List<WebIcon> options;
    int highlightedIcon = -1;

    public ChoiceMultiSelectWidget(List<WebIcon> options, ConfigOptions configOptions) {
        super(0, -100, 200, 20, Text.of(""));

        this.options = options;

        for (String s : ConfigOptions.WEB_OPTIONS.getAsString().split(",")) {
            WebIcon icon = WebIcon.getEnumFromName(s);
            if (icon != null) highlighted.add(icon);
        }

    }

    private void calculateIconPositions() {
        iconPositions = new int[options.size()];

        int numIcons = options.size();
        double spacePerIcon = (double) (width - 2) / numIcons;
        int margin = (int) ((spacePerIcon - 12) / 2);

        for (int i = 0; i < iconPositions.length; i++) {
            iconPositions[i] = (int) (1 + (i * spacePerIcon) + margin);
        }
    }

    private int getIconPosition(WebIcon icon) {
        return iconPositions[options.indexOf(icon)];
    }

    @Override
    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    @Override
    public void writeParameterToFile() {
        StringBuilder value = new StringBuilder();
        for (WebIcon icon : WebIcon.ORDERED_LIST) {
            if (highlighted.contains(icon)) value.append(icon.imageName).append(",");
        }
        if (!value.isEmpty()) value.deleteCharAt(value.length()-1);
        ConfigFile.writeParameter(ConfigOptions.WEB_OPTIONS, value.toString());
    }

    private void calculateHighlightedIcon(int mouseX, int mouseY) {
        for (int i = 0; i < options.size(); i++) {
            if (getX() + iconPositions[i] < mouseX && getY() + 2 < mouseY && getX() + iconPositions[i] + 12 >= mouseX && getBottom() - 2 >= mouseY) {
                highlightedIcon = i;
                return;
            }
        }
        highlightedIcon = -1;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (highlightedIcon < 0) return;
        WebIcon icon = options.get(highlightedIcon);
        if (highlighted.contains(icon)) highlighted.remove(icon);
        else highlighted.add(icon);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();

        if (iconPositions == null) calculateIconPositions();
        calculateHighlightedIcon(mouseX, mouseY);

        UContext.setContext(context);
        context.fill(getX(), getY(), getRight(), getBottom(), 0xFF000000);
        context.enableScissor(getX(), getY(), getRight(), getBottom());

        for (WebIcon icon : WebIcon.ORDERED_LIST) {
            if (highlighted.contains(icon) || options.indexOf(icon) == highlightedIcon) {
                UContext.drawTexture(icon.highlight, getX() + getIconPosition(icon), getY() + 2, 12, 16);
            }
            UContext.drawTexture(icon.icon, getX() + getIconPosition(icon) + 1, getY() + 3, 10, 14);
        }
        UContext.borderWidget(this, isFocused() ? 0xFFFFFFFF : 0xFF7f7f7f);
        context.disableScissor();

        if (highlightedIcon > -1) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, options.get(highlightedIcon).tooltipText, mouseX, mouseY);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
