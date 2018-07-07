package io.github.phantamanta44.pcrossbow.item;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.constant.LangConst;

public class XbowItems {

    public static ItemWrench wrench;
    public static ItemBeamModifier modPower;
    public static ItemBeamModifier modRadius;
    public static ItemBeamModifier modFluxAngle;

    public static void init() {
        LibNine.PROXY.getRegistrar().begin(Xbow.INSTANCE);
        wrench = new ItemWrench();
        modPower = new ItemBeamModifier(LangConst.ITEM_MOD_POWER_NAME, ItemBeamModifier.Type.POWER);
        modRadius = new ItemBeamModifier(LangConst.ITEM_MOD_RADIUS_NAME, ItemBeamModifier.Type.RADIUS);
        modFluxAngle = new ItemBeamModifier(LangConst.ITEM_MOD_FLUX_ANGLE_NAME, ItemBeamModifier.Type.FLUX_ANGLE);
        LibNine.PROXY.getRegistrar().end();
    }

}
