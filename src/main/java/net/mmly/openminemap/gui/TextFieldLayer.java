package net.mmly.openminemap.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.util.UnitConvert;

public class TextFieldLayer extends TextFieldWidget {

    private int ID;
    /*
    0 is SnapAngle input
     */

    public TextFieldLayer(TextRenderer textRenderer, int x, int y, int width, int height, Text text, int id) {
        super(textRenderer, x, y, width, height, text);
        this.ID = id;
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        super.onClick(click, doubled);
        System.out.println("\""+this.getText()+"\"");
        if (ID == 0 && this.getText().isEmpty() && !this.isFocused()) {
            this.setText(UnitConvert.floorToPlace(PlayerAttributes.yaw, 3));
            this.setSelectionStart(0);
            this.setSelectionEnd(this.getText().length());
        }
    }
}
