package net.mmly.openminemap.config;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.WebIcon;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.DrawableClaim;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.raster.ViewSetRastersScreen;
import net.mmly.openminemap.util.ConfigFile;

import java.util.ArrayList;

public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Component.nullToEmpty("OMM Config"));
        this.returnScreen = Minecraft.getInstance().gui.screen();
    }

    static ConfigScreen configScreen;
    private static final int BOTTOM_SPACE = 40;
    private static final int BOTTOM_BUTTON_OFFSET = 30;
    public static int windowScaledHeight;
    public static int windowScaledWidth;

    private static CreditLayer creditLayer;
    private static ButtonLayer exitButtonLayer;
    private static ButtonLayer checkButtonLayer;
    Button configHud;

    CategoryLabelWidget generalLabel;
    ChoiceButtonWidget artificialZoomOption;
    ChoiceNumberWidget snapAngleWidget;
    ChoiceButtonWidget rightClickMeuUsesOption;
    ChoiceSliderWidget tileScaleSlider;
    ChoiceButtonWidget reverseScrollOption;
    ChoiceSliderWidget zoomStrengthSlider;
    ChoiceButtonWidget teleportInterceptionOption;

    CategoryLabelWidget overlayLabel;
    ChoiceButtonWidget renderClaimsOption;
    ChoiceButtonWidget hiddenClaimsOption;
    ChoiceSliderWidget playerShowSlider;
    ChoiceSliderWidget directionIndicatorShowSlider;
    ChoiceSliderWidget playerSizeSlider;
    ChoiceSliderWidget waypointSizeSlider;
    ChoiceButtonWidget hoverNamesOption;
    ChoiceButtonWidget altitudeShadingOption;

    CategoryLabelWidget rasterLabel;
    private static RasterConfigWidget definedUrlWidget;

    CategoryLabelWidget interfaceLabel;
    ChoiceSliderWidget transparencySlider;
    ColorChoiceSliderWidget textColorSlider;
    ChoiceButtonWidget showConnectionStatusOption;
    ChoiceButtonWidget hudmapCompassOption;
    ChoiceButtonWidget hudmapBorderOption;
    ChoiceButtonWidget buttonStyleOption;
    ChoiceMultiSelectWidget webOptionsOption;
    ChoiceButtonWidget distortionDisplayOption;

    /*
        each button/text field is 20 tall, with a buffer zome of 5 between buttons.
        The top and bottom of the screen have a padding of 20.
     */

    Window window;
    private final Screen returnScreen;
    public static Button toggleArtificialZoomButton;
    private static final int ITEM_HEIGHT = 24;

    private int overlayLabelPosition;

    static ConfigList configList;
    ArrayList<AbstractWidget> choiceWidgets = new ArrayList<>();
    ArrayList<AnchorWidget> anchorWidgets = new ArrayList<>();

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(
                returnScreen instanceof ViewSetRastersScreen ? returnScreen : new MapScreen()
        );
        MapScreen.updateAltScreenMap(this);
    }

    public static ConfigScreen getInstance() {
        return configScreen;
    }
    public static int getConfigListBottom() {
        return configList.getBottom();
    }

    private void updateScreenDims() {
        window = Minecraft.getInstance().getWindow();
        windowScaledHeight = window.getGuiScaledHeight();
        windowScaledWidth = window.getGuiScaledWidth();
    }

    private void addConfigOptionWidget(AbstractWidget widget) {
        if (!ConfigChoice.class.isAssignableFrom(widget.getClass())) return;
        choiceWidgets.add(widget);
        AnchorWidget anchor = new AnchorWidget() {
            @Override
            public int getWidth() {
                return super.getWidth() - 4;
            }
        };
        this.addRenderableWidget(widget);

        configList.addEntry(anchor);
        anchorWidgets.add(anchor);
        ((ConfigChoice) widget).setAnchor(anchor);
        anchor.setWidget(widget);
    }

    public void scrollToOverlay() {
        configList.setScrollAmount(Math.max((overlayLabelPosition - 1.5) * ITEM_HEIGHT, 0));
    }

    @Override
    protected void init() {
        configScreen = this;

        updateScreenDims();

        configList = new ConfigList(Minecraft.getInstance(), 0, 0, 0, 24);
        configList.setWidth(windowScaledWidth);
        configList.setHeight(windowScaledHeight - BOTTOM_SPACE);
        this.addRenderableWidget(configList);

        creditLayer = new CreditLayer(0, 0);
        this.addRenderableWidget(creditLayer);

        exitButtonLayer = new ButtonLayer(windowScaledWidth - 22, (windowScaledHeight / 2) - BOTTOM_BUTTON_OFFSET, ButtonFunction.EXIT);
        checkButtonLayer = new ButtonLayer(windowScaledWidth + 2, (windowScaledHeight / 2) - BOTTOM_BUTTON_OFFSET, ButtonFunction.CHECKMARK);
        exitButtonLayer.setTooltip(Tooltip.create(Component.translatable("omm.config.gui.exit-without-saving")));
        checkButtonLayer.setTooltip(Tooltip.create(Component.translatable("omm.config.gui.save-and-exit")));
        this.addRenderableWidget(exitButtonLayer);
        this.addRenderableWidget(checkButtonLayer);

        //versionLabel = new TextWidget(0, windowScaledHeight - 16, windowScaledWidth - 5, 9, Text.of("OpenMineMap v" + OpenMineMapClient.MODVERSION), this.textRenderer);
        //versionLabel.alignRight();
        //this.addDrawableChild(versionLabel);

        configHud = Button.builder(Component.translatable("omm.config.option.configure-hud"), (btn) -> {
                this.saveChanges();
                Minecraft.getInstance().gui.setScreen(new MapConfigScreen());
                MapScreen.updateAltScreenMap(this);
        }).bounds(15, windowScaledHeight - 35, 120, 20).build();
        configHud.setTooltip(Tooltip.create(Component.translatable("omm.config.tooltip.configure-hud")));
        this.addRenderableWidget(configHud);

        generalLabel = new CategoryLabelWidget(Component.translatable("omm.config.category.general"), this.font);
        this.addConfigOptionWidget(generalLabel);

        artificialZoomOption = new ChoiceButtonWidget(ConfigOptions.Values.ON_OFF, ConfigOptions.ARTIFICIAL_ZOOM);
        this.addConfigOptionWidget(artificialZoomOption);

        snapAngleWidget = new ChoiceNumberWidget(font);
        this.addConfigOptionWidget(snapAngleWidget);

        rightClickMeuUsesOption = new ChoiceButtonWidget(ConfigOptions.Values.TP_COMMANDS, ConfigOptions.TELEPORT_METHOD, false);
        this.addConfigOptionWidget(rightClickMeuUsesOption);

        tileScaleSlider = new ChoiceSliderWidget(ConfigOptions.Values.TILE_SCALES, ConfigOptions.TILE_SCALE, true);
        this.addConfigOptionWidget(tileScaleSlider);

        reverseScrollOption = new ChoiceButtonWidget(ConfigOptions.Values.ON_OFF, ConfigOptions.REVERSE_SCROLL);
        this.addConfigOptionWidget(reverseScrollOption);

        zoomStrengthSlider = new ChoiceSliderWidget(ConfigOptions.Values.ZOOM_STRENGTHS, ConfigOptions.ZOOM_STRENGTH, true);
        this.addConfigOptionWidget(zoomStrengthSlider);

        teleportInterceptionOption = new ChoiceButtonWidget(ConfigOptions.Values.ON_OFF, ConfigOptions.TELEPORT_INTERCEPT);
        this.addConfigOptionWidget(teleportInterceptionOption);

        overlayLabel = new CategoryLabelWidget(Component.translatable("omm.raster.type.local-gen"), this.font);
        this.addConfigOptionWidget(overlayLabel);
        overlayLabelPosition = configList.getItemCount();

        renderClaimsOption = new ChoiceButtonWidget(ConfigOptions.Values.ON_OFF, ConfigOptions.CLAIMS_RENDERING);
        this.addConfigOptionWidget(renderClaimsOption);

        hiddenClaimsOption = new ChoiceButtonWidget(ConfigOptions.Values.ON_OFF, ConfigOptions.HIDDEN_CLAIMS);
        this.addConfigOptionWidget(hiddenClaimsOption);

        playerShowSlider = new ChoiceSliderWidget(ConfigOptions.Values.VISIBILITY, ConfigOptions.SHOW_PLAYERS);
        this.addConfigOptionWidget(playerShowSlider);

        directionIndicatorShowSlider = new ChoiceSliderWidget(ConfigOptions.Values.VISIBILITY, ConfigOptions.SHOW_DIRECTION_INDICATORS);
        this.addConfigOptionWidget(directionIndicatorShowSlider);

        playerSizeSlider = new ChoiceSliderWidget(ConfigOptions.Values.SIZES, ConfigOptions.PLAYER_SIZE);
        this.addConfigOptionWidget(playerSizeSlider);

        waypointSizeSlider = new ChoiceSliderWidget(ConfigOptions.Values.SIZES, ConfigOptions.WAYPOINT_SIZE);
        this.addConfigOptionWidget(waypointSizeSlider);

        hoverNamesOption = new ChoiceButtonWidget(ConfigOptions.Values.SHOW_HIDE, ConfigOptions.HOVER_NAMES);
        this.addConfigOptionWidget(hoverNamesOption);

        altitudeShadingOption = new ChoiceButtonWidget(ConfigOptions.Values.ON_OFF, ConfigOptions.ALTITUDE_SHADING);
        this.addConfigOptionWidget(altitudeShadingOption);

        rasterLabel = new CategoryLabelWidget(Component.translatable("omm.config.category.tile-source"), this.font);
        this.addConfigOptionWidget(rasterLabel);

        definedUrlWidget = new RasterConfigWidget(Component.translatable("omm.config.option.configure-rasters"));
        this.addConfigOptionWidget(definedUrlWidget);

        interfaceLabel = new CategoryLabelWidget(Component.nullToEmpty("Interface"), this.font);
        this.addConfigOptionWidget(interfaceLabel);

        transparencySlider = new ChoiceSliderWidget(ConfigOptions.Values.DECIMAL_PERCENT, ConfigOptions.INTERFACE_OPACITY, true);
        this.addConfigOptionWidget(transparencySlider);

        textColorSlider = new ColorChoiceSliderWidget(ConfigOptions.TEXT_COLOR);
        this.addConfigOptionWidget(textColorSlider);

        showConnectionStatusOption = new ChoiceButtonWidget(ConfigOptions.Values.SHOW_HIDE, ConfigOptions.SHOW_CONNECTION_STATUS);
        this.addConfigOptionWidget(showConnectionStatusOption);

        hudmapCompassOption = new ChoiceButtonWidget(ConfigOptions.Values.SHOW_HIDE, ConfigOptions.COMPASS);
        this.addConfigOptionWidget(hudmapCompassOption);

        hudmapBorderOption = new ChoiceButtonWidget(ConfigOptions.Values.SHOW_HIDE, ConfigOptions.HUDMAP_BORDER);
        this.addConfigOptionWidget(hudmapBorderOption);

        buttonStyleOption = new ChoiceButtonWidget(ConfigOptions.Values.BUTTON_STYLES, ConfigOptions.BUTTON_STYLE);
        this.addConfigOptionWidget(buttonStyleOption);

        webOptionsOption = new ChoiceMultiSelectWidget(WebIcon.ORDERED_LIST, ConfigOptions.WEB_OPTIONS);
        this.addConfigOptionWidget(webOptionsOption);

        distortionDisplayOption = new ChoiceButtonWidget(ConfigOptions.Values.ON_OFF, ConfigOptions.DISTORTION_DISPLAY);
        this.addConfigOptionWidget(distortionDisplayOption);

        if (OpenMineMapClient.SHOWDEVELOPEROPTIONS) {
            this.addConfigOptionWidget(new CategoryLabelWidget(Component.nullToEmpty("Developer"), this.font));
            this.addConfigOptionWidget(new ChoiceButtonWidget(ConfigOptions.Values.TRUE_FALSE, ConfigOptions.__DISABLE_WEB_REQUESTS, true));
            this.addConfigOptionWidget(new ChoiceButtonWidget(ConfigOptions.Values.TRUE_FALSE, ConfigOptions.__SHOW_MEMORY_CACHE_SIZE, true));
            this.addConfigOptionWidget(new ChoiceButtonWidget(ConfigOptions.Values.TRUE_FALSE, ConfigOptions.__ALT_INFO_TOOLTIP, true));
            this.addConfigOptionWidget(new ChoiceButtonWidget(ConfigOptions.Values.TRUE_FALSE, ConfigOptions.__LOG_HTTP_REQUESTS, true));
        }

        configList.restoreScroll();
        MapScreen.updateAltScreenMap(returnScreen, this);

    }

    public RasterConfigWidget getChoiceWidget() {
        return definedUrlWidget;
    }

    public void saveChanges() {
        for (AbstractWidget widget : choiceWidgets) {
            ((ConfigChoice) widget).writeParameterToFile();
        }
        if (!ConfigOptions.ARTIFICIAL_ZOOM.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) {
            MapScreen.clampZoom();
            HudMap.clampZoom();
        }
        TileManager.initializeConfigParameters();
        OmmMap.initializeConfigParameters(true);
        MapScreen.map.tryLoadClaims();
        HudMap.loadConfigParameters();
        ConfigFile.writeToFile();
        ButtonLayer.texturedButtons = ConfigOptions.BUTTON_STYLE.getAsBooleanFromValues(ConfigOptions.Values.BUTTON_STYLES);
        MapScreen.setPlainTextColor(textColorSlider.getTextColor(false), true);
        if (ConfigOptions.CLAIMS_RENDERING.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) {
            DrawableClaim.reloadClaimData(false, false, true);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        updateScreenDims();

        creditLayer.setPosition(windowScaledWidth - creditLayer.getWidth() - 3, windowScaledHeight - BOTTOM_SPACE + 7);
        exitButtonLayer.setPosition(windowScaledWidth / 2 - 22, windowScaledHeight - BOTTOM_BUTTON_OFFSET);
        checkButtonLayer.setPosition(windowScaledWidth / 2 + 2, windowScaledHeight - BOTTOM_BUTTON_OFFSET);
        configHud.setY(windowScaledHeight - BOTTOM_BUTTON_OFFSET);

        //context.enableScissor(0, 0, windowScaledWidth, windowScaledHeight - BOTTOM_SPACE);
        super.extractRenderState(context, mouseX, mouseY, delta);

        UContext.setContext(context);
        creditLayer.drawWidget(context, font);
        UContext.drawJustifiedText(Component.literal("OpenMineMap v" + OpenMineMapClient.MODVERSION), Justify.RIGHT, windowScaledWidth - 5, windowScaledHeight - 16, 0xFFFFFFFF, true);
    }
}
