package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.Requester;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;

import java.util.ArrayList;

public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Text.of("OMM Config"));
    }

    static ConfigScreen configScreen;
    private static final int BOTTOM_SPACE = 40;
    private static final int BOTTOM_BUTTON_OFFSET = 30;
    public static int windowScaledHeight;
    public static int windowScaledWidth;

    private static WikiLinkLayer wikiLinkLayer;
    private static ButtonLayer exitButtonLayer;
    private static ButtonLayer checkButtonLayer;
    TextWidget versionLabel;
    ButtonWidget configHud;

    CategoryLabelWidget generalLabel;
    ChoiceButtonWidget artificialZoomOption;
    ChoiceNumberWidget snapAngleWidget;
    ChoiceButtonWidget rightClickMeuUsesOption;
    ChoiceSliderWidget tileScaleSlider;
    ChoiceButtonWidget reverseScrollOption;
    ChoiceSliderWidget zoomStrengthSlider;

    CategoryLabelWidget overlayLabel;
    ChoiceSliderWidget playerShowSlider;
    ChoiceSliderWidget directionIndicatorShowSlider;
    ChoiceSliderWidget playerSizeSlider;
    ChoiceSliderWidget waypointSizeSlider;
    ChoiceButtonWidget hoverNamesOption;
    ChoiceButtonWidget altitudeShadingOption;

    CategoryLabelWidget urlLabel;
    private static UrlChoiceWidget definedUrlWidget;

    CategoryLabelWidget interfaceLabel;
    ChoiceSliderWidget transparencySlider;
    ChoiceButtonWidget showConnectionStatusOption;

    private final String[] onOffOptions = new String[] {"On", "Off"};
    private final String[] showHideOptions = new String[] {"Show", "Hide"};
    private final String[] booleanOptions = new String[] {"false", "true"};
    private final String[] visibilityOptions = new String[] {"None", "Self", "Local", "All"};
    private final String[] sizeOptions = new String[] {"Small", "Normal", "Large"};
    private final String[] zoomStrengthOptions = new String[] {
            "0.05", "0.1", "0.15", "0.2", "0.25",
            "0.3", "0.35", "0.4", "0.45", "0.5",
            "0.55", "0.6", "0.65", "0.7", "0.75",
            "0.8", "0.85", "0.9", "0.95", "1.0",
            "1.05", "1.1", "1.15", "1.2", "1.25",
            "1.3", "1.35", "1.4", "1.45", "1.5",
            "1.55", "1.6", "1.65", "1.7", "1.75",
            "1.8", "1.85", "1.9", "1.95", "2.0"
    }; // I know this is a lazy solution, but it's also the easiest (:
    private final String[] decimalPercentOptions = new String[] {
            "0.0",
            "0.05", "0.1", "0.15", "0.2", "0.25",
            "0.3", "0.35", "0.4", "0.45", "0.5",
            "0.55", "0.6", "0.65", "0.7", "0.75",
            "0.8", "0.85", "0.9", "0.95", "1.0"
    };
    private final String[] tileScaleOptions = new String[] {
            "64", "72", "80", "88", "96",
            "104", "112", "120", "128", "136",
            "144", "152", "160", "168", "176",
            "184", "192", "200", "208", "216",
            "224", "232", "240", "248", "256"
    };

    /*
        each button/text field is 20 tall, with a buffer zome of 5 between buttons.
        The top and bottom of the screen have a padding of 20.
     */

    Window window;
    public static ButtonWidget toggleArtificialZoomButton;

    static ConfigList configList;
    ArrayList<ClickableWidget> choiceWidgets = new ArrayList<>();
    ArrayList<ConfigAnchorWidget> anchorWidgets = new ArrayList<>();

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(
                new FullscreenMapScreen()
        );
    }

    public static ConfigScreen getInstance() {
        return configScreen;
    }
    public static int getConfigListBottom() {
        return configList.getBottom();
    }

    private void updateScreenDims() {
        window = MinecraftClient.getInstance().getWindow();
        windowScaledHeight = window.getScaledHeight();
        windowScaledWidth = window.getScaledWidth();
    }

    private void addConfigOptionWidget(ClickableWidget widget) {
        if (!ConfigChoice.class.isAssignableFrom(widget.getClass())) return;
        choiceWidgets.add(widget);
        ConfigAnchorWidget anchor = new ConfigAnchorWidget();
        this.addDrawableChild(widget);

        configList.addEntry(anchor);
        anchorWidgets.add(anchor);
        ((ConfigChoice) widget).setAnchor(anchor);
        anchor.setWidget(widget);
    }

    @Override
    protected void init() {
        configScreen = this;

        updateScreenDims();

        configList = new ConfigList(MinecraftClient.getInstance(), 0, 0, 0, 24);
        configList.setWidth(windowScaledWidth);
        configList.setHeight(windowScaledHeight - BOTTOM_SPACE);
        this.addDrawableChild(configList);

        wikiLinkLayer = new WikiLinkLayer(0, 0);
        this.addDrawableChild(wikiLinkLayer);

        exitButtonLayer = new ButtonLayer(windowScaledWidth - 22, (windowScaledHeight / 2) - BOTTOM_BUTTON_OFFSET, ButtonFunction.EXIT);
        checkButtonLayer = new ButtonLayer(windowScaledWidth + 2, (windowScaledHeight / 2) - BOTTOM_BUTTON_OFFSET, ButtonFunction.CHECKMARK);
        exitButtonLayer.setTooltip(Tooltip.of(Text.translatable("omm.config.gui.exit-without-saving")));
        checkButtonLayer.setTooltip(Tooltip.of(Text.translatable("omm.config.gui.save-and-exit")));
        this.addDrawableChild(exitButtonLayer);
        this.addDrawableChild(checkButtonLayer);

        //versionLabel = new TextWidget(0, windowScaledHeight - 16, windowScaledWidth - 5, 9, Text.of("OpenMineMap v" + OpenMineMapClient.MODVERSION), this.textRenderer);
        //versionLabel.alignRight();
        //this.addDrawableChild(versionLabel);

        configHud = ButtonWidget.builder(Text.translatable("omm.config.option.configure-hud"), (btn) -> {
                this.saveChanges();
                MinecraftClient.getInstance().setScreen(new MapConfigScreen());
                FullscreenMapScreen.toggleAltScreenMap(false);
        }).dimensions(15, windowScaledHeight - 35, 120, 20).build();
        configHud.setTooltip(Tooltip.of(Text.translatable("omm.config.tooltip.configure-hud")));
        this.addDrawableChild(configHud);

        generalLabel = new CategoryLabelWidget(Text.translatable("omm.config.category.general"), this.textRenderer);
        this.addConfigOptionWidget(generalLabel);

        artificialZoomOption = new ChoiceButtonWidget(onOffOptions, ConfigOptions.ARTIFICIAL_ZOOM);
        this.addConfigOptionWidget(artificialZoomOption);

        snapAngleWidget = new ChoiceNumberWidget(textRenderer);
        this.addConfigOptionWidget(snapAngleWidget);

        rightClickMeuUsesOption = new ChoiceButtonWidget(new String[] {"/tpll", "/tp"}, ConfigOptions.RIGHT_CLICK_MENU_USES, true);
        this.addConfigOptionWidget(rightClickMeuUsesOption);

        tileScaleSlider = new ChoiceSliderWidget(tileScaleOptions, ConfigOptions.TILE_SCALE, true);
        this.addConfigOptionWidget(tileScaleSlider);

        reverseScrollOption = new ChoiceButtonWidget(onOffOptions, ConfigOptions.REVERSE_SCROLL);
        this.addConfigOptionWidget(reverseScrollOption);

        zoomStrengthSlider = new ChoiceSliderWidget(zoomStrengthOptions, ConfigOptions.ZOOM_STRENGTH, true);
        this.addConfigOptionWidget(zoomStrengthSlider);

        overlayLabel = new CategoryLabelWidget(Text.translatable("omm.config.category.overlays"), this.textRenderer);
        this.addConfigOptionWidget(overlayLabel);

        playerShowSlider = new ChoiceSliderWidget(visibilityOptions, ConfigOptions.SHOW_PLAYERS);
        this.addConfigOptionWidget(playerShowSlider);

        directionIndicatorShowSlider = new ChoiceSliderWidget(visibilityOptions, ConfigOptions.SHOW_DIRECTION_INDICATORS);
        this.addConfigOptionWidget(directionIndicatorShowSlider);

        playerSizeSlider = new ChoiceSliderWidget(sizeOptions, ConfigOptions.PLAYER_SIZE);
        this.addConfigOptionWidget(playerSizeSlider);

        waypointSizeSlider = new ChoiceSliderWidget(sizeOptions, ConfigOptions.WAYPOINT_SIZE);
        this.addConfigOptionWidget(waypointSizeSlider);

        hoverNamesOption = new ChoiceButtonWidget(showHideOptions, ConfigOptions.HOVER_NAMES);
        this.addConfigOptionWidget(hoverNamesOption);

        altitudeShadingOption = new ChoiceButtonWidget(onOffOptions, ConfigOptions.ALTITUDE_SHADING);
        this.addConfigOptionWidget(altitudeShadingOption);

        urlLabel = new CategoryLabelWidget(Text.translatable("omm.config.category.tile-source"), this.textRenderer);
        this.addConfigOptionWidget(urlLabel);

        definedUrlWidget = new UrlChoiceWidget(this.textRenderer);
        this.addConfigOptionWidget(definedUrlWidget);
        this.addDrawableChild(definedUrlWidget.getUpArrowWidget());
        this.addDrawableChild(definedUrlWidget.getDownArrowWidget());

        interfaceLabel = new CategoryLabelWidget(Text.of("Interface"), this.textRenderer);
        this.addConfigOptionWidget(interfaceLabel);

        transparencySlider = new ChoiceSliderWidget(decimalPercentOptions, ConfigOptions.INTERFACE_OPACITY, true);
        this.addConfigOptionWidget(transparencySlider);

        showConnectionStatusOption = new ChoiceButtonWidget(showHideOptions, ConfigOptions.SHOW_CONNECTION_STATUS);
        this.addConfigOptionWidget(showConnectionStatusOption);

        if (OpenMineMapClient.SHOWDEVELOPEROPTIONS) {
            this.addConfigOptionWidget(new CategoryLabelWidget(Text.of("Developer"), this.textRenderer));
            this.addConfigOptionWidget(new ChoiceButtonWidget(booleanOptions, ConfigOptions.__DISABLE_WEB_REQUESTS, true));
            this.addConfigOptionWidget(new ChoiceButtonWidget(booleanOptions, ConfigOptions.__SHOW_MEMORY_CACHE_SIZE, true));
            this.addConfigOptionWidget(new ChoiceButtonWidget(booleanOptions, ConfigOptions.__EXPERIMENTAL_CLAIMS_RENDERING, true));
        }

        configList.restoreScroll();
        FullscreenMapScreen.toggleAltScreenMap(true);

    }

    public UrlChoiceWidget getChoiceWidget() {
        return definedUrlWidget;
    }

    public void saveChanges() {
        for (ClickableWidget widget : choiceWidgets) {
            ((ConfigChoice) widget).writeParameterToFile();
        }
        if (!ConfigFile.readParameter(ConfigOptions.ARTIFICIAL_ZOOM).equals("on")) {
            FullscreenMapScreen.clampZoom();
            HudMap.clampZoom();
        }
        TileManager.initializeConfigParameters();
        OmmMap.initializeConfigParameters(true);
        HudMap.setSnapAngle();
        ConfigFile.writeToFile();
        Requester.disableWebRequests = Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions.__DISABLE_WEB_REQUESTS));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateScreenDims();
        UContext.setContext(context);

        wikiLinkLayer.setPosition(windowScaledWidth - wikiLinkLayer.getWidth(), windowScaledHeight - BOTTOM_SPACE + 7);
        exitButtonLayer.setPosition(windowScaledWidth / 2 - 22, windowScaledHeight - BOTTOM_BUTTON_OFFSET);
        checkButtonLayer.setPosition(windowScaledWidth / 2 + 2, windowScaledHeight - BOTTOM_BUTTON_OFFSET);
        configHud.setY(windowScaledHeight - BOTTOM_BUTTON_OFFSET);

        //context.enableScissor(0, 0, windowScaledWidth, windowScaledHeight - BOTTOM_SPACE);
        super.render(context, mouseX, mouseY, delta);
        //context.disableScissor();
        wikiLinkLayer.drawWidget(context, textRenderer);
        UContext.drawJustifiedText(Text.of("OpenMineMap v" + OpenMineMapClient.MODVERSION), Justify.RIGHT, windowScaledWidth - 5, windowScaledHeight - 16, 0xFFFFFFFF);
    }
}
