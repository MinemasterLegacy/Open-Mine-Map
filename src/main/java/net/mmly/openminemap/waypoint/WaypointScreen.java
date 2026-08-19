package net.mmly.openminemap.waypoint;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.gui.RightClickMenu;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;
import net.mmly.openminemap.util.WaypointFile;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Supplier;

public class WaypointScreen extends Screen {

    private static final int BLACK = 0xFF000000;
    private static final int GRAY = 0xFF7f7f7f;
    private static final int DARK_GRAY = 0xFF3f3f3f;
    private static final int GREEN = 0xFF55ff55;
    private static final int RED = 0xFFFF5555;
    private static final int DARK_RED = 0xFFaa0000;

    private static int pathInt = 0;

    private ColorSliderWidget hueSlider;
    private ColorSliderWidget saturationSlider;
    private ColorSliderWidget valueSlider;
    private Button createWaypointButton;
    private Button saveWaypointButton;
    private Button deleteWaypointButton;
    WaypointList waypointList;

    private WaypointParameterWidget nameField;
    private WaypointParameterWidget longitudeWidget;
    private WaypointParameterWidget latitudeWidget;
    private WaypointParameterWidget angleWidget;

    private WaypointIconSelectButton leftButton;
    private WaypointIconSelectButton rightButton;

    public static WaypointScreen instance;
    private static int midPoint = 0;

    private static int createScroll = 0;

    private static double initLong;
    private static double initLat;
    private static String initName;
    private static boolean initWithValues = false;
    private static boolean initInEditMode = false;
    private static Waypoint initEditWaypoint;
    private boolean shiftPressed = false;

    public boolean inEditMode = false;
    Waypoint editingWaypoint = null;
    public String editingWaypointName = "";

    public WaypointStyle styleSelection = WaypointStyle.DIAMOND;
    ArrayList<WaypointEntryWidget> waypointWidgets = new ArrayList<>();
    ArrayList<AnchorWidget> anchorWidgets = new ArrayList<>();

    private static final Identifier[] styleIdentifiers = new Identifier[] {
            Identifier.fromNamespaceAndPath("openminemap", "waypoints/diamond.png"),
            Identifier.fromNamespaceAndPath("openminemap", "waypoints/star.png"),
            Identifier.fromNamespaceAndPath("openminemap", "waypoints/house.png"),
            Identifier.fromNamespaceAndPath("openminemap", "waypoints/city.png"),
            Identifier.fromNamespaceAndPath("openminemap", "waypoints/cross.png")
    };

    public static WaypointScreen getInstance() {
        return instance;
    }

    public WaypointScreen() {
        super(Component.nullToEmpty("OpenMineMap Waypoints"));
        instance = this;
    }

    public WaypointScreen(double lat, double lon, String name) {
        this(lat, lon);
        initName = name;
    }

