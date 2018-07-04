package io.github.phantamanta44.pcrossbow.block.base;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import io.github.phantamanta44.pcrossbow.item.base.ItemBlockPersistentState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class BlockPersistentState extends L9BlockStated implements IDismantleable {

    public BlockPersistentState(String name, Material material) {
        super(name, material);
    }

    @Override
    protected L9ItemBlock initItemBlock() {
        return new ItemBlockPersistentState(this);
    }

    public ItemStack getPersistingItemStack(IBlockAccess world, BlockPos pos, IBlockState state) {
        ItemStack stack = new ItemStack(this, 1, getMetaFromState(state));
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
            NBTTagCompound itemBlockStateTag = new NBTTagCompound();
            tile.writeToNBT(itemBlockStateTag);
            stack.getTagCompound().setTag(NBTConst.ITEM_BLOCK_STATE, itemBlockStateTag);
        }
        return stack;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getPersistingItemStack(world, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null) {
            drops.add(getPersistingItemStack(world, pos, state));
        } else {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, te, stack);
        world.setBlockToAir(pos);
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
        WorldUtils.dropItem(world, pos, getPersistingItemStack(world, pos, state));
    }

}
