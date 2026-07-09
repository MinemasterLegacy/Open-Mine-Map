package net.mmly.openminemap.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.waypoint.WaypointScreen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ColorUtil {

    private static int pathInt = 0;

    /// Input values range 0-255
    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /// Returns as \[alpha, red, green, blue\] ranging 0-255
    public static int[] decompose(int argb) {
        return new int[] {
                (argb & 0xFF000000) >>> 24,
                (argb & 0x00FF0000) >>> 16,
                (argb & 0x0000FF00) >>> 8,
                argb & 0x000000FF
        };
    }

    ///Ranges from 0 (no change) to 1 (full white). Alpha is not affected.
    public static int lighten(int argb, float percent) {
        int[] channels = decompose(argb);
        percent = Math.clamp(percent, 0, 1);
        return argb(
            channels[0],
                (channels[1] + ((int) ((255 - channels[1]) * percent))),
                (channels[2] + ((int) ((255 - channels[2]) * percent))),
                (channels[3] + ((int) ((255 - channels[3]) * percent)))
        );
    }

    ///Ranges from 0 (no change) to 1 (full black). Alpha is not affected.
    public static int darken(int argb, double percent) {
        int[] channels = decompose(argb);
        percent = -1 * (Math.clamp(percent, 0, 1) - 1);
        return argb(
                channels[0],
                ((int) (channels[1] * percent)),
                ((int) (channels[2] * percent)),
                ((int) (channels[3] * percent))
        );
    }

    /// Alpha should be a value in the range of 0-255
    public static int setAlpha(int alpha, int argb) {
        alpha = Math.clamp(alpha, 0, 255);
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }

    public static int average(int argb1, int argb2) {
        int[] color1 = decompose(argb1);
        int[] color2 = decompose(argb2);

        return argb(
                (color1[0] + color2[0]) / 2,
                (color1[1] + color2[1]) / 2,
                (color1[2] + color2[2]) / 2,
                (color1[3] + color2[3]) / 2
        );

    }

    /// Same as ::hsl
    /// Alpha: 0-255
    /// Hue: 0-360
    /// Saturation: 0-1
    /// Brightness: 0-1
    public static int hsb(int alpha, int hue, float saturation, float brightness) {
        return setAlpha(alpha, Color.HSBtoRGB((float) hue / 360, saturation, brightness));
    }

    /// Same as ::hsb
    /// Alpha: 0-255
    /// Hue: 0-360
    /// Saturation: 0-1
    /// Brightness: 0-1
    public static int hsl(int alpha, int hue, float saturation, float brightness) {
        return hsb(alpha, hue, saturation, brightness);
    }

    /// Alpha: 0-255
    /// Hue: 0-360
    /// Saturation: 0-1
    /// Value: 0-1
    public static int hsv(int alpha, int hue, float saturation, float value) {
        float light = value * (1 - saturation / 2);
        return hsb(
                alpha,
                hue,
                light % 1 == 0 ? 0 : (value - light) / Math.min(light, 1 - light),
                light
        );
    }

    public static int getCurrentRainbowColor() {
        return hsl(255, (int) ((Util.getEpochTimeMs() >>> 6) % 360), 0.95f, 0.75f);
    }

    public static Identifier getColoredIdentifier(Identifier identifier, int colorHSV) {
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

            Identifier wayIdent = Identifier.of("openminemap", "recolored-texture-"+ pathInt);
            client.getTextureManager().registerTexture(wayIdent, new NativeImageBackedTexture(nImage));
            pathInt++;

            is.close();
            //nImage.close();
            os.close();

            return wayIdent;

        } catch (IOException | IllegalArgumentException e) {
            return identifier;
        }

    }

}
