package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

    public MappablePlayer(double x, double z, double yaw, Text name, int uuidHash) {
        this(x, Double.NaN, z, yaw, name, name, null);
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
        } catch (CoordinateValueError ignored) {}

        if (latLon == null) {
            latitude = Double.NaN;
            longitude = Double.NaN;
            geoYaw = Double.NaN;
            outOfBounds = true;
            return;
        } else {
            latitude = latLon[0];
            longitude = latLon[1];
            outOfBounds = false;
        }

        geoYaw = Direction.getGeoAzimuth(x, z, yaw);

    }

    public double distanceTo(Entity entity) {
        //copied from entity class
        float f = (float)(x - entity.getX());
        float g = (float)(altitude - entity.getY());
        float h = (float)(z - entity.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

}
