package io.github.phantamanta44.pcrossbow.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.tileentity.IReconfigurableFacing;
import io.github.phantamanta44.pcrossbow.ProjectCrossbow;
import io.github.phantamanta44.pcrossbow.client.sound.ContinuousSound;
import io.github.phantamanta44.pcrossbow.client.sound.SingleSound;
import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.tile.base.TileBasicInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileLaser extends TileBasicInventory implements IReconfigurableFacing, IEnergyReceiver {

    public static final int MAX_ENERGY = 400000;

    private int rotation;
    private EnergyStorage energy;
    private long lastLasingTime = 0;
    private ContinuousSound sound = null;

    public TileLaser(int size) {
        super(size);
        energy = new EnergyStorage(MAX_ENERGY); // TODO Use our own impl?
        init = true;
        rotation = 2;
    }

    @Override
    protected void tick() {
        if (energy.getEnergyStored() > 80 && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
            if (energy.extractEnergy(80, true) == 80) {
                ForgeDirection facing = ForgeDirection.getOrientation(rotation);
                ProjectCrossbow.PROXY.doLasing(
                        worldObj, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F,
                        Vec3.createVectorHelper(facing.offsetX, facing.offsetY, facing.offsetZ), 8F, 1F, 0.1F);
                if (worldObj.isRemote) {
                    long currentTick = worldObj.getTotalWorldTime();
                    if (currentTick - lastLasingTime > 30) {
                        Minecraft.getMinecraft().getSoundHandler().playSound(new SingleSound(
                                ResConst.SOUND_LASER_STARTUP[getBlockMetadata()],
                                0.6F, 1F, xCoord, yCoord, zCoord));
                    }
                    if (sound == null || sound.isDonePlaying()) {
                        sound = new ContinuousSound(
                                ResConst.SOUND_LASER_LASING[getBlockMetadata()],
                                0.3F, 1F, xCoord, yCoord, zCoord, 4);
                        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
                    } else {
                        sound.refresh();
                    }
                    lastLasingTime = currentTick;
                }
            }
        }
        markForUpdate();
    }

    @Override
    public int getFacing() {
        return rotation;
    }

    @Override
    public boolean allowYAxisFacing() {
        return true;
    }

    @Override
    public boolean rotateBlock() {
        return false;
    }

    @Override
    public boolean setFacing(int side) {
        if (side > 5 || side < 0)
            return false;
        rotation = side;
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.readFromNBT(tag);
        rotation = tag.getInteger(NBTConst.ROTATION);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energy.writeToNBT(tag);
        tag.setInteger(NBTConst.ROTATION, rotation);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from.ordinal() != rotation;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int qty, boolean simulate) {
        return energy.receiveEnergy(qty, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return energy.getMaxEnergyStored();
    }

    public static class Test extends TileLaser {

        public Test() {
            super(0);
        }

    }

}
