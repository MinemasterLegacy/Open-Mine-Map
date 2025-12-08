package net.mmly.openminemap.waypoint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.Waypoint;
import net.mmly.openminemap.util.WaypointFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WaypointScreen extends Screen {

    private ColorSliderWidget hueSlider;
    private ColorSliderWidget saturationSlider;
    private ColorSliderWidget valueSlider;
    private static WaypointEntryWidget[] waypointEntries = new WaypointEntryWidget[0];
    private ButtonWidget createWaypointButton;

    private WaypointParameterWidget nameField;
    private WaypointParameterWidget longitudeWidget;
    private WaypointParameterWidget latitudeWidget;
    private WaypointParameterWidget angleWidget;

    public static WaypointScreen instance;
    private static int midPoint = 0;

    private final int SCROLLSPEED = 5;
    private static int entryListScroll = 0;
    private static int createScroll = 0;

    private static double initLong;
    private static double initLat;

    private static boolean initWithValues = false;

    public static WaypointScreen getInstance() {
        return instance;
    }

    public WaypointScreen() {
        super(Text.of("OpenMineMap Waypoints"));
        instance = this;
    }

    public WaypointScreen(double lat, double lon) {
        super(Text.of("OpenMineMap Waypoints"));
        instance = this;
        initWithValues = true;
        initLong = lon;
        initLat = lat;
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

        generateWaypointEntries();

        createWaypointButton = ButtonWidget.builder(Text.of("Create Waypoint"), (buttonWidget) -> {

            if (!nameField.valueIsValid()) {
                setFocused(nameField);
                return;
            }
            if (!longitudeWidget.valueIsValid()) {
                setFocused(longitudeWidget);
                return;
            }
            if (!latitudeWidget.valueIsValid()) {
                setFocused(latitudeWidget);
                return;
            }

            WaypointScreen.createWaypoint(
                    nameField.getText(),
                    Double.parseDouble(latitudeWidget.getText()),
                    Double.parseDouble(longitudeWidget.getText()),
                    getSelectedHSB(),
                    WaypointStyle.DIAMOND); //TODO make style dynamic
        }).build();
        this.addDrawableChild(createWaypointButton);

        nameField = new WaypointParameterWidget(this.textRenderer, Text.of("New Waypoint"), true, WaypointValueInputType.STRING);
        nameField.setTooltip(Tooltip.of(Text.of("Name")));
        nameField.setMaxLength(200);
        this.addDrawableChild(nameField);

        latitudeWidget = new WaypointParameterWidget(this.textRenderer,  Text.of(initWithValues ? Double.toString(initLat) : ""), true, WaypointValueInputType.LATITUDE);
        latitudeWidget.setTooltip(Tooltip.of(Text.of("Latitude")));
        this.addDrawableChild(latitudeWidget);

        longitudeWidget = new WaypointParameterWidget(this.textRenderer,  Text.of(initWithValues ? Double.toString(initLong) : ""), true, WaypointValueInputType.LONGITUDE);
        longitudeWidget.setTooltip(Tooltip.of(Text.of("Longitude")));
        this.addDrawableChild(longitudeWidget);

        if (initWithValues) {
            nameField.setCursorToStart(false);
            nameField.setSelectionEnd(nameField.getText().length());
            nameField.setSelectionStart(0);

        }

        updateWidgetPositions();

    }

    private int getSelectedHSB() {
        int hue = (int) (ColorSliderWidget.hue * 255);
        int sat = (int) (ColorSliderWidget.saturation * 255);
        int val = (int) (ColorSliderWidget.value * 255);

        System.out.println(hue +"\t"+ sat +"\t"+ val);

        return hue << 16 | sat << 8 | val;
    }

    private void generateWaypointEntries() {
        for (WaypointEntryWidget entry : waypointEntries) {
            getInstance().remove(entry);
        }

        int y = 20;
        int numEntries = OmmMap.getWaypoints().length;
        waypointEntries = new WaypointEntryWidget[numEntries];
        Waypoint[] waypoints = OmmMap.getWaypoints();

        for (int i = 0; i < numEntries; i++) {
            waypointEntries[i] = new WaypointEntryWidget(10, y, Text.of(""), waypoints[i], this.textRenderer);
            this.addDrawableChild(waypointEntries[i]);
            y += 25;
        }
    }

    public static void createWaypoint(String name, double lat, double lon, int color, WaypointStyle style) {
        WaypointFile.addWaypoint(style.toString().toLowerCase(), lat, lon, color, -1, name);
        WaypointFile.setWaypointsOfThisWorld(null);
        WaypointScreen.getInstance().generateWaypointEntries();
        //Waypoint waypoint = new Waypoint(style.toString().toLowerCase(), lat, lon, color, Double.NaN, name);
    }

    public static int getMidPoint() {
        return midPoint;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean b = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        if (!Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions.REVERSE_SCROLL))) verticalAmount *= 1;

        if (mouseX > midPoint) {
            int maxScroll = 200; //TODO determine based on total height of all widgets on the right
        } else {
            int maxScroll = Math.max((35 + (waypointEntries.length * 25) - MinecraftClient.getInstance().getWindow().getScaledHeight()), 0);
            System.out.println(maxScroll);
            entryListScroll = Math.clamp(
                    entryListScroll + (verticalAmount < 0 ? -SCROLLSPEED : SCROLLSPEED),
                    0,
                    maxScroll
            );
        }

        return b;
    }

    private void updateWidgetPositions() {
        midPoint = width / 2;

        for (WaypointEntryWidget entry : waypointEntries) {
            entry.setScroll(entryListScroll);
        }

        int creationAreaWidth = width - midPoint;

        int sliderWidths = Math.min((creationAreaWidth / 5), 30);
        float marginWidths = (float) (creationAreaWidth - (sliderWidths * 3)) / 4;

        float x = midPoint + marginWidths;

        hueSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20);
        x += marginWidths + sliderWidths;
        saturationSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20);
        x += marginWidths + sliderWidths;
        valueSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20);

        createWaypointButton.setDimensions(midPoint - 20 - 20 - 10 - 27, 20);
        createWaypointButton.setPosition(midPoint + 20 + 10 + 27, 160);

        nameField.setWidth(midPoint - 40);
        nameField.setPosition(midPoint + 20, 190);

        longitudeWidget.setWidth(midPoint - 40);
        longitudeWidget.setPosition(midPoint + 20, 220);

        latitudeWidget.setWidth(midPoint - 40);
        latitudeWidget.setPosition(midPoint + 20, 250);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        updateWidgetPositions();

        context.drawVerticalLine(midPoint, 0, height, 0xFF808080);

        //context.fill(140, 20, 160, 40, Color.HSBtoRGB(ColorSliderWidget.hue, ColorSliderWidget.saturation, ColorSliderWidget.value));

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
            context.drawTexture(wayIdent, midPoint + 20, 160, 0, 0, 27, 27, 27, 27);
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
        WaypointFile.save();
    }
}
