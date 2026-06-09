package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.util.RasterApiKeysFile;
import net.mmly.openminemap.util.TileUrl;

import java.util.ArrayList;

public class CreateRasterScreen extends Screen {

    private ArrayList<TextFieldWidget> fieldWidgets;
    private ButtonWidget doneButton;
    private ButtonLayer addAttributionButton;
    private ButtonLayer removeAttributionButton;
    private static TileUrl tileUrl;
    protected static boolean isNew;
    protected static boolean hasKeyField;
    protected static boolean baseFieldsEditable; // should not affect the key field
    private final Screen returnScreen;
    public static CreateRasterScreen instance;

    public static CreateRasterScreen getInstance() {
        return instance;
    }

    @Override
    public void close() {
        saveCurrentUrl();
        MinecraftClient.getInstance().setScreen(returnScreen);
    }

    private void saveCurrentUrl() {
        if (hasKeyField) {
            RasterApiKeysFile.writeApiKey(tileUrl.presetID, fieldWidgets.get(4).getText());
        }
        //TODO
    }

    /// Pass null for a new tile url
    public CreateRasterScreen(TileUrl url) { //for modifying some existing raster
        super(Text.of(""));
        instance = this;
        tileUrl = url;
        isNew = tileUrl == null;

        if (!isNew) {
            hasKeyField = tileUrl.hasKeyField();
        } else {
            hasKeyField = false;
        }

        if (tileUrl == null) baseFieldsEditable = true;
        else baseFieldsEditable = !tileUrl.isPreset();

        if (MinecraftClient.getInstance().currentScreen instanceof RasterWarningScreen) {
            returnScreen = ((RasterWarningScreen) MinecraftClient.getInstance().currentScreen).parent;
        } else {
            returnScreen = MinecraftClient.getInstance().currentScreen;
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

        doneButton.setPosition(width / 2 - 100, height - 20 - (int) perSpace);

        if (!baseFieldsEditable) return;

        addAttributionButton.setPosition(fieldWidgets.get(3).getRight() + 5, fieldWidgets.get(3).getY());

        removeAttributionButton.visible = false;
        if (getFocused() instanceof TextFieldWidget) {
            TextFieldWidget candidate = (TextFieldWidget) getFocused();
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

    public TileUrl buildRaster() {
        //todo
        return null;
    }

    private TextFieldWidget getNewFieldWidget(boolean isEditable) {
        TextFieldWidget f = new TextFieldWidget(textRenderer, 0, -100, 200, 20, Text.of(""));
        addDrawableChild(f);
        f.setMaxLength(1000);
        if (!isEditable) {
            f.setUneditableColor(0xFF7f7f7f);
            f.setEditableColor(0xFF7f7f7f);
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

        for (int i = 0; i < 3 + tileUrl.attribution_links.length; i++) {
            fieldWidgets.add(getNewFieldWidget(!tileUrl.isPreset()));
        }

        fieldWidgets.get(0).setText(tileUrl.name);
        fieldWidgets.get(1).setText(tileUrl.source_url);
        if (tileUrl.presetID == 0) fieldWidgets.get(2).setText(Text.translatable("omm.osm-attribution").getString());
        else fieldWidgets.get(2).setText(tileUrl.attribution);
        for (int i = 0; i < tileUrl.attribution_links.length; i++) {
            fieldWidgets.get(3+i).setText(tileUrl.attribution_links[i]);
        }

        if (hasKeyField) {
            fieldWidgets.add(getNewFieldWidget(true));
            fieldWidgets.getLast().setText(RasterApiKeysFile.readApiKey(tileUrl.presetID));
        }
    }

    @Override
    protected void init() {
        super.init();

        //TODO translate
        doneButton = ButtonWidget.builder(Text.of(isNew ? "Create" : "Done"), (widget) -> {
            CreateRasterScreen.instance.close(); //todo validate raster on creation
        }).position(0, -100).build();
        doneButton.setWidth(200);
        addDrawableChild(doneButton);

        initFieldWidgets();

        if (baseFieldsEditable) {
            addAttributionButton = new ButtonLayer(ButtonFunction.ADDRASTER);
            removeAttributionButton = new ButtonLayer(ButtonFunction.REMOVERASTER);
            addDrawableChild(addAttributionButton);
            addDrawableChild(removeAttributionButton);
            removeAttributionButton.visible = false;
        }

        updateWidgetPositions();

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        UContext.setContext(context);
        updateWidgetPositions();

        //TODO translate
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

    public MutableText getTranslated() { //TODO
        return Text.literal(this.toString());
    }

}