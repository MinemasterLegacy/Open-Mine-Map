package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
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
    public final Text stylizedName;
    public final Text name;
    public final UUID uuid;
    private final double x;
    private final double z;

    public MappablePlayer(PlayerEntity player) {
        this(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getName(), player.getStyledDisplayName(), player.getUuid());
    }

    public MappablePlayer(double lat, double lon, double yaw, UUID uuid) {
        this.outOfBounds = Double.isNaN(lat);
        this.latitude = lat;
        this.longitude = lon;
        this.uuid = uuid;
        this.altitude = Double.NaN;

        Text name = PlayersManager.getDisplayNameOf(uuid);

        if (name == null) {
            this.stylizedName = Text.of("(unknown)");
            this.name = stylizedName;
        } else {
            this.stylizedName = name;
            this.name = Text.of(stylizedName);
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

    public MappablePlayer(double x, double y, double z, double yaw, Text name, Text stylizedName, UUID uuid) {

        this.x = x;
        this.z = z;
        this.uuid = uuid;
        altitude = y;
        this.name = name;
        this.stylizedName = stylizedName;

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
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

}
