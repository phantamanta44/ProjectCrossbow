package io.github.phantamanta44.pcrossbow.block.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDismantleable {

    default boolean canDismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
        return true;
    }

    void dismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face);

}
