package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import io.github.phantamanta44.pcrossbow.LasingResult;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.tile.base.TileFreeRotatingOptics;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

@RegisterTile(XbowConst.MOD_ID)
public class TileOneWay extends TileFreeRotatingOptics {

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public LasingResult consumeBeam(Vec3d pos, Vec3d dir, EnumFacing face, double power, double radius, double fluxAngle) {
        LasingResult check = checkBeam(pos, dir);
        if (check != LasingResult.CONSUME) return check;
        Xbow.PROXY.doLasing(world, pos, LinAlUtils.reflect2D(dir.scale(-1D), getNorm()),
                power, radius, fluxAngle, getWorldPos());
        return LasingResult.CONSUME;
    }

    @Override
    protected LasingResult checkBeam(Vec3d pos, Vec3d dir) {
        return (dir.dotProduct(getNorm()) < 0 || !WorldUtils.pointLiesInBlock(pos, getPos()))
                ? LasingResult.PASS : LasingResult.CONSUME;
    }

}
