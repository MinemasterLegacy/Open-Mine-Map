package net.mmly.openminemap.map;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.projection.Projection;

import java.util.UUID;

public class MappablePlayer {

    public final boolean outOfBounds;
    public final double latitude;
    public final double longitude;
    public final double altitude;
    public final double geoYaw;
    public final Component stylizedName;
    public final Component name;
    public final UUID uuid;
    private final double x;
    private final double z;
    private final OverlayVisibility visibility;

    private static final Component fallbackText = Component.nullToEmpty("(unknown)");

    @Deprecated
    public MappablePlayer(OverlayVisibility visibility) {
        //Purely for visibilty comparisons
        outOfBounds = false;
        latitude = Double.NaN;
        longitude = Double.NaN;
        altitude = Double.NaN;
        geoYaw = Double.NaN;
        stylizedName = fallbackText;
        name = fallbackText;
        uuid = null;
        x = Double.NaN;
        z = Double.NaN;
        this.visibility = visibility;
    }

    public MappablePlayer(Player player, OverlayVisibility visibility) {
        this(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getName(), player.getFeedbackDisplayName(), player.getUUID(), visibility);
    }

    public MappablePlayer(double lat, double lon, double yaw, UUID uuid, OverlayVisibility visibility) {
        this.outOfBounds = Double.isNaN(lat);
        this.latitude = lat;
        this.longitude = lon;
        this.uuid = uuid;
        this.altitude = Double.NaN;
        this.visibility = visibility;

        Component name = PlayersManager.getDisplayNameOf(uuid);

        if (name == null) {
            this.stylizedName = fallbackText;
            this.name = fallbackText;
        } else {
            this.stylizedName = name;
            this.name = Component.translationArg(stylizedName);
        }

        double[] mcxz;
        try {
            mcxz = Projection.from_geo(lat, lon);
        } catch (CoordinateValueError e) {
            this.x = Double.NaN;
            this.z = Double.NaN;
            this.geoYaw = Double.NaN;
            return;
        }

        this.x = mcxz[0];
        this.z = mcxz[1];
        this.geoYaw = Direction.getGeoAzimuth(x, z, yaw);
    }

    public MappablePlayer(double x, double y, double z, double yaw, Component name, Component stylizedName, UUID uuid, OverlayVisibility visibility) {

        this.x = x;
        this.z = z;
        this.uuid = uuid;
        altitude = y;
        this.name = name;
        this.stylizedName = stylizedName;
        this.visibility = visibility;

        double[] latLon = null;
        try {
            latLon = Projection.to_geo(x, z);
            if (Double.isNaN(latLon[0])) latLon = null;
        } catch (CoordinateValueError ignored) {}

        if (latLon == null) {
            latitude = Double.NaN;
            longitude = Double.NaN;
            geoYaw = Double.NaN;
            outOfBounds = true;
        } else {
            latitude = latLon[0];
            longitude = latLon[1];
            geoYaw = Direction.getGeoAzimuth(x, z, yaw);
            outOfBounds = false;
        }

    }

    public double distanceTo(Entity entity) {
        //copied from entity class
        float f = (float)(x - entity.getX());
        float g = (float)(altitude - entity.getY());
        float h = (float)(z - entity.getZ());
        return Mth.sqrt(f * f + g * g + h * h);
    }

    public boolean isPlayerDrawable() {
        return TileManager.showPlayers.id >= visibility.id;
    }

    public boolean isIndicatorDrawable() {
        return TileManager.showDirectionIndicators.id >= visibility.id;
    }

}
