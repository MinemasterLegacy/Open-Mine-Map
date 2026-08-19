package net.mmly.openminemap.util;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class Notification {
    public Component text;
    private double expirationTime;

    public Notification(Component text, double durationMs) {
        this.text = text;
        this.expirationTime = Util.getMillis() + durationMs;
    }

    public Notification(Component text) {
        this(text, 3000);
    }

    public int timeToExpirationMs() {
        return (int) (expirationTime - Util.getMillis());
    }

}
