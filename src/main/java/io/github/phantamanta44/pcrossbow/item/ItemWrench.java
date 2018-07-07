package io.github.phantamanta44.pcrossbow.item;

import io.github.phantamanta44.libnine.item.L9ItemSubs;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.base.IDismantleable;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.client.gui.XbowGuis;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemWrench extends L9ItemSubs {

    public ItemWrench() {
        super(LangConst.ITEM_WRENCH_NAME, Type.values().length);
        setMaxStackSize(1);
    }

    @Override
    protected String getModelName(int variant) {
        return Type.values()[variant].getModelName();
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing face,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        return Type.values()[player.getHeldItem(hand).getMetadata()].doWrenching(player, world, pos, face, hand)
                ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    public enum Type {

        OPTICAL {
            @Override
            public boolean doWrenching(EntityPlayer player, World world, BlockPos pos, EnumFacing face, EnumHand hand) {
                if (player.isSneaking()) {
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof IDismantleable) {
                        IDismantleable target = (IDismantleable)state.getBlock();
                        if (target.canDismantle(player, world, pos, state, face)) {
                            if (world.isRemote) return true;
                            target.dismantle(player, world, pos, state, face);
                            state.getBlock().breakBlock(world, pos, state);
                            world.setBlockToAir(pos);
                        }
                    }
                } else {
                    Block block = world.getBlockState(pos).getBlock();
                    if (block.getValidRotations(world, pos) != null) {
                        if (world.isRemote) return true;
                        block.rotateBlock(world, pos, face);
                        return true;
                    }
                }
                return false;
            }
        },
        VECTOR {
            @Override
            public boolean doWrenching(EntityPlayer player, World world, BlockPos pos, EnumFacing face, EnumHand hand) {
                if (player.isSneaking()) {
                    if (world.isRemote) {
                        IBlockState state = world.getBlockState(pos);
                        player.sendMessage(new TextComponentString(String.format("%s (%d, %d, %d)",
                                state.getBlock().getPickBlock(state, null, world, pos, player).getDisplayName(),
                                pos.getX(), pos.getY(), pos.getZ())));
                    }
                    return true;
                } else {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile != null && tile.hasCapability(XbowCaps.VECTOR_DIR, null)) {
                        if (world.isRemote) return true;
                        Xbow.INSTANCE.getGuiHandler().openGui(player, XbowGuis.vectorWrench, new WorldBlockPos(world, pos));
                        return true;
                    }
                }
                return false;
            }
        };

        public abstract boolean doWrenching(EntityPlayer player, World world, BlockPos pos, EnumFacing face, EnumHand hand);

        public String getModelName() {
            return "wrench_" + name().toLowerCase();
        }

    }

}
