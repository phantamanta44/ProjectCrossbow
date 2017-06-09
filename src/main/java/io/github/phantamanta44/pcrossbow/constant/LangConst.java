package io.github.phantamanta44.pcrossbow.constant;

import net.minecraft.util.StatCollector;

public class LangConst {

    public static final String CREATIVE_TAB = "tab_" + XbowConst.MOD_ID;

    public static final String BLOCK_LASER_NAME = "laser";

    public static final String INFO_KEY = XbowConst.MOD_ID + ".info.";
    public static final String INFO_ENERGY_STORED = INFO_KEY + "energy";

    public static String get(String key, Object... args) {
        return String.format(get(key), args);
    }

    public static String get(String key) {
        return StatCollector.translateToLocal(key);
    }

}
