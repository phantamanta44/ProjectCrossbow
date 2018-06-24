package io.github.phantamanta44.pcrossbow.item.base;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.item.L9ItemBlockStated;
import io.github.phantamanta44.libnine.util.helper.WorldUtils;
import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockPersistentState extends L9ItemBlockStated {

    public ItemBlockPersistentState(L9BlockStated block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                float hitX, float hitY, float hitZ, IBlockState newState) {
        if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            TileEntity tile = WorldUtils.getTileSafely(world, pos);
            tile.readFromNBT(getStoredBlockState(stack));
            tile.setPos(pos);
            return true;
        }
        return false;
    }

    public NBTTagCompound getStoredBlockState(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) stack.setTagCompound(tag = new NBTTagCompound());
        if (!tag.hasKey(NBTConst.ITEM_BLOCK_STATE)) {
            NBTTagCompound state = new NBTTagCompound();
            tag.setTag(NBTConst.ITEM_BLOCK_STATE, state);
            getBlock().createNewTileEntity(null, stack.getItemDamage()).writeToNBT(state);
            return state;
        } else {
            return tag.getCompoundTag(NBTConst.ITEM_BLOCK_STATE);
        }
    }

}