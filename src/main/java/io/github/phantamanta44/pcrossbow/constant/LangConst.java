package io.github.phantamanta44.pcrossbow.constant;

import io.github.phantamanta44.pcrossbow.util.Cardinal;
import net.minecraft.util.text.translation.I18n;

public class LangConst {

    public static final String BLOCK_LASER_NAME = "laser";
    public static final String BLOCK_INDUCTOR_NAME = "inductor";
    public static final String BLOCK_OPTICS_NAME = "optics";

    public static final String ITEM_WRENCH_NAME = "wrench";

    public static final String GUI_VECTOR_WRENCH = "vectorwrench";

    public static final String INFO_KEY = XbowConst.MOD_ID + ".info.";
    public static final String INFO_ENERGY_STORED = INFO_KEY + "energy";

    public static final String INFO_CARDINAL_KEY = INFO_KEY + "cardinal.";

    public static String getCardinalName(Cardinal dir) {
        return INFO_CARDINAL_KEY + dir.name().toLowerCase();
    }

    public static String get(String key, Object... args) {
        return String.format(get(key), args);
    }

    public static String get(String key) {
        return I18n.translateToLocal(key);
    }

}
