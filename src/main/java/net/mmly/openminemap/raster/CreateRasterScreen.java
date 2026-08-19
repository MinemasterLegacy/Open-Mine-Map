package net.mmly.openminemap.raster;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.TileUrlErrorType;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.util.RasterApiKeysFile;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Locale;

public class CreateRasterScreen extends Screen {

    private ArrayList<EditBox> fieldWidgets;
    private Button doneButton;
    private Button cancelButton;
    private ButtonLayer addAttributionButton;
    private ButtonLayer removeAttributionButton;
    private static TileUrl originalRaster;
    protected static boolean isNew;
    protected static boolean hasKeyField;
    protected static boolean baseFieldsEditable; // should not affect the key field
    private final Screen returnScreen;
    public static CreateRasterScreen instance;
    public static LayerType layerType = null;

    public static CreateRasterScreen getInstance() {
        return instance;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(returnScreen);
        layerType = null;
    }

    private void saveCurrentUrl() {
        if (hasKeyField) {
            RasterApiKeysFile.writeApiKey(originalRaster.presetID, fieldWidgets.getLast().getValue());
            return;
        }

        if (baseFieldsEditable) {
            TileUrl raster = buildRaster();
            if (rasterIsValid(raster, originalRaster) != null) return;
            if (layerType == null) return;
            if (isNew) RasterProvider.addCustomRaster(raster);
            else RasterProvider.replaceCustomRaster(originalRaster, raster);
            TileUrlFile.saveCustomRastersToFile();
        }
    }

    /// Pass null for a new tile url
    public CreateRasterScreen(TileUrl url) { //for modifying some existing raster
        super(Component.nullToEmpty(""));
        instance = this;
        originalRaster = url;
        isNew = originalRaster == null;

        if (!isNew) {
            hasKeyField = originalRaster.hasKeyField();
        } else {
            hasKeyField = false;
        }

        if (originalRaster == null) baseFieldsEditable = true;
        else baseFieldsEditable = !originalRaster.isPreset();

        if (Minecraft.getInstance().screen instanceof RasterWarningScreen) {
            returnScreen = ((RasterWarningScreen) Minecraft.getInstance().screen).parent;
        } else {
            returnScreen = Minecraft.getInstance().screen;
        }
    }

    private void updateWidgetPositions() {
        int numElements = fieldWidgets.size() + 1;
        int numGroupings = 5 + (hasKeyField ? 1 : 0);
        int clearSpace = height - numElements * 20;
        double perSpace = (double) clearSpace / (numGroupings + 1);
        double yOffset = perSpace;

        for (int i = 0; i < 4; i++) {
            fieldWidgets.get(i).setX(width / 2 - 100);
            fieldWidgets.get(i).setY((int) yOffset);
            yOffset += 20 + perSpace;
        }

        yOffset -= perSpace;
        for (int i = 4; i < fieldWidgets.size(); i++) {
            fieldWidgets.get(i).setX(width / 2 - 100);
            fieldWidgets.get(i).setY((int) yOffset);
            yOffset += 20;
        }

        if (hasKeyField) {
            fieldWidgets.getLast().setY(fieldWidgets.getLast().getY() + (int) perSpace);
        }

        int edgeMargin = (width - 10 - 2 * Button.SMALL_WIDTH) / 2;
        doneButton.setPosition(edgeMargin, height - 20 - (int) perSpace);
        cancelButton.setPosition(edgeMargin + 10 + Button.SMALL_WIDTH, height - 20 - (int) perSpace);


        if (!baseFieldsEditable) return;

        addAttributionButton.setPosition(fieldWidgets.get(3).getRight() + 5, fieldWidgets.get(3).getY());

        removeAttributionButton.visible = false;
        if (getFocused() instanceof EditBox) {
            EditBox candidate = (EditBox) getFocused();
            int numField = fieldWidgets.indexOf(candidate);
            if (hasKeyField && numField == fieldWidgets.size()) return;
            if (numField < 4) return;
            removeAttributionButton.visible = true;
            removeAttributionButton.setPosition(candidate.getX() - 25, candidate.getY());
        }
    }

    public void addRasterField() {
        if (!baseFieldsEditable) return;
        fieldWidgets.add(fieldWidgets.size() - (hasKeyField ? 1 : 0), getNewFieldWidget(true));
    }

    public void removeRasterField() {
        if (!baseFieldsEditable) return;
        for (int i = 4; i < fieldWidgets.size() - (hasKeyField ? 1 : 0); i++) {
            if (fieldWidgets.get(i).getY() == removeAttributionButton.getY()) {
                fieldWidgets.get(i).visible = false;
                fieldWidgets.get(i).active = false;
                fieldWidgets.remove(i);
                return;
            }
        }
    }

    private String rasterIsValid(TileUrl raster) {
        return rasterIsValid(raster, null);
    }

    private String rasterIsValid(TileUrl raster, TileUrl nameIgnoredRaster) {
        TileUrlErrorType errorType = TileUrlFile.checkValidityOf(raster, nameIgnoredRaster);
        if (errorType == TileUrlErrorType.NO_ERROR) return null;
        else return Component.translatable(errorType.translationKey).getString();
    }

    public TileUrl buildRaster() {
        String name = fieldWidgets.get(0).getValue();
        String source = fieldWidgets.get(1).getValue();
        String attribution = fieldWidgets.get(2).getValue();
        if (name.isEmpty()) name = null;
        if (source.isEmpty()) source = null;
        if (attribution.isEmpty()) attribution = null;
        return new TileUrl(
                name,
                source,
                attribution,
                getAttributionLinksList(),
                layerType
        );
    }

