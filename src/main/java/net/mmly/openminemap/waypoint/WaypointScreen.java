package net.mmly.openminemap.waypoint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.Waypoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

public class WaypointScreen extends Screen {

    private ColorSliderWidget hueSlider;
    private ColorSliderWidget saturationSlider;
    private ColorSliderWidget valueSlider;
    private WaypointEntryWidget[] waypointEntries;

    private BufferedImage diamondWaypoint;
    private static int midPoint = 0;

    public WaypointScreen() {
        super(Text.of("OpenMineMap Waypoints"));
    }

    @Override
    protected void init() {
        super.init();

        hueSlider = new ColorSliderWidget(20, 20, 20, 120, ColorSliderType.HUE);
        saturationSlider = new ColorSliderWidget(60, 20, 20, 120, ColorSliderType.SATURATION);
        valueSlider = new ColorSliderWidget(100, 20, 20, 120, ColorSliderType.VALUE);

        this.addDrawableChild(hueSlider);
        this.addDrawableChild(saturationSlider);
        this.addDrawableChild(valueSlider);

        int y = 20;
        int numEntries = OmmMap.getWaypoints().length;
        waypointEntries = new WaypointEntryWidget[numEntries];
        Waypoint[] waypoints = OmmMap.getWaypoints();

        for (int i = 0; i < numEntries; i++) {
            waypointEntries[i] = new WaypointEntryWidget(10, y, Text.of(""), waypoints[i], this.textRenderer);
            this.addDrawableChild(waypointEntries[i]);
            y += 25;
        }

        try {
            diamondWaypoint = ImageIO.read(client.getResourceManager().getResource(Identifier.of("openminemap", "waypoints/diamond.png")).get().getInputStream());
        } catch (IOException e) {
            System.out.println("load error");
        }

        updateWidgetPositions();

    }

    public static int getMidPoint() {
        return midPoint;
    }

    private void updateWidgetPositions() {
        midPoint = width / 2;

        int creationAreaWidth = width - midPoint;

        int sliderWidths = Math.min((creationAreaWidth / 5), 30);
        float marginWidths = (float) (creationAreaWidth - (sliderWidths * 3)) / 4;

        float x = midPoint + marginWidths;

        hueSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20);
        x += marginWidths + sliderWidths;
        saturationSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20);
        x += marginWidths + sliderWidths;
        valueSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        updateWidgetPositions();

        context.drawVerticalLine(midPoint, 0, height, 0xFF808080);

        context.fill(140, 20, 160, 40, Color.HSBtoRGB(ColorSliderWidget.hue, ColorSliderWidget.saturation, ColorSliderWidget.value));

        //BufferedImage image = new BufferedImage(diamondWaypoint.getColorModel(), diamondWaypoint.getRaster(), diamondWaypoint.getColorModel().isAlphaPremultiplied(), null);
        //image = colorize(image, ColorSliderWidget.hue);

        try {
            BufferedImage image = ImageIO.read(client.getResourceManager().getResource(Identifier.of("openminemap", "waypoints/diamond.png")).get().getInputStream());
            image = colorize(image, ColorSliderWidget.hue, ColorSliderWidget.saturation, ColorSliderWidget.value);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            NativeImage nImage = NativeImage.read(is);

            Identifier wayIdent = client.getTextureManager().registerDynamicTexture("osmwaypoint", new NativeImageBackedTexture(nImage));
            context.drawTexture(wayIdent, 180, 20, 0, 0, 27, 27, 27, 27);
            client.getTextureManager().destroyTexture(wayIdent);

            is.close();
            nImage.close();
            os.close();

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("colorize fail");
        }

    }

    public static BufferedImage colorize(BufferedImage image, float hue, float sat, float bright) throws IllegalArgumentException {

        if (hue < 0 || hue > 1 || Float.isNaN(hue)) {
            throw new IllegalArgumentException(
                    "Hue must be between 0 and 1 inclusive.");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);

                int alpha = (argb & 0xff000000);
                int grayLevel = (argb >> 8) & 0xff;

                float brightness = (grayLevel / 255f);

                brightness *= bright;

                int rgb = Color.HSBtoRGB(hue, sat, brightness);

                argb = (rgb & 0x00ffffff) | alpha;
                image.setRGB(x, y, argb);
            }
        }

        return image;
    }

    @Override
    public void close() {
        super.close();
        MinecraftClient.getInstance().setScreen(
                new FullscreenMapScreen()
        );
    }
}
