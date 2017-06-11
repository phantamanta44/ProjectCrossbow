package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import io.github.phantamanta44.pcrossbow.item.base.ItemBlockPersistentDevice;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemBlockPowered extends ItemBlockPersistentDevice {

    public ItemBlockPowered(Block block) {
        super(block);
    }

    @Override
    public int receiveEnergy(ItemStack stack, int qty, boolean simulate) {
        NBTTagCompound state = getStoredBlockState(stack);
        int energy = state.getInteger(NBTConst.ENERGY);
        int toTransfer = Math.min(TileLaser.MAX_ENERGY - energy, qty);
        if (!simulate)
            state.setInteger(NBTConst.ENERGY, energy + toTransfer);
        return toTransfer;
    }

    @Override
    public int extractEnergy(ItemStack stack, int qty, boolean simulate) {
        NBTTagCompound state = getStoredBlockState(stack);
        int energy = state.getInteger(NBTConst.ENERGY);
        int toTransfer = Math.min(energy, qty);
        if (!simulate)
            state.setInteger(NBTConst.ENERGY, energy - toTransfer);
        return toTransfer;
    }

    @Override
    public int getEnergyStored(ItemStack stack) {
        return getStoredBlockState(stack).getInteger(NBTConst.ENERGY);
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        return TileLaser.MAX_ENERGY;
    }

}
