package io.github.phantamanta44.pcrossbow.item;

import io.github.phantamanta44.libnine.capability.provider.CapabilityBrokerLazy;
import io.github.phantamanta44.libnine.client.model.ParameterizedItemModel;
import io.github.phantamanta44.libnine.item.L9ItemSubs;
import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.libnine.util.nbt.NBTUtils;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.api.capability.IVectorDirectional;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.base.IDismantleable;
import io.github.phantamanta44.pcrossbow.client.gui.XbowGuis;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWrench extends L9ItemSubs implements ParameterizedItemModel.IParamaterized {

    public ItemWrench() {
        super(LangConst.ITEM_WRENCH_NAME, Type.values().length);
        setMaxStackSize(1);
    }

    @Override
    public void getModelMutations(ItemStack stack, ParameterizedItemModel.Mutation m) {
        m.mutate("type", Type.values()[stack.getMetadata()].getMutation());
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return Type.values()[stack.getMetadata()].getCapabilities(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        Type.values()[stack.getMetadata()].addInformation(stack, tooltip);
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
        },
        CLONE {
            @Override
            public boolean doWrenching(EntityPlayer player, World world, BlockPos pos, EnumFacing face, EnumHand hand) {
                TileEntity tile = world.getTileEntity(pos);
                if (player.isSneaking()) {
                    if (tile != null && tile.hasCapability(XbowCaps.VECTOR_DIR, null)) {
                        if (world.isRemote) player.sendMessage(new TextComponentTranslation(LangConst.MSG_CLONE_COPY));
                        player.getHeldItem(hand).getCapability(XbowCaps.VECTOR_DIR, null).setNorm(
                                tile.getCapability(XbowCaps.VECTOR_DIR, null).getNorm());
                        return true;
                    }
                } else if (tile != null && tile.hasCapability(XbowCaps.VECTOR_DIR, null)) {
                    if (world.isRemote) {
                        player.sendMessage(new TextComponentTranslation(LangConst.MSG_CLONE_PASTE));
                    } else {
                        tile.getCapability(XbowCaps.VECTOR_DIR, null).setNorm(
                                player.getHeldItem(hand).getCapability(XbowCaps.VECTOR_DIR, null).getNorm());
                    }
                    return true;
                }
                return false;
            }

            @Nullable
            @Override
            public ICapabilityProvider getCapabilities(ItemStack stack) {
                return new CapabilityBrokerLazy(c -> {
                    if (c == XbowCaps.VECTOR_DIR) return new VectorAspect(stack);
                    return null;
                });
            }

            @Override
            public void addInformation(ItemStack stack, List<String> tooltip) {
                Vec3d vec = stack.getCapability(XbowCaps.VECTOR_DIR, null).getNorm();
                tooltip.add(TextFormatting.GRAY + String.format("(%.2f, %.2f, %.2f)", vec.x, vec.y, vec.z));
            }

            class VectorAspect implements IVectorDirectional {

                private final ItemStack stack;
                private Vec3d norm;

                VectorAspect(ItemStack stack) {
                    this.stack = stack;
                    if (stack.hasTagCompound()) {
                        NBTTagCompound nbt = stack.getTagCompound();
                        if (nbt.hasKey(NBTConst.DIRECTION)) {
                            this.norm = NBTUtils.deserializeVec3d(nbt.getCompoundTag(NBTConst.DIRECTION));
                        } else {
                            this.norm = LinAlUtils.Y_POS;
                        }
                    } else {
                        stack.setTagCompound(new NBTTagCompound());
                        this.norm = LinAlUtils.Y_POS;
                    }
                }

                @Override
                public Vec3d getNorm() {
                    return norm;
                }

                @Override
                public void setNorm(Vec3d dir) {
                    norm = dir;
                    stack.getTagCompound().setTag(NBTConst.DIRECTION, NBTUtils.serializeVec3d(norm));
                }

            }
        };

        public abstract boolean doWrenching(EntityPlayer player, World world, BlockPos pos, EnumFacing face, EnumHand hand);

        @Nullable
        public ICapabilityProvider getCapabilities(ItemStack stack) {
            return null;
        }

        public void addInformation(ItemStack stack, List<String> tooltip) {
            // NO-OP
        }

        public String getMutation() {
            return name().toLowerCase();
        }

    }

}
