package io.github.phantamanta44.pcrossbow.constant;

import io.github.phantamanta44.pcrossbow.util.Cardinal;
import net.minecraft.util.text.translation.I18n;

public class LangConst {

    public static final String BLOCK_LASER_NAME = "laser";
    public static final String BLOCK_INDUCTOR_NAME = "inductor";
    public static final String BLOCK_OPTICS_NAME = "optics";
    public static final String BLOCK_SENSOR_NAME = "sensor";
    public static final String BLOCK_REDSTONE_WINDOW_NAME = "redstone_window";

    public static final String ITEM_WRENCH_NAME = "wrench";
    public static final String ITEM_MOD_POWER_NAME = "beam_mod_power";
    public static final String ITEM_MOD_RADIUS_NAME = "beam_mod_radius";
    public static final String ITEM_MOD_FLUX_ANGLE_NAME = "beam_mod_flux_angle";

    public static final String GUI_VECTOR_WRENCH = "vector_wrench";
    public static final String GUI_LASER = "laser";
    public static final String GUI_INDUCTOR = "inductor";

    private static final String INFO_KEY = XbowConst.MOD_ID + ".info.";
    public static final String INFO_FRACTION = INFO_KEY + "fraction";
    public static final String INFO_ENERGY_STORED = INFO_KEY + "energy";
    public static final String INFO_NOT_APPLICABLE = INFO_KEY + "not_applicable";

    public static final String INFO_POWER_NAME = INFO_KEY + "power_name";
    public static final String INFO_RADIUS_NAME = INFO_KEY + "radius_name";
    public static final String INFO_FLUX_ANGLE_NAME = INFO_KEY + "flux_angle_name";
    public static final String INFO_INTENSITY_NAME = INFO_KEY + "intensity_name";

    public static final String INFO_POWER = INFO_KEY + "power";
    public static final String INFO_RADIUS = INFO_KEY + "radius";
    public static final String INFO_FLUX_ANGLE = INFO_KEY + "flux_angle";
    
    public static final String INFO_BASE_POWER = INFO_KEY + "base_power";
    public static final String INFO_BASE_RADIUS = INFO_KEY + "base_radius";
    public static final String INFO_BASE_FLUX_ANGLE = INFO_KEY + "base_flux_angle";

    public static final String INFO_MOD_POWER = INFO_KEY + "mod_power";
    public static final String INFO_MOD_RADIUS = INFO_KEY + "mod_radius";
    public static final String INFO_MOD_FLUX_ANGLE = INFO_KEY + "mod_flux_angle";

    public static final String INFO_REDSTONE_IGNORED = INFO_KEY + "redstone_ignored";
    public static final String INFO_REDSTONE_DIRECT = INFO_KEY + "redstone_direct";
    public static final String INFO_REDSTONE_INVERTED = INFO_KEY + "redstone_inverted";

    private static final String INFO_CARDINAL_KEY = INFO_KEY + "cardinal.";

    public static String getCardinalName(Cardinal dir) {
        return INFO_CARDINAL_KEY + dir.name().toLowerCase();
    }

    private static final String MSG_KEY = XbowConst.MOD_ID + ".message.";
    public static final String MSG_CLONE_COPY = MSG_KEY + "clone_copy";
    public static final String MSG_CLONE_PASTE = MSG_KEY + "clone_paste";

    private static final String UNIT_KEY = XbowConst.MOD_ID + ".unit.";
    public static final String UNIT_ENERGY = UNIT_KEY + "energy";
    public static final String UNIT_TIME = UNIT_KEY + "time";
    public static final String UNIT_POWER= UNIT_KEY + "power";
    public static final String UNIT_DIST = UNIT_KEY + "distance";
    public static final String UNIT_ANGLE = UNIT_KEY + "angle";
    public static final String UNIT_INT = UNIT_KEY + "intensity";

    public static String get(String key, Object... args) {
        return String.format(get(key), args);
    }

    @SuppressWarnings("deprecation")
    public static String get(String key) {
        return I18n.translateToLocal(key);
    }

}
