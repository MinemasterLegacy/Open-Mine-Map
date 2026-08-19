package net.mmly.openminemap.enums;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public enum WebIcon {
    GOOGLE_MAPS("Google Maps", "gm"),
    GOOGLE_EARTH("Google Earth", "ge"),
    GOOGLE_EARTH_PRO("Google Earth Pro (.kml)", "gep"),
    OPEN_STREET_MAP("OpenStreetMap", "osm"),
    YANDEX_MAPS("Yandex Maps", "yx"),
    BING_MAPS("Bing Maps", "bm"),
    APPLE_MAPS("Apple Maps", "am"),
    BUILD_THE_EARTH("BuildTheEarth", "bte"),
    MAPILLARY("Mapillary", "mpy"),
    LOOKMAP("Lookmap", "lm");

    //public final Tooltip tooltip;
    public final Component tooltipText;
    public final String imageName;
    public final Identifier icon;
    public final Identifier highlight;

    public static final List<WebIcon> ORDERED_LIST = List.of(GOOGLE_MAPS, GOOGLE_EARTH, GOOGLE_EARTH_PRO, OPEN_STREET_MAP, YANDEX_MAPS, BING_MAPS, APPLE_MAPS, BUILD_THE_EARTH, MAPILLARY, LOOKMAP);

    WebIcon(String tooltipString, String imageName) {
        this.imageName = imageName;
        //this.tooltip = Tooltip.of(Text.of(tooltipString));
        this.tooltipText = Component.nullToEmpty(tooltipString);
        this.icon = Identifier.fromNamespaceAndPath("openminemap", "webicons/icons/" + imageName + ".png");
        this.highlight = Identifier.fromNamespaceAndPath("openminemap", "webicons/selections/" + imageName + ".png");
    }

    public static WebIcon getEnumFromName(String imageName) {
        for (WebIcon icon : WebIcon.values()) {
            if (icon.imageName.equals(imageName)) return icon;
        }
        return null;
    }
}
