package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import io.github.phantamanta44.pcrossbow.item.base.ItemBlockPersistentDevice;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemBlockLaser extends ItemBlockPersistentDevice {
    
    public ItemBlockLaser(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
            int direction = BlockPistonBase.determineOrientation(world, x, y, z, player);
            ((TileLaser)world.getTileEntity(x, y, z)).setFacing(direction);
            return true;
        } else {
            return false;
        }
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
