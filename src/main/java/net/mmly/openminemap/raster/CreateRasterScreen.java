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
import net.mmly.openminemap.util.TileUrl;

public class CreateRasterScreen extends Screen {

    private TextFieldWidget[] fieldWidgets;
    private ButtonWidget doneButton;
    private TileUrl tileUrl;
    protected boolean isNew;
    protected boolean keyField;
    private final Screen returnScreen;
    public static CreateRasterScreen instance;

    public static CreateRasterScreen getInstance() {
        return instance;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(returnScreen);
    }

    /// Pass null for a new tile url
    public CreateRasterScreen(TileUrl url) { //for modifying some existing raster
        super(Text.of(""));
        instance = this;
        this.tileUrl = url;
        this.isNew = tileUrl == null;
        if (!isNew) {
            keyField = tileUrl.hasKeyField();
        } else {
            keyField = false;
        }
        returnScreen = MinecraftClient.getInstance().currentScreen;
    }

    private void updateWidgetPositions() {
        int numElements = fieldWidgets.length + 1;
        int clearSpace = height - numElements * 20;
        double perSpace = (double) clearSpace / (numElements + 1);
        double yOffset = perSpace;
        for (TextFieldWidget widget : fieldWidgets) {
            widget.setX(width / 2 - 100);
            widget.setY((int) yOffset);
            yOffset += 20 + perSpace;
        }
        doneButton.setPosition(width / 2 - 100, (int) yOffset);

    }

    @Override
    protected void init() {
        super.init();

        fieldWidgets = new TextFieldWidget[keyField ? 5 : 4];
        for (int i = 0; i < fieldWidgets.length; i++) {
            fieldWidgets[i] = new TextFieldWidget(textRenderer, 200, 20, Text.of(""));
            fieldWidgets[0].setY(-100);
            addDrawableChild(fieldWidgets[i]);
            fieldWidgets[i].setMaxLength(1000);
        }

        fieldWidgets[0].setText(tileUrl.name);
        fieldWidgets[1].setText(tileUrl.source_url);
        fieldWidgets[2].setText(tileUrl.attribution);
        fieldWidgets[3].setText(tileUrl.attribution_links[0]);

        //TODO translate
        doneButton = ButtonWidget.builder(Text.of("Done"), (widget) -> {
            CreateRasterScreen.instance.close();
        }).position(0, -100).build();
        doneButton.setWidth(200);
        addDrawableChild(doneButton);

        updateWidgetPositions();

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        UContext.setContext(context);
        updateWidgetPositions();

        //TODO translate
        for (int i = 0; i < fieldWidgets.length; i++) {
            UContext.drawJustifiedText(Fields.inOrder[i].getTranslated(), Justify.RIGHT, fieldWidgets[i].getX() - 7, fieldWidgets[i].getY() + 6, 0xFFFFFFFF, true);
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