    private String[] getAttributionLinksList() {
        String[] links = new String[fieldWidgets.size() - 3 - (hasKeyField ? 1 : 0)];
        for (int i = 0; i < fieldWidgets.size() - 3 - (hasKeyField ? 1 : 0); i++) {
            links[i] = fieldWidgets.get(3+i).getValue();
            if (links[i].isEmpty()) links[i] = null;
        }
        return links;
    }

    private EditBox getNewFieldWidget(boolean isEditable) {
        EditBox f = new EditBox(font, 0, -100, 200, 20, Component.nullToEmpty(""));
        addRenderableWidget(f);
        f.setMaxLength(1000);
        if (!isEditable) {
            f.setTextColorUneditable(0xFF7f7f7f);
            f.setTextColor(0xFF7f7f7f);
            f.setEditable(false);
        }
        return f;
    }

    private void initFieldWidgets() {
        fieldWidgets = new ArrayList<>();

        if (isNew) {
            for (int i = 0; i < 4; i++) {
                fieldWidgets.add(getNewFieldWidget(true));
            }
            return;
        }
        //tileUrl will not be null past this point

        for (int i = 0; i < 3 + originalRaster.attribution_links.length; i++) {
            fieldWidgets.add(getNewFieldWidget(!originalRaster.isPreset()));
        }

        fieldWidgets.get(0).setValue(originalRaster.name);
        fieldWidgets.get(1).setValue(originalRaster.source_url);
        if (originalRaster.presetID == 0) fieldWidgets.get(2).setValue(Component.translatable("omm.osm-attribution").getString());
        else fieldWidgets.get(2).setValue(originalRaster.attribution);
        for (int i = 0; i < originalRaster.attribution_links.length; i++) {
            fieldWidgets.get(3+i).setValue(originalRaster.attribution_links[i]);
        }

        if (hasKeyField) {
            fieldWidgets.add(getNewFieldWidget(true));
            fieldWidgets.getLast().setValue(RasterApiKeysFile.readApiKey(originalRaster.presetID));
        }
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        boolean b = super.keyPressed(input);
        if (!ConfigOptions.__SHOW_DEVELOPER_OPTIONS.getAsBoolean()) return b;
        if (input.input() != GLFW.GLFW_KEY_RIGHT_ALT) return b;
        if (!isNew) return b;

        fieldWidgets.get(0).setValue("Dummy Raster");
        fieldWidgets.get(1).setValue("https://a.a.a{x}{y}{z}");
        fieldWidgets.get(2).setValue("{e}");
        fieldWidgets.get(3).setValue("https://a.a.a");
        saveCurrentUrl();
        onClose();
        return b;
    }

    @Override
    protected void init() {
        super.init();

        doneButton = Button.builder(isNew ? Component.translatable("omm.text.create") : Component.translatable("omm.text.done"), (widget) -> {
            if (TileUrlFile.checkValidityOf(buildRaster(), originalRaster) != TileUrlErrorType.NO_ERROR) return;
            saveCurrentUrl();
            CreateRasterScreen.instance.onClose();
        }).pos(0, -100).build();
        doneButton.setWidth(Button.SMALL_WIDTH);
        addRenderableWidget(doneButton);

        cancelButton = Button.builder(Component.translatable("gui.cancel"), (widget) -> {
            CreateRasterScreen.instance.onClose();
        }).pos(0, -100).build();
        cancelButton.setWidth(Button.SMALL_WIDTH);
        addRenderableWidget(cancelButton);

        initFieldWidgets();

        if (baseFieldsEditable) {
            addAttributionButton = new ButtonLayer(ButtonFunction.ADDRASTER);
            removeAttributionButton = new ButtonLayer(ButtonFunction.REMOVERASTER);
            addRenderableWidget(addAttributionButton);
            addRenderableWidget(removeAttributionButton);
            removeAttributionButton.visible = false;
        }

        updateWidgetPositions();

    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        UContext.setContext(context);
        updateWidgetPositions();

        if (isNew) {
            String validity = rasterIsValid(buildRaster(), originalRaster);
            if (validity == null) {
                doneButton.active = true;
                doneButton.setTooltip(null);
            } else {
                doneButton.active = false;
                doneButton.setTooltip(Tooltip.create(Component.nullToEmpty(validity)));
            }
        }

        for (int i = 0; i < 4; i++) {
            UContext.drawJustifiedText(Fields.inOrder[i].getTranslated(), Justify.RIGHT, fieldWidgets.get(i).getX() - 7, fieldWidgets.get(i).getY() + 6, 0xFFFFFFFF, true);
        }

        if (hasKeyField) {
            UContext.drawJustifiedText(Fields.inOrder[4].getTranslated(), Justify.RIGHT, fieldWidgets.getLast().getX() - 7, fieldWidgets.getLast().getY() + 6, 0xFFFFFFFF, true);
        }

    }
}

enum Fields {
    NAME,
    SOURCE,
    ATTRIBUTION,
    LINKS,
    KEY;

    public static final Fields[] inOrder = new Fields[] {NAME, SOURCE, ATTRIBUTION, LINKS, KEY};

    public MutableComponent getTranslated() {
        return Component.translatable("omm.raster.field." + this.toString().toLowerCase(Locale.US));
    }

}