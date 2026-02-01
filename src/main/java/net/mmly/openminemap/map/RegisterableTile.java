package net.mmly.openminemap.map;

import java.io.InputStream;

public class RegisterableTile {

    InputStream image;
    String key;
    String cacheName;

    RegisterableTile(InputStream image, String key, String cacheName) {
        this.image = image;
        this.key = key;
        this.cacheName = cacheName;
    }

}
