package net.mmly.openminemap.util;

public class ColorUtil {

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int[] decompose(int argb) {
        return new int[] {
                (argb & 0xFF000000) >> 24,
                (argb & 0x00FF0000) >> 16,
                (argb & 0x0000FF00) >> 8,
                argb & 0x000000FF
        };
    }

    ///Ranges from 0 (no change) to 1 (full white). Alpha is not affected.
    public static int lighten(int argb, float percent) {
        int[] channels = decompose(argb);
        percent = Math.clamp(percent, 0, 1);
        return argb(
            channels[0] << 24,
                (channels[1] + ((int) ((255 - channels[1]) * percent))) << 16,
                (channels[2] + ((int) ((255 - channels[2]) * percent))) << 8,
                (channels[3] + ((int) ((255 - channels[3]) * percent)))
        );
    }

    ///Ranges from 0 (no change) to 1 (full black). Alpha is not affected.
    public static int darken(int argb, double percent) {
        int[] channels = decompose(argb);
        percent = -1 * (Math.clamp(percent, 0, 1) - 1);
        return argb(
                channels[0] << 24,
                ((int) (channels[1] * percent)) << 16,
                ((int) (channels[2] * percent)) << 8,
                ((int) (channels[3] * percent))
        );
    }

    public static int setAlpha(int alpha, int argb) {
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }

}
