package io.github.phantamanta44.pcrossbow.item.base;

import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemBlockPersistentState extends ItemBlockWithMetadataAndName {

    public ItemBlockPersistentState(Block block) {
        super(block);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (placed) {
            TileEntity tile = world.getTileEntity(x, y, z);
            tile.readFromNBT(getStoredBlockState(stack));
            tile.xCoord = x;
            tile.yCoord = y;
            tile.zCoord = z;
        }
        return placed;
    }

    public NBTTagCompound getStoredBlockState(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
            stack.setTagCompound(tag = new NBTTagCompound());
        if (!tag.hasKey(NBTConst.ITEM_BLOCK_STATE)) {
            NBTTagCompound state = new NBTTagCompound();
            tag.setTag(NBTConst.ITEM_BLOCK_STATE, state);
            ((ITileEntityProvider)field_150939_a).createNewTileEntity(null, stack.getItemDamage()).writeToNBT(state); // TODO Get a hold of the world somehow?
            return state;
        }
        else
            return tag.getCompoundTag(NBTConst.ITEM_BLOCK_STATE);
    }

}