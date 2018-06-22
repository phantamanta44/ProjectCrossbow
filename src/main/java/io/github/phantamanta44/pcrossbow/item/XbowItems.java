package io.github.phantamanta44.pcrossbow.item;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.pcrossbow.Xbow;

public class XbowItems {

    public static void init() {
        LibNine.PROXY.getRegistrar().begin(Xbow.INSTANCE);
        // TODO
        LibNine.PROXY.getRegistrar().end();
    }

}
