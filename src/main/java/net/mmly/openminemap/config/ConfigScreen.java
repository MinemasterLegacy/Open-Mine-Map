package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.gui.TextFieldLayer;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;

import java.util.ArrayList;

public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Text.of("OMM Config"));
    }

    static ConfigScreen configScreen;
    private static final int BOTTOM_SPACE = 40;
    private static final int BOTTOM_BUTTON_OFFSET = 32;
    private static int windowHeight;
    private static int windowWidth;
    public static int windowScaledHeight;
    public static int windowScaledWidth;
    private static Identifier[][] buttonIdentifiers = new Identifier[3][2];

    int nextOptionSlot;
    int totalOptions;
    private int scrollRange;
    private int currentScroll = 0;
    private int maxScroll;
    private final int SCROLLSPEED = 5;

    private static WikiLinkLayer wikiLinkLayer;
    private static ButtonLayer exitButtonLayer;
    private static ButtonLayer checkButtonLayer;
    TextWidget versionLabel;
    ButtonWidget configHud;

    CategoryLabelWidget generalLabel;
    ChoiceButtonWidget artificialZoomOption;
    public static TextFieldWidget snapAngleWidget;
    ChoiceButtonWidget rightClickMeuUsesOption;
    ChoiceButtonWidget reverseScrollOption;
    ChoiceSliderWidget zoomStrengthWidget;
    ChoiceButtonWidget hoverNamesOption;

    TextWidget overlayLabel;
    ChoiceSliderWidget playerShowSlider;
    ChoiceSliderWidget directionIndicatorShowSlider;
    ChoiceButtonWidget altitudeShadingOption;

    TextWidget urlLabel;
    private static UrlChoiceWidget definedUrlWidget;
    private final String[] zoomStrengthLevels = new String[] {
            "0.05", "0.1", "0.15", "0.2", "0.25",
            "0.3", "0.35", "0.4", "0.45", "0.5",
            "0.55", "0.6", "0.65", "0.7", "0.75",
            "0.8", "0.85", "0.9", "0.95", "1.0",
            "1.05", "1.1", "1.15", "1.2", "1.25",
            "1.3", "1.35", "1.4", "1.45", "1.5",
            "1.55", "1.6", "1.65", "1.7", "1.75",
            "1.8", "1.85", "1.9", "1.95", "2.0"
    }; // I know this is a lazy solution, but it's also the easiest (:

    /*
        each button/text field is 20 tall, with a buffer zome of 5 between buttons.
        The top and bottom of the screen have a padding of 20.
     */

    protected static final int buttonSize = 20;

    Window window;
    public static ButtonWidget toggleArtificialZoomButton;

    ConfigList configList;
    ArrayList<ClickableWidget> choiceWidgets = new ArrayList<>();
    ArrayList<ConfigAnchorWidget> anchorWidgets = new ArrayList<>();

    @Override
    public void close() {
        super.close();
        MinecraftClient.getInstance().setScreen(
                new FullscreenMapScreen()
        );
    }
    public static ConfigScreen getInstance() {
        return configScreen;
    }

    private int getNextOptionSlot() {
        totalOptions++;
        nextOptionSlot += 25;
        return nextOptionSlot;
    }

    private void updateScreenDims() {
        window = MinecraftClient.getInstance().getWindow();
        windowHeight = window.getHeight();
        windowWidth = window.getWidth();
        windowScaledHeight = window.getScaledHeight();
        windowScaledWidth = window.getScaledWidth();
    }

    static protected void updateTileSet() {
        String path;
        String[] names = new String[] {"check.png", "exit.png"};
        String[] states = new String[] {"locked/", "default/", "hover/"};
        path = "buttons/vanilla/";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                buttonIdentifiers[i][j] = Identifier.of("openminemap", path + states[i] + names[j]);
            }
        }
    }

    private void updateScrollPositions(int change) {
        currentScroll -= change;
        generalLabel.setY(generalLabel.getY() + change);
        configHud.setY(configHud.getY() + change);
        artificialZoomOption.getButtonWidget().setY(artificialZoomOption.getButtonWidget().getY() + change);
       // customUrlWidget.setY(customUrlWidget.getY() + change);
        snapAngleWidget.setY(snapAngleWidget.getY() + change);
        rightClickMeuUsesOption.getButtonWidget().setY(rightClickMeuUsesOption.getButtonWidget().getY() + change);
        reverseScrollOption.getButtonWidget().setY(reverseScrollOption.getButtonWidget().getY() + change);
        zoomStrengthWidget.setY(zoomStrengthWidget.getY() + change);
        hoverNamesOption.getButtonWidget().setY(hoverNamesOption.getButtonWidget().getY() + change);
        overlayLabel.setY(overlayLabel.getY() + change);
        playerShowSlider.setY(playerShowSlider.getY() + change);
        directionIndicatorShowSlider.setY(directionIndicatorShowSlider.getY() + change);
        altitudeShadingOption.getButtonWidget().setY(altitudeShadingOption.getButtonWidget().getY() + change);
        urlLabel.setY(urlLabel.getY() + change);
        definedUrlWidget.setY(definedUrlWidget.getY() + change);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean b = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        maxScroll = Math.max(scrollRange - windowScaledHeight, 0);
        //System.out.println(verticalAmount);
        if (TileManager.doReverseScroll) verticalAmount *= -1;
        if (verticalAmount < 0) { //down
            if (currentScroll + SCROLLSPEED < maxScroll) {
                updateScrollPositions(-SCROLLSPEED);
            } else {
                updateScrollPositions(currentScroll - maxScroll);
            }
        } else /*verticalAmount > 0*/ { //up
            if (currentScroll - SCROLLSPEED > 0) {
                updateScrollPositions(SCROLLSPEED);
            } else {
                updateScrollPositions(currentScroll);
            }
        }
        //System.out.println(": "+maxScroll+", "+currentScroll+", "+windowScaledHeight+", "+scrollRange);
        //System.out.println(currentScroll);
        return b;
    }

    private void addConfigOptionWidget(ClickableWidget widget) {
        if (!ConfigChoice.class.isAssignableFrom(widget.getClass())) return;
        choiceWidgets.add(widget);
        ConfigAnchorWidget anchor = new ConfigAnchorWidget();
        configList.addEntry(anchor);
        anchorWidgets.add(anchor);
        ((ConfigChoice) widget).setAnchor(anchor);
        anchor.setWidget(widget);
        this.addDrawableChild(widget);
    }

    @Override
    protected void init() {
        totalOptions = 0;
        nextOptionSlot = -5;
        configScreen = this;

        updateTileSet();
        updateScreenDims();

        configList = new ConfigList(MinecraftClient.getInstance(), 0, 0, 0, 24);
        configList.setWidth(windowScaledWidth);
        configList.setHeight(windowScaledHeight - BOTTOM_SPACE);
        this.addDrawableChild(configList);

        wikiLinkLayer = new WikiLinkLayer(0, 0);
        this.addDrawableChild(wikiLinkLayer);

        exitButtonLayer = new ButtonLayer(windowScaledWidth - 22, (windowScaledHeight / 2) - BOTTOM_BUTTON_OFFSET, buttonSize, buttonSize, ButtonFunction.EXIT);
        checkButtonLayer = new ButtonLayer(windowScaledWidth + 2, (windowScaledHeight / 2) - BOTTOM_BUTTON_OFFSET, buttonSize, buttonSize, ButtonFunction.CHECKMARK);
        exitButtonLayer.setTooltip(Tooltip.of(Text.translatable("omm.config.gui.exit-without-saving")));
        checkButtonLayer.setTooltip(Tooltip.of(Text.translatable("omm.config.gui.save-and-exit")));
        this.addDrawableChild(exitButtonLayer);
        this.addDrawableChild(checkButtonLayer);

        versionLabel = new TextWidget(0, windowScaledHeight - 20, windowScaledWidth - 5, 20, Text.of("OpenMineMap v" + OpenMineMapClient.MODVERSION), this.textRenderer);
        versionLabel.alignRight();
        this.addDrawableChild(versionLabel);

        configHud = ButtonWidget.builder(Text.translatable("omm.config.option.configure-hud"), (btn) -> {
                this.saveChanges();
                MinecraftClient.getInstance().setScreen(new MapConfigScreen());
        }).dimensions(20, windowScaledHeight - 35, 120, 20).build();
        configHud.setTooltip(Tooltip.of(Text.translatable("omm.config.tooltip.configure-hud")));
        this.addDrawableChild(configHud);

        generalLabel = new CategoryLabelWidget(Text.translatable("omm.config.category.general"), this.textRenderer);
        this.addConfigOptionWidget(generalLabel);

        artificialZoomOption = new ChoiceButtonWidget(Text.translatable("omm.config.option.artificial-zoom"), Text.of(Text.translatable("omm.config.tooltip.artificial-zoom")), new String[] {"Off", "On"}, ConfigOptions.ARTIFICIAL_ZOOM);
        this.addConfigOptionWidget(artificialZoomOption);

        snapAngleWidget = new TextFieldLayer(this.textRenderer, 20, getNextOptionSlot(), 120, 20, Text.translatable("omm.config.option.snap-angle"), 0);
        snapAngleWidget.setMaxLength(50);
        snapAngleWidget.setText(ConfigFile.readParameter(ConfigOptions.SNAP_ANGLE));
        snapAngleWidget.setTooltip(Tooltip.of(Text.translatable("omm.config.tooltip.snap-angle")));
        this.addDrawableChild(snapAngleWidget);

        rightClickMeuUsesOption = new ChoiceButtonWidget(Text.translatable("omm.config.option.rcm-uses"), Text.translatable("omm.config.tooltip.rcm-uses"), new String[] {"/tpll", "/tp"}, ConfigOptions.RIGHT_CLICK_MENU_USES);
        this.addConfigOptionWidget(rightClickMeuUsesOption);

        reverseScrollOption = new ChoiceButtonWidget(Text.translatable("omm.config.option.reverse-scroll"), Text.translatable("omm.config.tooltip.reverse-scroll"), new String[] {"Off", "On"}, ConfigOptions.REVERSE_SCROLL);
        this.addConfigOptionWidget(reverseScrollOption);

        zoomStrengthWidget = new ChoiceSliderWidget(Text.translatable("omm.config.option.zoom-strength"), Text.translatable("omm.config.tooltip.zoom-strength"), zoomStrengthLevels, ConfigOptions.ZOOM_STRENGTH);
        this.addConfigOptionWidget(zoomStrengthWidget);

        hoverNamesOption = new ChoiceButtonWidget(Text.translatable("omm.config.option.hover-names"), Text.translatable("omm.config.tooltip.hover-names"), new String[] {"Off", "On"}, ConfigOptions.HOVER_NAMES);
        this.addConfigOptionWidget(hoverNamesOption);

        overlayLabel = new CategoryLabelWidget(Text.translatable("omm.config.category.overlays"), this.textRenderer);
        this.addConfigOptionWidget(overlayLabel);

        playerShowSlider = new ChoiceSliderWidget(Text.translatable("omm.config.option.players"), Text.translatable("omm.config.tooltip.players"), new String[] {"None", "Self", "Local"}, ConfigOptions.SHOW_PLAYERS);
        this.addConfigOptionWidget(playerShowSlider);

        directionIndicatorShowSlider = new ChoiceSliderWidget(Text.translatable("omm.config.option.directions"), Text.translatable("omm.config.tooltip.directions"), new String[] {"None", "Self", "Local"}, ConfigOptions.SHOW_DIRECTION_INDICATORS);
        this.addConfigOptionWidget(directionIndicatorShowSlider);

        altitudeShadingOption = new ChoiceButtonWidget(Text.translatable("omm.config.option.altitude-shading"),  Text.translatable("omm.config.tooltip.altitude-shading"), new String[] {"On", "Off"}, ConfigOptions.ALTITUDE_SHADING);
        this.addConfigOptionWidget(altitudeShadingOption);

        urlLabel = new CategoryLabelWidget(Text.translatable("omm.config.category.tile-source"), this.textRenderer);
        this.addConfigOptionWidget(urlLabel);

        definedUrlWidget = new UrlChoiceWidget(this.textRenderer, 20, getNextOptionSlot(), 120, 20);
        this.addDrawableChild(definedUrlWidget);
        this.addDrawableChild(definedUrlWidget.getUpArrowWidget());
        this.addDrawableChild(definedUrlWidget.getDownArrowWidget());

        scrollRange = totalOptions * 25 + 35;
        currentScroll = 0;
    }

    public void saveChanges() {

        if (definedUrlWidget.currentUrlId != TileUrlFile.getCurrentUrlId()) {
            definedUrlWidget.writeParameterToFile();
            TileManager.setCacheDir();
            TileManager.themeColor = 0xFF808080;
        }

        /*
        if (!Objects.equals(ConfigFile.readParameter(ConfigOptions.TILE_MAP_URL), customUrlWidget.getText())) {
            //System.out.println("yea");
            TileManager.clearCacheDir();
        }

         */
        String snapAngle;
        try {
            snapAngle = Double.toString(Double.parseDouble(snapAngleWidget.getText())); //will ensure that the snap angle is a number
        } catch (NumberFormatException e) {
            snapAngle = "";
        }
        //ConfigFile.writeParameter(ConfigOptions.TILE_MAP_URL, customUrlWidget.getText());
        ConfigFile.writeParameter(ConfigOptions.SNAP_ANGLE, snapAngle);
        //ConfigFile.writeParameter("ArtificialZoom", Boolean.toString(doArtificialZoom));
        rightClickMeuUsesOption.writeParameterToFile();
        artificialZoomOption.writeParameterToFile();
        reverseScrollOption.writeParameterToFile();
        zoomStrengthWidget.writeParameterToFile();
        hoverNamesOption.writeParameterToFile();
        playerShowSlider.writeParameterToFile();
        directionIndicatorShowSlider.writeParameterToFile();
        altitudeShadingOption.writeParameterToFile();
        if (!ConfigFile.readParameter(ConfigOptions.ARTIFICIAL_ZOOM).equals("on")) {
            FullscreenMapScreen.clampZoom();
            HudMap.clampZoom();
        }

        TileManager.initializeConfigParameters();
        HudMap.setSnapAngle();
        ConfigFile.writeToFile();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateScreenDims();
        UContext.setContext(context);

        wikiLinkLayer.setPosition(windowScaledWidth - wikiLinkLayer.getWidth(), windowScaledHeight - 32);
        exitButtonLayer.setPosition(windowScaledWidth / 2 - 22, windowScaledHeight - BOTTOM_BUTTON_OFFSET);
        checkButtonLayer.setPosition(windowScaledWidth / 2 + 2, windowScaledHeight - BOTTOM_BUTTON_OFFSET);
        configHud.setY(windowScaledHeight - BOTTOM_BUTTON_OFFSET);

        //context.enableScissor(0, 0, windowScaledWidth, windowScaledHeight - BOTTOM_SPACE);
        super.render(context, mouseX, mouseY, delta);
        //context.disableScissor();

        wikiLinkLayer.drawWidget(context, textRenderer);
        context.drawTexture(checkButtonLayer.isHovered() ? buttonIdentifiers[2][0] : buttonIdentifiers[1][0], checkButtonLayer.getX(), checkButtonLayer.getY(), 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(exitButtonLayer.isHovered() ? buttonIdentifiers[2][1] : buttonIdentifiers[1][1], exitButtonLayer.getX(), exitButtonLayer.getY(), 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);

    }
}
