package net.mmly.openminemap.waypoint;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;

public class WaypointParameterWidget extends TextFieldWidget {

    boolean required;
    WaypointValueInputType type;
    TextRenderer renderer;

    public WaypointParameterWidget(TextRenderer textRenderer, boolean required, WaypointValueInputType type) {
        this(textRenderer, Text.of(""), required, type);
    }

    public WaypointParameterWidget(TextRenderer textRenderer, Text text, boolean required, WaypointValueInputType type) {
        super(textRenderer, 20, 20, text);
        this.required = required;
        this.type = type;
        setText(text.getString());
        setCursorToStart(false);
        this.renderer = textRenderer;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if (!valueIsValid()) {
            context.drawBorder(getX(), getY(), width, height, 0xFFFF5555);
        }

        String suggestion = type.getTranslatedString().replace("_", " ").toLowerCase();
        suggestion = suggestion.substring(0, 1).toUpperCase() + suggestion.substring(1);

        if (getText().isBlank()) {
            int textWidth = renderer.getWidth(suggestion);
            context.drawTextWithShadow(renderer, suggestion, getX() + (width / 2) - (textWidth / 2), getY() + (height / 2) - (renderer.fontHeight / 2), 0xFF404040);
        }

    }

    public boolean valueIsValid() {
        if (getText().isBlank() && !required) {
            return true;
        }

        if (this.getText().isBlank() & !type.isNumber()) return false;

        if (type.isNumber()) {
            try {
                if (Double.isNaN(Double.parseDouble(this.getText()))) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (!WaypointScreen.getInstance().inEditMode) { //is name
            if (WaypointScreen.instance.editingWaypointName.equals(this.getText())) return true;
            for (Waypoint waypoint : OmmMap.getWaypoints()) {
                if (waypoint.name.equals(this.getText()) || getText().isBlank()) return false;
            }
        }

        if (type.isCoordinate()) {
            double c = Double.parseDouble(this.getText());
            if (type == WaypointValueInputType.LATITUDE && Math.abs(c) > 85.05112) return false;
            if (type == WaypointValueInputType.LONGITUDE && Math.abs(c) > 180) return false;
        }

        return true;
    }
}