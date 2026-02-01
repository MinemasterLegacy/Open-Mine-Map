package net.mmly.openminemap.util;

import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class Notification {
    public Text text;
    public double expirationTime;

    public Notification(Text text, double durationMs) {
        this.text = text;
        this.expirationTime = Util.getMeasuringTimeMs() + durationMs;
    }

    public Notification(Text text) {
        this(text, 3000);
    }

}
