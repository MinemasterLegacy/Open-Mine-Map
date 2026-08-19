package net.mmly.openminemap.util;

import net.minecraft.resources.Identifier;
import net.mmly.openminemap.waypoint.WaypointStyle;

public class Waypoint extends NamedLocation{

    public Identifier identifier;
    public String style;
    public boolean pinned;
    public boolean visible;
    public int color;

    //mapxy here refer to the position at the lowest possible zoom level (18)

    public Waypoint(String style, double latitude, double longitude, int colorHSV, double angle, String name, boolean pinned, boolean visible) {
        super(name, latitude, longitude, angle);
        WaypointStyle sty;
        try {
            sty = WaypointStyle.getByString(style);
        } catch (IllegalArgumentException e) {
            sty = WaypointStyle.DIAMOND;
        }
        identifier = ColorUtil.getColoredIdentifier(Identifier.fromNamespaceAndPath("openminemap", "waypoints/"+sty.name().toLowerCase()+".png"), colorHSV);

        this.pinned = pinned;
        this.visible = visible;
        this.color = colorHSV;
        this.style = style;
    }
}
