package io.github.phantamanta44.pcrossbow.constant;

import net.minecraft.util.text.translation.I18n;

public class LangConst {

    public static final String BLOCK_LASER_NAME = "laser";
    public static final String BLOCK_INDUCTOR_NAME = "inductor";
    public static final String BLOCK_OPTICS_NAME = "optics";

    public static final String INFO_KEY = XbowConst.MOD_ID + ".info.";
    public static final String INFO_ENERGY_STORED = INFO_KEY + "energy";

    public static String get(String key, Object... args) {
        return String.format(get(key), args);
    }

    public static String get(String key) {
        return I18n.translateToLocal(key);
    }

}
