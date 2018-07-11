package io.github.phantamanta44.pcrossbow.tile.base;

import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.tile.L9TileEntity;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.IVectorDirectional;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class TileFreeRotatingOptics extends L9TileEntity implements ILaserConsumer, IVectorDirectional {

    @AutoSerialize
    private final IDatum<Vec3d> norm;

    public TileFreeRotatingOptics() {
        this.norm = IDatum.of(LinAlUtils.Y_POS);
        markRequiresSync();
    }

    @Override
    protected ICapabilityProvider initCapabilities() {
        return new CapabilityBroker()
                .with(XbowCaps.LASER_CONSUMER, this)
                .with(XbowCaps.VECTOR_DIR, this);
    }

    @Override
    public Vec3d getNorm() {
        return norm.get();
    }

    @Override
    public void setNorm(Vec3d dir) {
        norm.set(dir);
        setDirty();
    }

    @Override
    public boolean canConsumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        return dir.dotProduct(getNorm()) != 0;
    }

    @Override
    public Vec3d getBeamEndpoint(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        Vec3d cast = LinAlUtils.castOntoPlane(pos, dir, WorldUtils.getBlockCenter(getPos()), getNorm());
        return cast != null ? cast : pos;
    }

}
