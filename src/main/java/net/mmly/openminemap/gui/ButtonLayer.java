package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.config.ConfigScreen;
import net.mmly.openminemap.config.MapConfigScreen;
import net.mmly.openminemap.util.UnitConvert;

public class ButtonLayer extends ClickableWidget {

    private int function;

    public ButtonLayer(int x, int y, int width, int height, int f) {
        super(x, y, width, height, Text.empty());
        function = f;
    }


    //function - 0 is zoomin, 1 is zoomout, 2 is reset, 3 is follow, 4 is config, 5 is exit

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000); //0x00000000
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        switch (function) {
            case 0: //zoom in
                FullscreenMapScreen.zoomIn();
                break;
            case 1: //zoom out
                FullscreenMapScreen.zoomOut();
                break;
            case 2: //reset
                FullscreenMapScreen.resetMap();
                break;
            case 3: //follow
                FullscreenMapScreen.followPlayer();
                break;
            case 4: //config
                MinecraftClient.getInstance().setScreen(
                        new ConfigScreen()
                );
                break;
            case 5: //exit
                if (MinecraftClient.getInstance().currentScreen.getTitle().equals(Text.of("OMM Map Config"))) {
                    MapConfigScreen.revertChanges();
                }
                MinecraftClient.getInstance().currentScreen.close();
                break;
            case 6:
                //waypoints
                break;
            case 7:
                if (MinecraftClient.getInstance().currentScreen.getTitle().equals(Text.of("OMM Config"))) {
                    ConfigScreen.getInstance().saveChanges();
                } else if (MinecraftClient.getInstance().currentScreen.getTitle().equals(Text.of("OMM Map Config"))) {
                    MapConfigScreen.saveChanges();
                } else {
                    break;
                }
                MinecraftClient.getInstance().setScreen(null);
                break;
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    public boolean isHovered() {
        return this.isMouseOver(UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getX()), UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()));
    }
}