    public WaypointScreen(double lat, double lon) {
        super(Component.nullToEmpty("OpenMineMap Waypoints"));
        instance = this;
        initWithValues = true;
        initLong = lon;
        initLat = lat;
        initName = null;
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.input() == GLFW.GLFW_KEY_LEFT_SHIFT || input.input() == GLFW.GLFW_KEY_RIGHT_SHIFT) shiftPressed = true;
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        if (input.input() == GLFW.GLFW_KEY_LEFT_SHIFT || input.input() == GLFW.GLFW_KEY_RIGHT_SHIFT) shiftPressed = false;
        return super.keyReleased(input);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (RightClickMenu.instance.isMouseOver(click.x(), click.y())) {
            if (RightClickMenu.instance.mouseClicked(click, doubled)) {
                return true;
            }
        }
        boolean b = super.mouseClicked(click, doubled);
        if (click.button() == 0) RightClickMenu.disableMenu();
        return b;
    }

    //called by the right click menu to immediately enter edit mode
    public WaypointScreen(Waypoint waypoint) {
        super(Component.nullToEmpty("OpenMineMap Waypoints"));
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
        nameField.setValue(waypoint.name);
        latitudeWidget.setValue(Double.toString(waypoint.latitude));
        longitudeWidget.setValue(Double.toString(waypoint.longitude));
        if (waypoint.angle < 0) {
            angleWidget.setValue("");
        } else {
            angleWidget.setValue(Double.toString(waypoint.angle));
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
        nameField.setValue("");
        latitudeWidget.setValue("");
        longitudeWidget.setValue("");
        angleWidget.setValue("");
        saveWaypointButton.visible = false;
        createWaypointButton.visible = true;
    }

    @Override
    protected void init() {
        super.init();

        hueSlider = new ColorSliderWidget(20, 20, 20, 120, ColorSliderType.HUE);
        saturationSlider = new ColorSliderWidget(60, 20, 20, 120, ColorSliderType.SATURATION);
        valueSlider = new ColorSliderWidget(100, 20, 20, 120, ColorSliderType.VALUE);

        this.addRenderableWidget(hueSlider);
        this.addRenderableWidget(saturationSlider);
        this.addRenderableWidget(valueSlider);

        generateWaypointEntries();

        createWaypointButton = Button.builder(Component.translatable("omm.waypoints.button.create"), (buttonWidget) -> {
            if (!fieldsAreValid()) return;
            WaypointScreen.createWaypoint(
                    nameField.getValue(),
                    Double.parseDouble(latitudeWidget.getValue()),
                    Double.parseDouble(longitudeWidget.getValue()),
                    getSelectedHSB(),
                    styleSelection,
                    angleWidget.getValue().isBlank() ? -1 : Double.parseDouble(angleWidget.getValue())
                    );
        }).build();
        this.addRenderableWidget(createWaypointButton);

        saveWaypointButton = Button.builder(Component.translatable("omm.waypoints.button.save"), (buttonWidget) -> {
            if (!fieldsAreValid()) return;
            WaypointScreen.saveEditingWaypoint();
            exitEditMode();
        }).build();
        this.addRenderableWidget(saveWaypointButton);

        deleteWaypointButton = Button.builder(Component.translatable("omm.waypoints.button.delete"), (buttonWidget) -> {
            if (!WaypointScreen.instance.inEditMode) return;
            WaypointScreen.deleteEditingWaypoint();
            exitEditMode();
        }).build();
        this.addRenderableWidget(deleteWaypointButton);

        nameField = new WaypointParameterWidget(this.font,
                Component.nullToEmpty(initWithValues ?
                            (initName != null ?
                                initName :
                                UnitConvert.floorToPlace(initLat, 7) + ", " + UnitConvert.floorToPlace(initLong, 7)) :
                            "")
                , true, WaypointValueInputType.NAME);
        nameField.setMaxLength(200);
        this.addRenderableWidget(nameField);

        latitudeWidget = new WaypointParameterWidget(this.font,  Component.nullToEmpty(initWithValues ? Double.toString(initLat) : ""), true, WaypointValueInputType.LATITUDE);
        this.addRenderableWidget(latitudeWidget);

        longitudeWidget = new WaypointParameterWidget(this.font,  Component.nullToEmpty(initWithValues ? Double.toString(initLong) : ""), true, WaypointValueInputType.LONGITUDE);
        this.addRenderableWidget(longitudeWidget);

        angleWidget = new WaypointParameterWidget(this.font, Component.nullToEmpty(""), false, WaypointValueInputType.SNAP_ANGLE);
        this.addRenderableWidget(angleWidget);

        leftButton = new WaypointIconSelectButton(-1);
        this.addRenderableWidget(leftButton);

        rightButton = new WaypointIconSelectButton(1);
        this.addRenderableWidget(rightButton);

        this.addRenderableWidget(new RightClickMenu(this.font));

        if (initWithValues) {
            nameField.moveCursorToStart(false);
            nameField.setHighlightPos(nameField.getValue().length());
            nameField.setCursorPosition(0);
        }

        editingWaypoint = null;
        editingWaypointName = "";
        inEditMode = false;
        saveWaypointButton.visible = false;

        if (initInEditMode) {
            enableEditMode(initEditWaypoint);
            latitudeWidget.moveCursorToStart(false);
            longitudeWidget.moveCursorToStart(false);
            angleWidget.moveCursorToStart(false);
            nameField.moveCursorToStart(false);
        }

        initWithValues = false;
        initInEditMode = false;

        updateWidgetPositions();
        MapScreen.updateAltScreenMap(null, this);
    }

    public static void deleteEditingWaypoint() {
        if (WaypointFile.deleteWaypoint(getInstance().editingWaypointName)) {
            WaypointFile.setWaypointsOfThisWorld(false);
            instance.generateWaypointEntries();
        } else {
            OpenMineMapClient.debugMessages.add(Component.translatable("omm.error.waypoint-delete-failed").toString());
        }
    }

    private void refreshMidpoint() {
        midPoint = 240;
    }

    public static void saveEditingWaypoint() {
        if (WaypointFile.overwriteWaypoint(
                instance.editingWaypointName,
                instance.nameField.getValue(),
                Double.parseDouble(instance.latitudeWidget.getValue()),
                Double.parseDouble(instance.longitudeWidget.getValue()),
                instance.getSelectedHSB(),
                instance.angleWidget.getValue().isBlank() ? -1 : positiseAngle(Double.parseDouble(instance.angleWidget.getValue())),
                instance.styleSelection.toString().toLowerCase()
        )) {
            instance.generateWaypointEntries();
        } else {
            OpenMineMapClient.debugMessages.add(Component.translatable("omm.error.waypoint-property-failiure").getString());
        }
    }

    public boolean fieldsAreValid() {
        return nameField.valueIsValid() && longitudeWidget.valueIsValid() && latitudeWidget.valueIsValid() && angleWidget.valueIsValid();
    }

    public int getSelectedHSB() {
        int hue = (int) (ColorSliderWidget.hue * 255);
        int sat = (int) (ColorSliderWidget.saturation * 255);
        int val = (int) (ColorSliderWidget.value * 255);

        //System.out.println(hue +"\t"+ sat +"\t"+ val);

        return hue << 16 | sat << 8 | val;
    }

    private void generateWaypointEntries() {

        refreshMidpoint();
        if (waypointList != null) this.removeWidget(waypointList);
        for (WaypointEntryWidget widget : waypointWidgets) {
            this.removeWidget(widget);
        }

        waypointList = new WaypointList(Minecraft.getInstance(), midPoint, height, 0, 24);
        this.addRenderableWidget(waypointList);

        waypointWidgets.clear();
        anchorWidgets.clear();

        int numEntries = OmmMap.getWaypoints().length;
        //waypointEntries = new WaypointEntryWidget[numEntries];
        Waypoint[] waypoints = OmmMap.getWaypoints();

        for (int i = 0; i < numEntries; i++) {
            waypointWidgets.add(new WaypointEntryWidget(Component.nullToEmpty(""), waypoints[i], this.font, waypoints[i].pinned, waypoints[i].visible));
            this.addWaypointWidget(waypointWidgets.getLast());
        }
    }

    private void addWaypointWidget(WaypointEntryWidget widget) {
        waypointWidgets.add(widget);
        AnchorWidget anchor = new AnchorWidget();
        this.addRenderableWidget(widget);

        waypointList.addEntry(anchor);
        anchorWidgets.add(anchor);
        widget.setAnchor(anchor);
        anchor.setWidget(widget);
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

        if (shiftPressed) {
            if (horizontalAmount > 0) leftButton.onClick(mouseX, mouseY);
            else rightButton.onClick(mouseX, mouseY);
        } else if (mouseX > midPoint) {
            int maxScroll = Math.max(390 - Minecraft.getInstance().getWindow().getGuiScaledHeight(), 0);
            createScroll = Math.clamp(
                    createScroll + (verticalAmount < 0 ? -10 : 10),
                    0,
                    maxScroll
            );
        }

        updateWidgetPositions();
        return b;
    }

    private void updateWidgetPositions() {
        refreshMidpoint();

        int creationAreaWidth = width - midPoint;

        int sliderWidths = Math.min((creationAreaWidth / 5), 30);
        float marginWidths = (float) (creationAreaWidth - (sliderWidths * 3)) / 4;

        float x = midPoint + marginWidths;

        hueSlider.setRectangle(sliderWidths, 120, (int) x, 20 - createScroll);
        x += marginWidths + sliderWidths;
        saturationSlider.setRectangle(sliderWidths, 120, (int) x, 20 - createScroll);
        x += marginWidths + sliderWidths;
        valueSlider.setRectangle(sliderWidths, 120, (int) x, 20 - createScroll);

        int elementWidths = width - midPoint - 40;
        int elementXs = midPoint + 20;

        createWaypointButton.setWidth(elementWidths);
        createWaypointButton.setPosition(elementXs, 310 - createScroll);
        saveWaypointButton.setWidth(elementWidths);
        saveWaypointButton.setPosition(elementXs, 310 - createScroll);

        deleteWaypointButton.setWidth(elementWidths);
        deleteWaypointButton.setPosition(elementXs, 350 - createScroll);

        nameField.setWidth(elementWidths);
        nameField.setPosition(elementXs, 190 - createScroll);

        longitudeWidget.setWidth(elementWidths);
        longitudeWidget.setPosition(elementXs, 250 - createScroll);

        latitudeWidget.setWidth(elementWidths);
        latitudeWidget.setPosition(elementXs, 220 - createScroll);

        angleWidget.setWidth(elementWidths);
        angleWidget.setPosition(elementXs, 280 - createScroll);

        //        context.fill(midPoint + 20, 148, context.getScaledWindowWidth() - 21, 180, 0xFF000000);
        leftButton.setPosition(midPoint + 11, 157 - createScroll);
        rightButton.setPosition(Minecraft.getInstance().getWindow().getGuiScaledWidth() - 18,157 - createScroll);

    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {

        deleteWaypointButton.active = inEditMode;
        deleteWaypointButton.setTooltip(inEditMode ? Tooltip.create(Component.translatable("omm.waypoints.delete-tooltip")) : null);

        super.render(context, mouseX, mouseY, delta);
        UContext.setContext(context);

        while (pathInt > -1) {
            Minecraft.getInstance().getTextureManager().release(Identifier.fromNamespaceAndPath("openminemap", "waypoint-s-shaded-"+pathInt));
            pathInt--;
        }
        pathInt = 0;

        updateWidgetPositions();
        context.vLine(midPoint, -3, height, GRAY);

        if ((inEditMode && saveWaypointButton.isHovered()) || (!inEditMode && createWaypointButton.isHovered())) {
            if (nameField.valueIsValid() && longitudeWidget.valueIsValid() && latitudeWidget.valueIsValid() && angleWidget.valueIsValid()) {
                UContext.drawBorder(createWaypointButton.getX(), createWaypointButton.getY(), createWaypointButton.getWidth(), createWaypointButton.getHeight(), GREEN);
            } else {
                UContext.drawBorder(createWaypointButton.getX(), createWaypointButton.getY(), createWaypointButton.getWidth(), createWaypointButton.getHeight(), RED);
            }
        }

        if (inEditMode && deleteWaypointButton.isHovered()) {
            UContext.drawBorder(deleteWaypointButton.getX(), deleteWaypointButton.getY(), deleteWaypointButton.getWidth(), deleteWaypointButton.getHeight(), DARK_RED);
        }

        //context.fill(140, 20, 160, 40, Color.HSBtoRGB(ColorSliderWidget.hue, ColorSliderWidget.saturation, ColorSliderWidget.value));

        //BufferedImage image = new BufferedImage(diamondWaypoint.getColorModel(), diamondWaypoint.getRaster(), diamondWaypoint.getColorModel().isAlphaPremultiplied(), null);
        //image = colorize(image, ColorSliderWidget.hue);

        int createMidpoint = (width - midPoint) / 2 + midPoint;

        context.fill(midPoint + 20, 148 - createScroll, context.guiWidth() - 21, 180 - createScroll, BLACK);
        UContext.drawBorder(midPoint + 20, 148 - createScroll, context.guiWidth() - 40 - midPoint, 32, GRAY);
        context.enableScissor(midPoint + 21, 148 - createScroll, context.guiWidth() - 21, 180 - createScroll);

        int image = styleSelection.ordinal();
        context.fill(createMidpoint - 14, 149 - createScroll, createMidpoint + 14, 179 - createScroll, DARK_GRAY);
        drawColorizedImage(context, styleIdentifiers[image], createMidpoint - 12, 152 - createScroll, 24, 24);

        for (int i = 1; (createMidpoint - 12 + (i * 30)) < context.guiWidth() - 20; i++) {
            drawColorizedImage(context, styleIdentifiers[(image + i) % styleIdentifiers.length], createMidpoint - 12 + (i * 30), 152 - createScroll, 24, 24);
        }

        for (int i = -1; (createMidpoint - 12 + (i * 30)) > midPoint - 7; i--) {
            drawColorizedImage(context, styleIdentifiers[(((image + i) % styleIdentifiers.length) + styleIdentifiers.length) % styleIdentifiers.length], createMidpoint - 12 + (i * 30), 152 - createScroll, 24, 24);
        }

        UContext.drawDottedVerticalLine(148 - createScroll, 180 - createScroll, midPoint + 21, GRAY);
        UContext.drawDottedVerticalLine(148 - createScroll, 180 - createScroll, context.guiWidth() - 22, GRAY);

        context.disableScissor();

        RightClickMenu.instance.drawWidget(context, this.font);

    }

    private static double positiseAngle(double angle) {
        return (angle % 360 + 360) % 360;
    }

    private static void drawColorizedImage(GuiGraphics context, Identifier identifier, int x, int y, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(Minecraft.getInstance().getResourceManager().getResource(identifier).get().open());
            image = colorize(image, ColorSliderWidget.hue, ColorSliderWidget.saturation, ColorSliderWidget.value);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            NativeImage nImage = NativeImage.read(is);

            Identifier wayIdent = Identifier.fromNamespaceAndPath("openminemap", "waypoint-s-shaded-"+pathInt);
            Minecraft.getInstance().getTextureManager().register(wayIdent, new DynamicTexture(new nameSupplier(), nImage));
            pathInt++;
            context.blit(RenderPipelines.GUI_TEXTURED, wayIdent, x, y, 0, 0, width, height, width, height);

            is.close();
            nImage.close();
            os.close();

        } catch (IOException | IllegalArgumentException e) {
            context.blit(RenderPipelines.GUI_TEXTURED, identifier, x, y, 0, 0, width, height, width, height);
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
    public void onClose() {
        //super.close();
        Minecraft.getInstance().setScreen(
                new MapScreen()
        );
        MapScreen.updateAltScreenMap(this);
        WaypointFile.save();
    }
}

class nameSupplier implements Supplier<String> {
    @Override
    public String get() {
        return "osmTileName";
    }
}