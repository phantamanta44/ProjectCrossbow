package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.tile.base.TileFreeRotatingOptics;
import net.minecraft.util.math.Vec3d;

@RegisterTile(XbowConst.MOD_ID)
public class TileMirror extends TileFreeRotatingOptics {

    @Override
    public void consumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        Xbow.PROXY.doLasing(world, pos, LinAlUtils.reflect2D(dir.scale(-1D), getNorm()),
                power, radius, fluxAngle, getWorldPos());
    }

}
