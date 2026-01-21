package net.mmly.openminemap.map;

import java.io.InputStream;

public class RegisterableTile {

    InputStream image;
    String key;

    RegisterableTile(InputStream image, String key) {
        this.image = image;
        this.key = key;
    }

}
