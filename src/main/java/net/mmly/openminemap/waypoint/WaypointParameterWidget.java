package net.mmly.openminemap.waypoint;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;

public class WaypointParameterWidget extends TextFieldWidget {

    boolean required;
    WaypointValueInputType type;

    public WaypointParameterWidget(TextRenderer textRenderer, boolean required, WaypointValueInputType type) {
        this(textRenderer, Text.of(""), required, type);
    }

    public WaypointParameterWidget(TextRenderer textRenderer, Text text, boolean required, WaypointValueInputType type) {
        super(textRenderer, 20, 20, text);
        this.required = required;
        this.type = type;
        setText(text.getString());
        setCursorToStart(false);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if (!valueIsValid()) {
            context.drawBorder(getX(), getY(), width, height, 0xFFFF5555);
        }

    }

    public boolean valueIsValid() {
        if (getText().isBlank()) {
            return required;
        }

        if (type.isNumber()) {
            try {
                if (Double.isNaN(Double.parseDouble(this.getText()))) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (!WaypointScreen.getInstance().inEditMode) { //is name
            for (Waypoint waypoint : OmmMap.getWaypoints()) {
                if (waypoint.name.equals(this.getText())) return false;
            }
        }

        if (type.isCoordinate()) {
            double c = Double.parseDouble(this.getText());
            if (type == WaypointValueInputType.LATITUDE && Math.abs(c) > UnitConvert.myToLat(0, 0)) return false;
            if (type == WaypointValueInputType.LONGITUDE && Math.abs(c) > UnitConvert.mxToLong(128, 0)) return false;
        }

        return true;
    }
}