package net.mmly.openminemap.util;

public class ColorUtil {

    /// Input values range 0-255
    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /// Returns as \[alpha, red, green, blue\]
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

}
