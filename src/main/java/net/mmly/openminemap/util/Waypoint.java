package net.mmly.openminemap.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.waypoint.ColorSliderWidget;
import net.mmly.openminemap.waypoint.WaypointScreen;
import net.mmly.openminemap.waypoint.WaypointStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Waypoint {

    public double longitude;
    public double latitude;
    public Identifier identifier;
    public double angle;
    public String name;
    public String style;
    public boolean pinned;
    public boolean visible;
    public int color;

    //mapxy here refer to the position at the lowest possible zoom level (18)

    public Waypoint(String style, double latitude, double longitude, int colorHSV, double angle, String name, boolean pinned, boolean visible) {
        WaypointStyle sty;
        try {
            sty = WaypointStyle.getByString(style);
        } catch (IllegalArgumentException e) {
            sty = WaypointStyle.DIAMOND;
        }
        identifier = getColoredIdentifier(Identifier.of("openminemap", "waypoints/"+sty.name().toLowerCase()+".png"), colorHSV);

        this.longitude = longitude;
        this.latitude = latitude;
        this.angle = angle;
        this.name = name;
        this.pinned = pinned;
        this.visible = visible;
        this.color = colorHSV;
        this.style = style;
    }

    private Identifier getColoredIdentifier(Identifier identifier, int colorHSV) {

        MinecraftClient client = MinecraftClient.getInstance();

        int value = colorHSV & 0x0000FF;
        int saturation = (colorHSV >> 8) & 0x0000FF;
        int hue = (colorHSV >> 16) & 0x0000FF;

        try {
            BufferedImage image = ImageIO.read(client.getResourceManager().getResource(identifier).get().getInputStream());
            image = WaypointScreen.colorize(image, (float) hue /256, (float) saturation /256, (float) value /256);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            NativeImage nImage = NativeImage.read(is);

            Identifier wayIdent = client.getTextureManager().registerDynamicTexture("osmwaypoint", new NativeImageBackedTexture(nImage));

            is.close();
            //nImage.close();
            os.close();

            return wayIdent;

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("colorize fail");
            return identifier;
        }

    }
}
