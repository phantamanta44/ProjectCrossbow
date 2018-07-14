package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.libnine.client.model.ParameterizedItemModel;
import io.github.phantamanta44.libnine.item.L9ItemBlockStated;
import io.github.phantamanta44.pcrossbow.block.BlockMachine;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.tile.base.TileSimpleProcessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockMachine extends L9ItemBlockStated implements ParameterizedItemModel.IParamaterized {

    public ItemBlockMachine(BlockMachine block) {
        super(block);
    }

    @Override
    public void getModelMutations(ItemStack stack, ParameterizedItemModel.Mutation m) {
        m.mutate("type", getBlock().getStateFromMeta(stack.getMetadata()).getValue(XbowProps.MACHINE_TYPE).getName());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) return false;
        ((TileSimpleProcessor)world.getTileEntity(pos)).setDirection(player.getHorizontalFacing().getOpposite());
        return true;
    }

}
