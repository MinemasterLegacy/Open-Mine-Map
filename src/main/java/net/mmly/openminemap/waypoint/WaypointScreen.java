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
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;
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
    private ButtonWidget saveWaypointButton;
    private ButtonWidget deleteWaypointButton;

    private WaypointParameterWidget nameField;
    private WaypointParameterWidget longitudeWidget;
    private WaypointParameterWidget latitudeWidget;
    private WaypointParameterWidget angleWidget;

    private WaypointIconSelectButton leftButton;
    private WaypointIconSelectButton rightButton;

    public static WaypointScreen instance;
    private static int midPoint = 0;

    private static final int SCROLLSPEED = 10;
    private static int entryListScroll = 0;
    private static int createScroll = 0;

    private static double initLong;
    private static double initLat;
    private static boolean initWithValues = false;
    private static boolean initInEditMode = false;
    private static Waypoint initEditWaypoint;

    public boolean inEditMode = false;
    Waypoint editingWaypoint = null;
    public String editingWaypointName = "";

    public WaypointStyle styleSelection = WaypointStyle.DIAMOND;

    private static final Identifier[] styleIdentifiers = new Identifier[] {
            Identifier.of("openminemap", "waypoints/diamond.png"),
            Identifier.of("openminemap", "waypoints/star.png"),
            Identifier.of("openminemap", "waypoints/house.png"),
            Identifier.of("openminemap", "waypoints/city.png"),
            Identifier.of("openminemap", "waypoints/cross.png")
    };

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

    //called by the right click menu to immediately enter edit mode
    public WaypointScreen(Waypoint waypoint) {
        super(Text.of("OpenMineMap Waypoints"));
        instance = this;
        initEditWaypoint = waypoint;
        initInEditMode = true;
    }

    public void enableEditMode(Waypoint waypoint) {
        editingWaypoint = waypoint;
        editingWaypointName = waypoint.name;
        inEditMode = true;
        ColorSliderWidget.setColor(waypoint.color);
        styleSelection = WaypointStyle.getByString(waypoint.style);
        nameField.setText(waypoint.name);
        latitudeWidget.setText(Double.toString(waypoint.latitude));
        longitudeWidget.setText(Double.toString(waypoint.longitude));
        if (waypoint.angle < 0) {
            angleWidget.setText("");
        } else {
            angleWidget.setText(Double.toString(waypoint.angle));
        }
        createWaypointButton.visible = false;
        saveWaypointButton.visible = true;
    }

    public void exitEditMode() {
        editingWaypoint = null;
        editingWaypointName = "";
        inEditMode = false;
        ColorSliderWidget.setColor(ColorSliderWidget.defaultHue, ColorSliderWidget.defaultSaturation, ColorSliderWidget.defaultValue);
        styleSelection = WaypointStyle.DIAMOND;
        nameField.setText("");
        latitudeWidget.setText("");
        longitudeWidget.setText("");
        angleWidget.setText("");
        saveWaypointButton.visible = false;
        createWaypointButton.visible = true;
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

        createWaypointButton = ButtonWidget.builder(Text.translatable("omm.waypoints.button.create"), (buttonWidget) -> {
            if (!fieldsAreValid()) return;
            WaypointScreen.createWaypoint(
                    nameField.getText(),
                    Double.parseDouble(latitudeWidget.getText()),
                    Double.parseDouble(longitudeWidget.getText()),
                    getSelectedHSB(),
                    styleSelection,
                    angleWidget.getText().isBlank() ? -1 : Double.parseDouble(angleWidget.getText())
                    );
        }).build();
        this.addDrawableChild(createWaypointButton);

        saveWaypointButton = ButtonWidget.builder(Text.translatable("omm.waypoints.button.save"), (buttonWidget) -> {
            if (!fieldsAreValid()) return;
            WaypointScreen.saveEditingWaypoint();
            exitEditMode();
        }).build();
        this.addDrawableChild(saveWaypointButton);

        deleteWaypointButton = ButtonWidget.builder(Text.translatable("omm.waypoints.button.delete"), (buttonWidget) -> {
            if (!WaypointScreen.instance.inEditMode) return;
            WaypointScreen.deleteEditingWaypoint();
            exitEditMode();
        }).build();
        this.addDrawableChild(deleteWaypointButton);

        nameField = new WaypointParameterWidget(this.textRenderer, Text.of(initWithValues ? UnitConvert.floorToPlace(initLat, 7) + ", " + UnitConvert.floorToPlace(initLong, 7) : ""), true, WaypointValueInputType.NAME);
        nameField.setMaxLength(200);
        this.addDrawableChild(nameField);

        latitudeWidget = new WaypointParameterWidget(this.textRenderer,  Text.of(initWithValues ? Double.toString(initLat) : ""), true, WaypointValueInputType.LATITUDE);
        this.addDrawableChild(latitudeWidget);

        longitudeWidget = new WaypointParameterWidget(this.textRenderer,  Text.of(initWithValues ? Double.toString(initLong) : ""), true, WaypointValueInputType.LONGITUDE);
        this.addDrawableChild(longitudeWidget);

        angleWidget = new WaypointParameterWidget(this.textRenderer, Text.of(""), false, WaypointValueInputType.SNAP_ANGLE);
        this.addDrawableChild(angleWidget);

        leftButton = new WaypointIconSelectButton(-1);
        this.addDrawableChild(leftButton);

        rightButton = new WaypointIconSelectButton(1);
        this.addDrawableChild(rightButton);

        if (initWithValues) {
            nameField.setCursorToStart(false);
            nameField.setSelectionEnd(nameField.getText().length());
            nameField.setSelectionStart(0);
        }

        editingWaypoint = null;
        editingWaypointName = "";
        inEditMode = false;
        saveWaypointButton.visible = false;

        if (initInEditMode) {
            enableEditMode(initEditWaypoint);
            latitudeWidget.setCursorToStart(false);
            longitudeWidget.setCursorToStart(false);
            angleWidget.setCursorToStart(false);
            nameField.setCursorToStart(false);
        }

        initWithValues = false;
        initInEditMode = false;

        updateWidgetPositions();
    }

    public static void deleteEditingWaypoint() {
        if (WaypointFile.deleteWaypoint(getInstance().editingWaypointName)) {
            WaypointFile.setWaypointsOfThisWorld(false);
            instance.generateWaypointEntries(Math.max(entryListScroll - 25, 0));
        } else {
            OpenMineMapClient.debugMessages.add(Text.translatable("OpenMineMap: Waypoint delete failed").getString());
        }
    }

    public static void saveEditingWaypoint() {
        if (WaypointFile.overwriteWaypoint(
                instance.editingWaypointName,
                instance.nameField.getText(),
                Double.parseDouble(instance.latitudeWidget.getText()),
                Double.parseDouble(instance.longitudeWidget.getText()),
                instance.getSelectedHSB(),
                instance.angleWidget.getText().isBlank() ? -1 : positiseAngle(Double.parseDouble(instance.angleWidget.getText())),
                instance.styleSelection.toString().toLowerCase()
        )) {
            instance.generateWaypointEntries(entryListScroll);
        } else {
            OpenMineMapClient.debugMessages.add(Text.translatable("omm.error.waypoint-property-failiure").getString());
        }
    }

    public boolean fieldsAreValid() {
        return nameField.valueIsValid() && longitudeWidget.valueIsValid() && latitudeWidget.valueIsValid() && angleWidget.valueIsValid();
    }

    public int getSelectedHSB() {
        int hue = (int) (ColorSliderWidget.hue * 255);
        int sat = (int) (ColorSliderWidget.saturation * 255);
        int val = (int) (ColorSliderWidget.value * 255);

        System.out.println(hue +"\t"+ sat +"\t"+ val);

        return hue << 16 | sat << 8 | val;
    }

    private void generateWaypointEntries(int scroll) {
        generateWaypointEntries();
        entryListScroll = scroll;
        updateWidgetPositions();
    }

    private void generateWaypointEntries() {
        entryListScroll = 0;

        for (WaypointEntryWidget entry : waypointEntries) {
            getInstance().remove(entry);
        }

        int y = 20;
        int numEntries = OmmMap.getWaypoints().length;
        waypointEntries = new WaypointEntryWidget[numEntries];
        Waypoint[] waypoints = OmmMap.getWaypoints();

        for (int i = 0; i < numEntries; i++) {
            waypointEntries[i] = new WaypointEntryWidget(10, y, Text.of(""), waypoints[i], this.textRenderer, waypoints[i].pinned, waypoints[i].visible);
            this.addDrawableChild(waypointEntries[i]);
            y += 25;
        }
    }

    public static void createWaypoint(String name, double lat, double lon, int color, WaypointStyle style, double angle) {
        WaypointFile.addWaypoint(style.toString().toLowerCase(), lat, lon, color, angle, name, false, true);
        WaypointFile.setWaypointsOfThisWorld(false);
        WaypointScreen.getInstance().generateWaypointEntries();
        //Waypoint waypoint = new Waypoint(style.toString().toLowerCase(), lat, lon, color, Double.NaN, name);
    }

    public static int getMidPoint() {
        return midPoint;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean b = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        if (!TileManager.doReverseScroll) verticalAmount *= -1;

        if (mouseX > midPoint) {
            int maxScroll = Math.max(390 - MinecraftClient.getInstance().getWindow().getScaledHeight(), 0);
            createScroll = Math.clamp(
                    createScroll + (verticalAmount < 0 ? -SCROLLSPEED : SCROLLSPEED),
                    0,
                    maxScroll
            );
        } else {
            int maxScroll = Math.max((35 + (waypointEntries.length * 25) - MinecraftClient.getInstance().getWindow().getScaledHeight()), 0);
            entryListScroll = Math.clamp(
                    entryListScroll + (verticalAmount < 0 ? -SCROLLSPEED : SCROLLSPEED),
                    0,
                    maxScroll
            );
        }

        updateWidgetPositions();
        return b;
    }

    private void updateWidgetPositions() {
        midPoint = width / 2;

        WaypointEntryWidget.setScroll(entryListScroll);

        int creationAreaWidth = width - midPoint;

        int sliderWidths = Math.min((creationAreaWidth / 5), 30);
        float marginWidths = (float) (creationAreaWidth - (sliderWidths * 3)) / 4;

        float x = midPoint + marginWidths;

        hueSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20 - createScroll);
        x += marginWidths + sliderWidths;
        saturationSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20 - createScroll);
        x += marginWidths + sliderWidths;
        valueSlider.setDimensionsAndPosition(sliderWidths, 120, (int) x, 20 - createScroll);

        createWaypointButton.setDimensions(midPoint - 40, 20);
        createWaypointButton.setPosition(midPoint + 20, 310 - createScroll);
        saveWaypointButton.setDimensions(midPoint - 40, 20);
        saveWaypointButton.setPosition(midPoint + 20, 310 - createScroll);

        deleteWaypointButton.setDimensions(midPoint - 40, 20);
        deleteWaypointButton.setPosition(midPoint + 20, 350 - createScroll);

        nameField.setWidth(midPoint - 40);
        nameField.setPosition(midPoint + 20, 190 - createScroll);

        longitudeWidget.setWidth(midPoint - 40);
        longitudeWidget.setPosition(midPoint + 20, 250 - createScroll);

        latitudeWidget.setWidth(midPoint - 40);
        latitudeWidget.setPosition(midPoint + 20, 220 - createScroll);

        angleWidget.setWidth(midPoint - 40);
        angleWidget.setPosition(midPoint + 20, 280 - createScroll);

        //        context.fill(midPoint + 20, 148, context.getScaledWindowWidth() - 21, 180, 0xFF000000);
        leftButton.setPosition(midPoint + 11, 157 - createScroll);
        rightButton.setPosition(MinecraftClient.getInstance().getWindow().getScaledWidth() - 18,157 - createScroll);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        updateWidgetPositions();

        context.drawVerticalLine(midPoint, 0, height, 0xFF808080);

        deleteWaypointButton.active = inEditMode;
        deleteWaypointButton.setTooltip(inEditMode ? Tooltip.of(Text.translatable("omm.waypoints.delete-tooltip")) : null);

        if ((inEditMode && saveWaypointButton.isHovered()) || (!inEditMode && createWaypointButton.isHovered())) {
            if (nameField.valueIsValid() && longitudeWidget.valueIsValid() && latitudeWidget.valueIsValid() && angleWidget.valueIsValid()) {
                context.drawBorder(createWaypointButton.getX(), createWaypointButton.getY(), createWaypointButton.getWidth(), createWaypointButton.getHeight(), 0xFF55ff55);
            } else {
                context.drawBorder(createWaypointButton.getX(), createWaypointButton.getY(), createWaypointButton.getWidth(), createWaypointButton.getHeight(), 0xFFFF5555);
            }
        }

        if (inEditMode && deleteWaypointButton.isHovered()) {
            context.drawBorder(deleteWaypointButton.getX(), deleteWaypointButton.getY(), deleteWaypointButton.getWidth(), deleteWaypointButton.getHeight(), 0xFFaa0000);
        }

        //context.fill(140, 20, 160, 40, Color.HSBtoRGB(ColorSliderWidget.hue, ColorSliderWidget.saturation, ColorSliderWidget.value));

        //BufferedImage image = new BufferedImage(diamondWaypoint.getColorModel(), diamondWaypoint.getRaster(), diamondWaypoint.getColorModel().isAlphaPremultiplied(), null);
        //image = colorize(image, ColorSliderWidget.hue);

        context.fill(midPoint + 20, 148 - createScroll, context.getScaledWindowWidth() - 21, 180 - createScroll, 0xFF000000);
        context.drawBorder(midPoint + 20, 148 - createScroll, context.getScaledWindowWidth() - 40 - midPoint, 32, 0xFF808080);
        context.enableScissor(midPoint + 21, 148 - createScroll, context.getScaledWindowWidth() - 21, 180 - createScroll);

        int image = styleSelection.ordinal();
        context.fill(midPoint + (midPoint / 2) - 14, 149 - createScroll, midPoint + (midPoint / 2) + 14, 179 - createScroll, 0xFF404040);
        drawColorizedImage(context, styleIdentifiers[image], midPoint + (midPoint / 2) - 12, 152 - createScroll, 24, 24);

        for (int i = 1; (midPoint + (midPoint / 2) - 12 + (i * 30)) < context.getScaledWindowWidth() - 20; i++) {
            drawColorizedImage(context, styleIdentifiers[(image + i) % styleIdentifiers.length], midPoint + (midPoint / 2) - 12 + (i * 30), 152 - createScroll, 24, 24);
        }

        for (int i = -1; (midPoint + (midPoint / 2) - 12 + (i * 30)) > midPoint - 7; i--) {
            drawColorizedImage(context, styleIdentifiers[(((image + i) % styleIdentifiers.length) + styleIdentifiers.length) % styleIdentifiers.length], midPoint + (midPoint / 2) - 12 + (i * 30), 152 - createScroll, 24, 24);
        }

        context.disableScissor();

    }

    private static double positiseAngle(double angle) {
        return (angle % 360 + 360) % 360;
    }

    private static void drawColorizedImage(DrawContext context, Identifier identifier, int x, int y, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(MinecraftClient.getInstance().getResourceManager().getResource(identifier).get().getInputStream());
            image = colorize(image, ColorSliderWidget.hue, ColorSliderWidget.saturation, ColorSliderWidget.value);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            NativeImage nImage = NativeImage.read(is);

            Identifier wayIdent = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("osmwaypoint", new NativeImageBackedTexture(nImage));
            context.drawTexture(wayIdent, x, y, 0, 0, width, height, width, height);
            MinecraftClient.getInstance().getTextureManager().destroyTexture(wayIdent);

            is.close();
            nImage.close();
            os.close();

        } catch (IOException | IllegalArgumentException e) {
            context.drawTexture(identifier, x, y, 0, 0, width, height, width, height);
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
