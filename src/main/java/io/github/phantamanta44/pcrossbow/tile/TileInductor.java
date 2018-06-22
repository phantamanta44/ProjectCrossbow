package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.capability.L9AspectEnergy;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.component.IntReservoir;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.DataSerialization;
import io.github.phantamanta44.libnine.util.helper.ByteUtils;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.util.EnergyHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

@RegisterTile(XbowConst.MOD_ID)
public class TileInductor extends L9TileEntityTicking implements ILaserConsumer {

    public static final int MAX_ENERGY = 400000;

    private final CapabilityBroker capabilities;
    @AutoSerialize
    private final IntReservoir energy;
    private final DataSerialization serializer;

    public TileInductor() {
        this.capabilities = new CapabilityBroker();
        this.energy = new IntReservoir(MAX_ENERGY);
        this.serializer = new DataSerialization(this);
        capabilities.put(CapabilityEnergy.ENERGY, new L9AspectEnergy(energy));
        capabilities.put(XbowCaps.LASER_CONSUMER, this);
        energy.onQuantityChange((o, n) -> setDirty());
        markRequiresSync();
        setInitialized();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public void consumeBeam(Vec3d direction, float power, float radius) {
        energy.offer(80, false);
    }

    @Override
    protected void tick() {
        if (energy.getQuantity() > 0) {
            EnergyHelper.distributeAdj(world, pos, Math.min(energy.getQuantity(), 24000));
            setDirty();
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capabilities.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capabilities.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        serializer.deserializeNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        serializer.serializeNBT(tag);
        return tag;
    }

    @Override
    public void serializeBytes(ByteUtils.Writer data) {
        super.serializeBytes(data);
        serializer.serializeBytes(data);
    }

    @Override
    public void deserializeBytes(ByteUtils.Reader data) {
        super.deserializeBytes(data);
        serializer.deserializeBytes(data);
    }

}
