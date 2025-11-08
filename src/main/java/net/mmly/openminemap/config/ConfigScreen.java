package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.TextFieldLayer;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;

import java.util.Objects;

public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Text.of("OMM Config"));
    }

    static ConfigScreen configScreen;
    private static int windowHeight;
    private static int windowWidth;
    private static int windowScaledHeight;
    private static int windowScaledWidth;
    private static ButtonLayer exitButtonLayer;
    private static ButtonLayer checkButtonLayer;
    private static Identifier[][] buttonIdentifiers = new Identifier[3][2];
    public static TextFieldWidget customUrlWidget;
    public static TextFieldWidget snapAngleWidget;
    protected static boolean doArtificialZoom;
    ChoiceButtonWidget rightClickMeuUsesOption;
    ChoiceButtonWidget artificialZoomOption;
    ChoiceButtonWidget reverseScrollOption;
    ChoiceSliderWidget playerShowSlider;
    ChoiceSliderWidget directionIndicatorShowSlider;
    TextWidget overlayLabel;
    TextWidget generalLabel;
    TextWidget versionLabel;
    ButtonWidget configHud;
    int nextOptionSlot;
    int totalOptions;
    private int scrollRange;
    private int currentScroll = 0;
    private int maxScroll;
    private final int SCROLLSPEED = 5;

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

    static protected String trueFalseToOnOff(boolean t) {
        if (t) {
            return "On";
        } else {
            return "Off";
        }
    }

    static protected boolean onOffToTrueFalse(String t) {
        return t.equals("On");
    }

    protected void toggleArtificialZoom() {
        doArtificialZoom = !doArtificialZoom;
        //System.out.println(toggleArtificialZoomButton.getX());
        toggleArtificialZoomButton.visible = false;
        toggleArtificialZoomButton = newToggleArtificialZoomButton();
        this.addDrawableChild(toggleArtificialZoomButton);
    }

    protected ButtonWidget newToggleArtificialZoomButton() {
        ButtonWidget b = ButtonWidget.builder(Text.of("Artificial Zoom: " + trueFalseToOnOff(doArtificialZoom)), (btn) -> {
            toggleArtificialZoom();
        })
                .dimensions(20, getNextOptionSlot(), 120, 20).build();
        b.setTooltip(Tooltip.of(Text.of(
                "Artificial Zoom allows for higher zoom levels than normal (+6 levels) by oversizing the smallest tile size."
        )));
        return b;
    }

    private void updateScrollPositions(int change) {
        currentScroll -= change;
        generalLabel.setY(generalLabel.getY() + change);
        configHud.setY(configHud.getY() + change);
        artificialZoomOption.getButtonWidget().setY(artificialZoomOption.getButtonWidget().getY() + change);
        customUrlWidget.setY(customUrlWidget.getY() + change);
        snapAngleWidget.setY(snapAngleWidget.getY() + change);
        rightClickMeuUsesOption.getButtonWidget().setY(rightClickMeuUsesOption.getButtonWidget().getY() + change);
        reverseScrollOption.getButtonWidget().setY(reverseScrollOption.getButtonWidget().getY() + change);
        overlayLabel.setY(overlayLabel.getY() + change);
        playerShowSlider.setY(playerShowSlider.getY() + change);
        directionIndicatorShowSlider.setY(directionIndicatorShowSlider.getY() + change);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean b = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        maxScroll = Math.max(scrollRange - windowScaledHeight, 0);
        //System.out.println(verticalAmount);
        if (ConfigFile.readParameter(ConfigOptions.REVERSE_SCROLL).equals("on")) verticalAmount *= -1;
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
        System.out.println("init");
        totalOptions = 0;
        nextOptionSlot = -5;
        configScreen = this;

        updateTileSet();
        updateScreenDims();

        exitButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1], buttonSize, buttonSize, ButtonFunction.EXIT);
        checkButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1], buttonSize, buttonSize, ButtonFunction.CHECKMARK);
        exitButtonLayer.setTooltip(Tooltip.of(Text.of("Exit without Saving")));
        checkButtonLayer.setTooltip(Tooltip.of(Text.of("Save and Exit")));
        this.addDrawableChild(exitButtonLayer);
        this.addDrawableChild(checkButtonLayer);

        versionLabel = new TextWidget(0, windowScaledHeight - 20, windowScaledWidth - 5, 20, Text.of("OpenMineMap v1.2.1"), this.textRenderer);
        versionLabel.alignRight();
        this.addDrawableChild(versionLabel);

        configHud = ButtonWidget.builder(Text.of("Configure HUD..."), (btn) -> {
                this.saveChanges();
                MinecraftClient.getInstance().setScreen(new MapConfigScreen());
        }).dimensions(20, getNextOptionSlot(), 120, 20).build();
        configHud.setTooltip(Tooltip.of(Text.of("Change positioning and size of HUD elements.")));
        this.addDrawableChild(configHud);

        generalLabel = new TextWidget(20, getNextOptionSlot() + 5, 120, 20, Text.of("General"), this.textRenderer);
        this.addDrawableChild(generalLabel);

        doArtificialZoom = ConfigFile.readParameter(ConfigOptions.ARTIFICIAL_ZOOM).equals("on");
        artificialZoomOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.of("Artificial Zoom"), Text.of(""), new String[] {"Off", "On"}, ConfigOptions.ARTIFICIAL_ZOOM);
        artificialZoomOption.getButtonWidget().setTooltip(Tooltip.of(Text.of("Adds further zoom levels beyond what OpenStreetMap provides")));
        this.addDrawableChild(artificialZoomOption.getButtonWidget());

        customUrlWidget = new TextFieldWidget(this.textRenderer, 20, getNextOptionSlot(), 300, 20, Text.of("Map Tile Data URL"));
        customUrlWidget.setMaxLength(1000);
        customUrlWidget.setText(ConfigFile.readParameter(ConfigOptions.TILE_MAP_URL));
        customUrlWidget.setTooltip(Tooltip.of(Text.of("Set the URL that OpenMineMap will attempt to load tiles from. \n{x}: Tile X position\n{y}: Tile Y position\n{z}: Zoom level")));
        this.addDrawableChild(customUrlWidget);

        snapAngleWidget = new TextFieldLayer(this.textRenderer, 20, getNextOptionSlot(), 120, 20, Text.of("Snap Angle"), 0);
        snapAngleWidget.setMaxLength(50);
        snapAngleWidget.setText(ConfigFile.readParameter(ConfigOptions.SNAP_ANGLE));
        snapAngleWidget.setTooltip(Tooltip.of(Text.of("Set an angle that can be snapped to using a keybind. Can be used to help make straight lines. (Use a Minecraft angle)")));
        this.addDrawableChild(snapAngleWidget);

        rightClickMeuUsesOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.of("Right Click Menu Uses"), Text.of("The command that will be used to teleport when using the Fullscreen Right Click Menu."), new String[] {"/tpll", "/tp"}, ConfigOptions.RIGHT_CLICK_MENU_USES);
        this.addDrawableChild(rightClickMeuUsesOption.getButtonWidget());

        reverseScrollOption = new ChoiceButtonWidget(20, getNextOptionSlot(), Text.of("Reverse Scroll"), Text.of("Reverse the scroll wheel."), new String[] {"Off", "On"}, ConfigOptions.REVERSE_SCROLL);
        this.addDrawableChild(reverseScrollOption.getButtonWidget());

        overlayLabel = new TextWidget(20, getNextOptionSlot() + 5, 120, 20, Text.of("Overlays"), this.textRenderer);
        overlayLabel.setTooltip(Tooltip.of(Text.of("")));
        this.addDrawableChild(overlayLabel);

        playerShowSlider = new ChoiceSliderWidget(20, getNextOptionSlot(), Text.of("Players"), new String[] {"None", "Self", "Local"}, ConfigOptions.SHOW_PLAYERS);
        playerShowSlider.setTooltip(Tooltip.of(Text.of("Show Players on all maps")));
        this.addDrawableChild(playerShowSlider);

        directionIndicatorShowSlider = new ChoiceSliderWidget(20, getNextOptionSlot(), Text.of("Directions"), new String[] {"None", "Self", "Local"}, ConfigOptions.SHOW_DIRECTION_INDICATORS);
        directionIndicatorShowSlider.setTooltip(Tooltip.of(Text.of("Show Direction Indicators on all maps")));
        this.addDrawableChild(directionIndicatorShowSlider);

        scrollRange = totalOptions * 25 + 35;
    }

    public void saveChanges() {
        if (!Objects.equals(ConfigFile.readParameter(ConfigOptions.TILE_MAP_URL), customUrlWidget.getText())) {
            //System.out.println("yea");
            TileManager.clearCacheDir();
        }
        String snapAngle;
        try {
            snapAngle = Double.toString(Double.parseDouble(snapAngleWidget.getText())); //will ensure that the snap angle is a number
        } catch (NumberFormatException e) {
            snapAngle = "";
        }
        ConfigFile.writeParameter(ConfigOptions.TILE_MAP_URL, customUrlWidget.getText());
        ConfigFile.writeParameter(ConfigOptions.SNAP_ANGLE, snapAngle);
        //ConfigFile.writeParameter("ArtificialZoom", Boolean.toString(doArtificialZoom));
        rightClickMeuUsesOption.writeParameterToFile();
        artificialZoomOption.writeParameterToFile();
        reverseScrollOption.writeParameterToFile();
        playerShowSlider.writeParameterToFile();
        directionIndicatorShowSlider.writeParameterToFile();
        TileManager.initializeConfigParameters();
        HudMap.setSnapAngle();
        ConfigFile.writeToFile();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        updateScreenDims();

        exitButtonLayer.setPosition(windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1]);
        checkButtonLayer.setPosition(windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1]);

        context.drawTexture(checkButtonLayer.isHovered() ? buttonIdentifiers[2][0] : buttonIdentifiers[1][0], windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(exitButtonLayer.isHovered() ? buttonIdentifiers[2][1] : buttonIdentifiers[1][1], windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);

    }
}
