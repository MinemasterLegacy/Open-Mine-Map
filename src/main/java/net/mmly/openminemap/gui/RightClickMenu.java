package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;

import java.awt.*;

public class RightClickMenu extends ClickableWidget {

    public static final int width = 95;
    public static final int height = 32; // = 16 * number of menu options
    public static int hoverOn = 0;

    public RightClickMenu(int x, int y) {
        super(x, y, width, height, Text.empty());
    }

    float savedMouseLat;
    float savedMouseLong;

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x00000000);
        if (this.isMouseOver(UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getX()), UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()))) {
            hoverOn = (int) Math.ceil((UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()) - this.getY() + UnitConvert.pixelToScaledCoords(1))/16);
        } else {
            hoverOn = 0;
        }

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        switch (hoverOn) {
            case 1: {
                //MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll " + savedMouseLat + " " + savedMouseLong);
                try { //can be used during development to use the /tp command instead of /tpll
                    double[] xy = Projection.from_geo(savedMouseLat, savedMouseLong);
                    MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+(int) xy[0]+" ~ "+ (int) xy[1]);
                } catch (CoordinateValueError error) {
                    System.out.println("Error with teleport here");
                }
                break;
            }
            case 2: {
                try {
                    //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test"), null);
                    MinecraftClient.getInstance().keyboard.setClipboard(savedMouseLat + " " + savedMouseLong);
                } catch (HeadlessException e) {
                    System.out.println("Unable to write to clipboard; System does not support it.");
                }
                break;
            }
        }
        FullscreenMapScreen.disableRightClickMenu();
    }

    protected void setSavedMouseLatLong(double x, double y) {
        savedMouseLat = (float) y;
        savedMouseLong = (float) x;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}
