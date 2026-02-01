package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.gui.TextFieldLayer;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;

public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Text.of("OMM Config"));
    }

    static ConfigScreen configScreen;
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

    TextWidget generalLabel;
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
    public static TextFieldWidget customUrlWidget;
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
    protected final int[][] buttonPositionModifiers = new int[][] {
            {(8 + buttonSize), 22},
            {(8 + buttonSize), -2},
    };

    Window window;
    public static ButtonWidget toggleArtificialZoomButton;

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

    @Override
    protected void init() {
        totalOptions = 0;
        nextOptionSlot = -5;
        configScreen = this;

        updateTileSet();
        updateScreenDims();

        wikiLinkLayer = new WikiLinkLayer(0, 0);
        this.addDrawableChild(wikiLinkLayer);

        exitButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1], buttonSize, buttonSize, ButtonFunction.EXIT);
        checkButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1], buttonSize, buttonSize, ButtonFunction.CHECKMARK);
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
        }).dimensions(20, getNextOptionSlot(), 120, 20).build();
        configHud.setTooltip(Tooltip.of(Text.translatable("omm.config.tooltip.configure-hud")));
        this.addDrawableChild(configHud);

        generalLabel = new TextWidget(20, getNextOptionSlot() + 5, 120, 20, Text.translatable("omm.config.category.general"), this.textRenderer);
        this.addDrawableChild(generalLabel);

        artificialZoomOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.artificial-zoom"), Text.of(""), new String[] {"Off", "On"}, ConfigOptions.ARTIFICIAL_ZOOM);
        artificialZoomOption.getButtonWidget().setTooltip(Tooltip.of(Text.translatable("omm.config.tooltip.artificial-zoom")));
        this.addDrawableChild(artificialZoomOption.getButtonWidget());

        snapAngleWidget = new TextFieldLayer(this.textRenderer, 20, getNextOptionSlot(), 120, 20, Text.translatable("omm.config.option.snap-angle"), 0);
        snapAngleWidget.setMaxLength(50);
        snapAngleWidget.setText(ConfigFile.readParameter(ConfigOptions.SNAP_ANGLE));
        snapAngleWidget.setTooltip(Tooltip.of(Text.translatable("omm.config.tooltip.snap-angle")));
        this.addDrawableChild(snapAngleWidget);

        rightClickMeuUsesOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.rcm-uses"), Text.translatable("omm.config.tooltip.rcm-uses"), new String[] {"/tpll", "/tp"}, ConfigOptions.RIGHT_CLICK_MENU_USES);
        this.addDrawableChild(rightClickMeuUsesOption.getButtonWidget());

        reverseScrollOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.reverse-scroll"), Text.translatable("omm.config.tooltip.reverse-scroll"), new String[] {"Off", "On"}, ConfigOptions.REVERSE_SCROLL);
        this.addDrawableChild(reverseScrollOption.getButtonWidget());

        zoomStrengthWidget = new ChoiceSliderWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.zoom-strength"), Text.translatable("omm.config.tooltip.zoom-strength"), zoomStrengthLevels, ConfigOptions.ZOOM_STRENGTH);
        this.addDrawableChild(zoomStrengthWidget);

        hoverNamesOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.hover-names"), Text.translatable("omm.config.tooltip.hover-names"), new String[] {"Off", "On"}, ConfigOptions.HOVER_NAMES);
        this.addDrawableChild(hoverNamesOption.getButtonWidget());

        overlayLabel = new TextWidget(20, getNextOptionSlot() + 5, 120, 20, Text.translatable("omm.config.category.overlays"), this.textRenderer);
        this.addDrawableChild(overlayLabel);

        playerShowSlider = new ChoiceSliderWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.players"), Text.translatable("omm.config.tooltip.players"), new String[] {"None", "Self", "Local"}, ConfigOptions.SHOW_PLAYERS);
        this.addDrawableChild(playerShowSlider);

        directionIndicatorShowSlider = new ChoiceSliderWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.directions"), Text.translatable("omm.config.tooltip.directions"), new String[] {"None", "Self", "Local"}, ConfigOptions.SHOW_DIRECTION_INDICATORS);
        this.addDrawableChild(directionIndicatorShowSlider);

        altitudeShadingOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.translatable("omm.config.option.altitude-shading"),  Text.translatable("omm.config.tooltip.altitude-shading"), new String[] {"On", "Off"}, ConfigOptions.ALTITUDE_SHADING);
        this.addDrawableChild(altitudeShadingOption.getButtonWidget());

        urlLabel = new TextWidget(20, getNextOptionSlot() + 5, 120, 20, Text.translatable("omm.config.category.tile-source"), this.textRenderer);
        this.addDrawableChild(urlLabel);

        /*
        customUrlWidget = new TextFieldLayer(this.textRenderer, 20, getNextOptionSlot(), 300, 20, Text.of("Map Tile Data URL"), 1);
        customUrlWidget.setMaxLength(1000);
        customUrlWidget.setText(ConfigFile.readParameter(ConfigOptions.TILE_MAP_URL));
        customUrlWidget.setTooltip(Tooltip.of(Text.of("Set the URL that OpenMineMap will attempt to load tiles from. \n{x}: Tile X position\n{y}: Tile Y position\n{z}: Zoom level")));
        this.addDrawableChild(customUrlWidget);

         */

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
        super.render(context, mouseX, mouseY, delta);
        updateScreenDims();

        wikiLinkLayer.setPosition(windowScaledWidth - wikiLinkLayer.getWidth(), windowScaledHeight - 32);
        exitButtonLayer.setPosition(windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1]);
        checkButtonLayer.setPosition(windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1]);

        wikiLinkLayer.drawWidget(context, textRenderer);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, checkButtonLayer.isHovered() ? buttonIdentifiers[2][0] : buttonIdentifiers[1][0], windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, exitButtonLayer.isHovered() ? buttonIdentifiers[2][1] : buttonIdentifiers[1][1], windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);

    }
}
