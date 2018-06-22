package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.capability.L9AspectEnergy;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.component.IntReservoir;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.DataSerialization;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.helper.ByteUtils;
import io.github.phantamanta44.libnine.util.helper.WorldUtils;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.client.sound.ContinuousSound;
import io.github.phantamanta44.pcrossbow.client.sound.SingleSound;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

@RegisterTile(XbowConst.MOD_ID)
public abstract class TileLaser extends L9TileEntityTicking {

    public static final int MAX_ENERGY = 400000;

    private final CapabilityBroker capabilities;
    @AutoSerialize
    private final IntReservoir energy;
    @AutoSerialize
    private final IDatum<EnumFacing> rotation;
    private final DataSerialization serializer;

    private long lastLasingTime = 0;
    private ContinuousSound sound = null;

    public TileLaser(int size) {
        this.capabilities = new CapabilityBroker();
        this.energy = new IntReservoir(MAX_ENERGY);
        this.rotation = IDatum.of(EnumFacing.NORTH);
        this.serializer = new DataSerialization(this);
        capabilities.put(CapabilityEnergy.ENERGY, new L9AspectEnergy(energy));
        energy.onQuantityChange((o, n) -> setDirty());
        markRequiresSync();
        setInitialized();
    }

    public void setDirection(EnumFacing dir) {
        rotation.set(dir);
        setDirty();
    }

    @Override
    protected void tick() {
        if (energy.getQuantity() > 80 && world.isBlockIndirectlyGettingPowered(pos) > 0) {
            if (energy.draw(80, true) == 80) {
                Xbow.PROXY.doLasing(world, WorldUtils.getBlockCenter(pos), new Vec3d(rotation.get().getDirectionVec()),
                        50F, 4F, 0.0025F);
                if (world.isRemote) {
                    long currentTick = world.getTotalWorldTime();
                    if (currentTick - lastLasingTime > 30) {
                        Minecraft.getMinecraft().getSoundHandler().playSound(new SingleSound(
                                ResConst.SOUND_LASER_STARTUP[getBlockMetadata()],
                                0.6F, 1F, pos, SoundCategory.BLOCKS));
                    }
                    if (sound == null || sound.isDonePlaying()) {
                        sound = new ContinuousSound(
                                ResConst.SOUND_LASER_LASING[getBlockMetadata()],
                                0.3F, 1F, pos, SoundCategory.BLOCKS, 4);
                        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
                    } else {
                        sound.refresh();
                    }
                    lastLasingTime = currentTick;
                }
                setDirty();
            }
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

    @RegisterTile(XbowConst.MOD_ID)
    public static class Test extends TileLaser {

        public Test() {
            super(0);
        }

    }

}
