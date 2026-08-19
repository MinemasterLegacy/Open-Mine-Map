package net.mmly.openminemap.waypoint;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.Waypoint;

public class WaypointParameterWidget extends EditBox {

    boolean required;
    WaypointValueInputType type;
    Font renderer;

    public WaypointParameterWidget(Font textRenderer, boolean required, WaypointValueInputType type) {
        this(textRenderer, Component.nullToEmpty(""), required, type);
    }

    public WaypointParameterWidget(Font textRenderer, Component text, boolean required, WaypointValueInputType type) {
        super(textRenderer, 20, 20, text);
        this.required = required;
        this.type = type;
        setValue(text.getString());
        moveCursorToStart(false);
        this.renderer = textRenderer;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if (!valueIsValid()) {
            UContext.drawBorder(getX(), getY(), width, height, 0xFFFF5555);
        }

        String suggestion = type.getTranslatedString().replace("_", " ").toLowerCase();
        suggestion = suggestion.substring(0, 1).toUpperCase() + suggestion.substring(1);

        if (getValue().isBlank()) {
            int textWidth = renderer.width(suggestion);
            context.drawString(renderer, suggestion, getX() + (width / 2) - (textWidth / 2), getY() + (height / 2) - (renderer.lineHeight / 2), 0xFF404040);
        }

    }

    public boolean valueIsValid() {
        if (getValue().isBlank() && !required) {
            return true;
        }

        if (this.getValue().isBlank() & !type.isNumber()) return false;

        if (type.isNumber()) {
            try {
                if (Double.isNaN(Double.parseDouble(this.getValue()))) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (!WaypointScreen.getInstance().inEditMode) { //is name
            if (WaypointScreen.instance.editingWaypointName.equals(this.getValue())) return true;
            for (Waypoint waypoint : OmmMap.getWaypoints()) {
                if (waypoint.name.equals(this.getValue()) || getValue().isBlank()) return false;
            }
        }

        if (type.isCoordinate()) {
            double c = Double.parseDouble(this.getValue());
            if (type == WaypointValueInputType.LATITUDE && Math.abs(c) > 85.05112) return false;
            if (type == WaypointValueInputType.LONGITUDE && Math.abs(c) > 180) return false;
        }

        return true;
    }
}