package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.libnine.block.state.VirtualState;
import io.github.phantamanta44.libnine.util.helper.WorldUtils;
import io.github.phantamanta44.pcrossbow.block.BlockLaser;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockLaser extends ItemBlockPowered {
    
    public ItemBlockLaser(BlockLaser block) {
        super(block, TileLaser.MAX_ENERGY);
    }

    @Override
    public String getModelName(VirtualState state) {
        return state.get(XbowProps.LASER_TYPE).getItemModel();
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) return false;
        ((TileLaser)WorldUtils.getTileSafely(world, pos))
                .setDirection(EnumFacing.getDirectionFromEntityLiving(pos, player));
        return true;
    }
    
}
