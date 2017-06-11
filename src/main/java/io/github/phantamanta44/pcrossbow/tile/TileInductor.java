package io.github.phantamanta44.pcrossbow.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import io.github.phantamanta44.pcrossbow.api.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.client.handler.ClientTickHandler;
import io.github.phantamanta44.pcrossbow.tile.base.TileMod;
import io.github.phantamanta44.pcrossbow.util.EnergyHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class TileInductor extends TileMod implements IEnergyProvider, ILaserConsumer {

    public static final int MAX_ENERGY = 400000;

    private EnergyStorage energy;

    public TileInductor() {
        energy = new EnergyStorage(MAX_ENERGY); // TODO Use our own impl?
        init = true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    public void consumeBeam(Vec3 direction, float power, float radius) {
        energy.receiveEnergy(80, false);
    }

    @Override
    protected void tick() {
        if (energy.getEnergyStored() > 0)
            EnergyHelper.distributeAdj(worldObj, xCoord, yCoord, zCoord, Math.min(energy.getEnergyStored(), 24000));
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int qty, boolean simulate) {
        return energy.extractEnergy(qty, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return energy.getMaxEnergyStored();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        energy.writeToNBT(tag);
    }

}
