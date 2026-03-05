package net.mmly.openminemap.enums;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public enum WebIcon {
    GOOGLE_MAPS("Google Maps"),
    GOOGLE_EARTH("Google Earth"),
    GOOGLE_EARTH_PRO("Google Earth Pro (.kml)"),
    OPEN_STREET_MAP("OpenStreetMap"),
    BING_MAPS("Bing Maps"),
    APPLE_MAPS("Apple Maps");

    private final Tooltip tooltip;

    WebIcon(String tooltipString) {
        this.tooltip = Tooltip.of(Text.of(tooltipString));
    }

    public static WebIcon getUsingId(int id) {
        for (WebIcon enu : WebIcon.values()) {
            if (enu.ordinal() == id) return enu;
        }
        return null;
    }

    public static Tooltip getTooltipUsingId(int id) {
        if (!(id >= 0 && id < WebIcon.values().length)) return null;
        return WebIcon.getUsingId(id).tooltip;
    }
}
