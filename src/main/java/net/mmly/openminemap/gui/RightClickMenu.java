package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.WebIcon;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

public class RightClickMenu extends ClickableWidget {

    private static final int width = 95;
    // = 16 * number of menu options
    private static final int height = 48;
    private static int hoverOn = 0;
    private static boolean useTp;
    boolean enabled;
    public static boolean selectingSite = false;
    double clickX = 0;
    double clickY = 0;
    private final Identifier rightClickCursor = Identifier.of("openminemap", "selectcursor.png");
    public int horizontalSide = 1;
    public int verticalSize = 1;
    //private WebAppSelectLayer webSelect = null;

    public RightClickMenu(int x, int y) {
        super(x, y, width, height, Text.empty());
        useTp = Objects.equals(ConfigFile.readParameter(ConfigOptions.RIGHT_CLICK_MENU_USES), "/tp");
        enabled = false;

    }

    static float savedMouseLat;
    static float savedMouseLong;

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x00000000);
        if (this.isMouseOver(UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getX()), UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()))) {
            hoverOn = (int) Math.ceil((UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()) - this.getY() + UnitConvert.pixelToScaledCoords(1))/16);
        } else {
            hoverOn = 0;
        }

    }

    public void drawWidget(DrawContext context, TextRenderer renderer) {
        if (!enabled) return;
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x88000000);


        context.drawText(renderer, "Teleport Here", getX() + 4, getY() + 4, RightClickMenu.hoverOn == 1 ? 0xFFa8afff : 0xFFFFFFFF, false);
        context.drawText(renderer, "Copy Coordinates", getX() + 4, getY() + 20, RightClickMenu.hoverOn == 2 ? 0xFFa8afff : 0xFFFFFFFF, false);
        context.drawText(renderer, "Open In...", getX() + 4, getY() + 36, RightClickMenu.hoverOn == 3 || selectingSite ? 0xFFa8afff : 0xFFFFFFFF, false);

        /*


         */
        context.drawTexture(rightClickCursor, (int) clickX - 4, (int) clickY - 4, 0, 0, 9, 9, 9, 9);


    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        switch (hoverOn) {
            case 1: {
                //MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll " + savedMouseLat + " " + savedMouseLong);
                try { //can be used during development to use the /tp command instead of /tpll
                    if (MinecraftClient.getInstance().player != null) {
                        double[] mcXz = Projection.from_geo(savedMouseLat, savedMouseLong);
                        if (useTp) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+(int) mcXz[0]+" "+PlayersManager.getHighestPoint(mcXz[0], mcXz[1])+" "+ (int) mcXz[1]);
                        } else {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll "+savedMouseLat+" "+savedMouseLong+" "+PlayersManager.getHighestPoint(mcXz[0], mcXz[1]));
                        }
                    }
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
            case 3: {
                selectingSite = !selectingSite;
                int modX = 0;
                int modY = 0;
                if (horizontalSide == -1) modX -= width + 8 + 14;
                if (verticalSize == -1) modY -= (98 - height);

                FullscreenMapScreen.webAppSelectLayer.setPosition(getX() + width + 4 + modX, getY() + modY);
                return;
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