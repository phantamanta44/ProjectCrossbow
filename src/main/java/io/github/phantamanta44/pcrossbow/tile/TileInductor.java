package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.capability.impl.L9AspectEnergy;
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

    @AutoSerialize
    private final IntReservoir energy;

    public TileInductor() {
        this.energy = new IntReservoir(Integer.MAX_VALUE);
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

    public int getEnergyStored() {
        return energy.getQuantity();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public void consumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        if (!world.isRemote) {
            energy.offer(Math.max((int)Math.floor(power * (1 - Math.pow(2 * radius, 3))), 0), true);
            world.notifyNeighborsOfStateChange(this.pos, blockType, false);
        }
    }

    @Override
    protected void tick() {
        if (!world.isRemote) {
            if (energy.getQuantity() > 0) {
                energy.draw(EnergyUtils.distributeAdj(world, pos, energy.getQuantity()), true);
                if (energy.getQuantity() <= 240) {
                    energy.setQuantity(0);
                    world.notifyNeighborsOfStateChange(pos, blockType, false);
                } else {
                    energy.setQuantity((int)Math.ceil(energy.getQuantity() / 3F));
                }
                setDirty();
            }
        }
    }

}
