package net.mmly.openminemap.util;

import java.util.function.Supplier;

public class nameSupplier implements Supplier<String> {
    @Override
    public String get() {
        return "osmTileName";
    }
}