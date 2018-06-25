package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.capability.L9AspectEnergy;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.component.IntReservoir;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.util.EnergyUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

@RegisterTile(XbowConst.MOD_ID)
public class TileInductor extends L9TileEntityTicking implements ILaserConsumer {

    public static final int MAX_ENERGY = 400000;

    @AutoSerialize
    private final IntReservoir energy;

    private long inductionTime;

    public TileInductor() {
        this.energy = new IntReservoir(MAX_ENERGY);
        this.inductionTime = 0;
        energy.onQuantityChange((o, n) -> setDirty());
        markRequiresSync();
        setInitialized();
    }

    @Override
    protected ICapabilityProvider initCapabilities() {
        return new CapabilityBroker()
                .with(CapabilityEnergy.ENERGY, new L9AspectEnergy(energy))
                .with(XbowCaps.LASER_CONSUMER, this);
    }

    public long getInductionTime() {
        return inductionTime;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public void consumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        if (world.isRemote) {
            inductionTime = world.getTotalWorldTime();
        } else {
            energy.offer(80, true);
        }
    }

    @Override
    protected void tick() {
        if (!world.isRemote) {
            if (energy.getQuantity() > 0) {
                energy.draw(EnergyUtils.distributeAdj(world, pos, energy.draw(24000, false)), true);
                setDirty();
            }
        }
    }

}
