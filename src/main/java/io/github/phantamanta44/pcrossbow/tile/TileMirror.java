package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.tile.L9TileEntity;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.WorldBlockPos;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.helper.ByteUtils;
import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

@RegisterTile(XbowConst.MOD_ID)
public class TileMirror extends L9TileEntity implements ILaserConsumer {

    @AutoSerialize
    private final IDatum<Vec3d> norm;

    public TileMirror() {
        this.norm = IDatum.of(LinAlUtils.Y_POS);
        markRequiresSync();
    }

    @Override
    protected ICapabilityProvider initCapabilities() {
        return new CapabilityBroker().with(XbowCaps.LASER_CONSUMER, this);
    }

    public Vec3d getNorm() {
        return norm.get();
    }

    public void setNorm(Vec3d dir) {
        norm.set(dir);
        setDirty();
    }

    @Override
    public void consumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        Xbow.PROXY.doLasing(world, pos, LinAlUtils.reflect2D(dir.scale(-1D), getNorm()),
                power, radius, fluxAngle, new WorldBlockPos(world, this.pos));
    }

    @Override
    public void deserializeBytes(ByteUtils.Reader data) {
        super.deserializeBytes(data);
        System.out.println(getNorm());
    }

}
