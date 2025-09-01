package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.FullscreenMapScreen;

public class ConfigScreen extends Screen {
    public ConfigScreen() {
        super(Text.empty());
    }

    private static int windowHeight;
    private static int windowWidth;
    private static int windowScaledHeight;
    private static int windowScaledWidth;
    private static ButtonLayer exitButtonLayer;
    private static ButtonLayer checkButtonLayer;
    private static Identifier[][] buttonIdentifiers = new Identifier[3][2];

    protected static final int buttonSize = 20;
    protected final int[][] buttonPositionModifiers = new int[][] {
            {(8 + buttonSize), 22},
            {(8 + buttonSize), -2},
    };

    Window window = MinecraftClient.getInstance().getWindow();

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
        switch (FullscreenMapScreen.buttonTheme) {
            case 1: path = "buttons/sodify/";
            default: path = "buttons/vanilla/"; // 0
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                buttonIdentifiers[i][j] = Identifier.of("openminemap", path + states[i] + names[j]);
            }
        }
    }

    @Override
    protected void init() {
        exitButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[1][0], (windowScaledHeight / 2) + buttonPositionModifiers[1][1], buttonSize, buttonSize, 5);
        checkButtonLayer = new ButtonLayer(windowScaledWidth - buttonPositionModifiers[0][0], (windowScaledHeight / 2) + buttonPositionModifiers[0][1], buttonSize, buttonSize, 6);
        this.addDrawableChild(exitButtonLayer);
        this.addDrawableChild(checkButtonLayer);

        updateTileSet();

        ButtonWidget configHud = ButtonWidget.builder(Text.of("Configure HUD..."), (btn) -> {
                    MinecraftClient.getInstance().setScreen(
                            new MapConfigScreen()
                    );
        }).dimensions(20, 20, 120, 20).build();
        this.addDrawableChild(configHud);

        TextFieldWidget textFieldWidget = new TextFieldWidget(this.textRenderer, 20, 50, 300, 20, Text.of("Map Tile Data URL"));
        textFieldWidget.setText("Map Tile Data URL");
        this.addDrawableChild(textFieldWidget);

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
