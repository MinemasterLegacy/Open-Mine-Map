package net.mmly.openminemap.enums;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public enum WebIcon {
    GOOGLE_MAPS,
    GOOGLE_EARTH,
    GOOGLE_EARTH_PRO,
    OPEN_STREET_MAP,
    BING_MAPS,
    APPLE_MAPS;

    public static WebIcon getUsingId(int id) {
        return switch (id) {
            case 0 -> GOOGLE_MAPS;
            case 1 -> GOOGLE_EARTH;
            case 2 -> GOOGLE_EARTH_PRO;
            case 3 -> OPEN_STREET_MAP;
            case 4 -> BING_MAPS;
            case 5 -> APPLE_MAPS;
            default -> null;
        };
    }

    public static Tooltip getTooltipUsingId(int id) {
        return Tooltip.of(Text.of(
            switch (id) {
                case 0 -> "Google Maps";
                case 1 -> "Google Earth";
                case 2 -> "Google Earth Pro (.kml)";
                case 3 -> "OpenStreetMap";
                case 4 -> "Bing Maps";
                case 5 -> "Apple Maps";
                default -> "null";
            }));
    }
}
