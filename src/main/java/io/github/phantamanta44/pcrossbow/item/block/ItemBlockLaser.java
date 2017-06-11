package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockLaser extends ItemBlockPowered {
    
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
    
}
