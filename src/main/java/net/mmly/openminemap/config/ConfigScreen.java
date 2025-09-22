package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ConfigFile;

import java.util.Objects;

public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Text.of("OMM Config"));
    }

    private static int windowHeight;
    private static int windowWidth;
    private static int windowScaledHeight;
    private static int windowScaledWidth;
    private static ButtonLayer exitButtonLayer;
    private static ButtonLayer checkButtonLayer;
    private static Identifier[][] buttonIdentifiers = new Identifier[3][2];
    public static TextFieldWidget customUrlWidget;
    public static TextFieldWidget snapAngleWidget;
    protected static boolean artificialZoomOption;

    protected static final int buttonSize = 20;
    protected final int[][] buttonPositionModifiers = new int[][] {
            {(8 + buttonSize), 22},
            {(8 + buttonSize), -2},
    };

    Window window = MinecraftClient.getInstance().getWindow();

    public static ButtonWidget toggleArtificialZoomButton;

    private void updateScreenDims() {
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
        artificialZoomOption = !artificialZoomOption;
        //System.out.println(toggleArtificialZoomButton.getX());
        toggleArtificialZoomButton.visible = false;
        toggleArtificialZoomButton = newToggleArtificialZoomButton();
        this.addDrawableChild(toggleArtificialZoomButton);
    }

    protected ButtonWidget newToggleArtificialZoomButton() {
        ButtonWidget b = ButtonWidget.builder(Text.of("Artificial Zoom: " + trueFalseToOnOff(artificialZoomOption)), (btn) -> {
            toggleArtificialZoom();
        }).dimensions(160, 20, 120, 20).build();
        b.setTooltip(Tooltip.of(Text.of(
                "Artificial Zoom allows for higher zoom levels than normal (+6 levels) by oversizing the smallest tile size."
        )));
        return b;
    }

    @Override
    protected void init() {
        exitButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1], buttonSize, buttonSize, 5);
        checkButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1], buttonSize, buttonSize, 7);
        this.addDrawableChild(exitButtonLayer);
        this.addDrawableChild(checkButtonLayer);

        updateTileSet();

        ButtonWidget configHud = ButtonWidget.builder(Text.of("Configure HUD..."), (btn) -> {
                ConfigScreen.saveChanges();
                MinecraftClient.getInstance().setScreen(
                        new MapConfigScreen()
                );
        }).dimensions(20, 20, 120, 20).build();
        configHud.setTooltip(Tooltip.of(Text.of("Change positioning and size of HUD elements.")));
        this.addDrawableChild(configHud);

        artificialZoomOption = Boolean.parseBoolean(ConfigFile.readParameter("ArtificialZoom"));
        toggleArtificialZoomButton = newToggleArtificialZoomButton();
        this.addDrawableChild(toggleArtificialZoomButton);

        customUrlWidget = new TextFieldWidget(this.textRenderer, 20, 50, 300, 20, Text.of("Map Tile Data URL"));
        customUrlWidget.setMaxLength(100);
        customUrlWidget.setText(ConfigFile.readParameter("TileMapUrl"));
        customUrlWidget.setTooltip(Tooltip.of(Text.of("Set the URL that OpenMineMap will attempt to load tiles from. \n{x}: Tile X position\n{y}: Tile Y position\n{z}: Zoom level")));
        this.addDrawableChild(customUrlWidget);

        snapAngleWidget = new TextFieldWidget(this.textRenderer, 20, 80, 100, 20, Text.of("Snap Angle"));
        snapAngleWidget.setMaxLength(20);
        snapAngleWidget.setText(ConfigFile.readParameter("SnapAngle"));
        snapAngleWidget.setTooltip(Tooltip.of(Text.of("Set an angle that can be snapped to using a keybind. Can be used to help make straight lines.")));
        this.addDrawableChild(snapAngleWidget);

    }

    public static void saveChanges() {
        if (!Objects.equals(ConfigFile.readParameter("TileMapUrl"), customUrlWidget.getText())) {
            //System.out.println("yea");
            TileManager.clearCacheDir();
            TileManager.setArtificialZoom();
        }
        String snapAngle;
        try {
            Double.parseDouble(snapAngleWidget.getText());
        } catch (NumberFormatException e) {
            snapAngle = "";
        }
        ConfigFile.writeParameter("TileMapUrl", customUrlWidget.getText());
        ConfigFile.writeParameter("SnapAngle", snapAngleWidget.getText());
        ConfigFile.writeParameter("ArtificialZoom", Boolean.toString(artificialZoomOption));
        ConfigFile.writeToFile();
        HudMap.setSnapAngle();
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
