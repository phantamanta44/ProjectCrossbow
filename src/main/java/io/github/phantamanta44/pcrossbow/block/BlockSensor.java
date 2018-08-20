package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.collection.Accrue;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.LasingResult;
import io.github.phantamanta44.pcrossbow.block.base.IDismantleable;
import io.github.phantamanta44.pcrossbow.block.base.ILaserBlock;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockSensor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSensor extends L9BlockStated implements IDismantleable, ILaserBlock {

    public BlockSensor() {
        super(LangConst.BLOCK_SENSOR_NAME, Material.REDSTONE_LIGHT);
        setHardness(0.35F);
        setSoundType(SoundType.GLASS);
    }

    @Override
    protected L9ItemBlock initItemBlock() {
        return new ItemBlockSensor(this);
    }

    @Override
    protected void accrueProperties(Accrue<IProperty<?>> props) {
        props.accept(XbowProps.ACTIVE);
    }

    @Override
    protected IBlockState initDefaultState(IBlockState state) {
        return state.withProperty(XbowProps.ACTIVE, false);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, getMetaFromState(getDefaultState())));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(getDefaultState());
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
        dropBlockAsItem(world, pos, state, 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getValue(XbowProps.ACTIVE) ? 15 : 0;
    }

    @Override
    public LasingResult getLasingResult(WorldBlockPos blockPos, Vec3d pos, Vec3d dir, EnumFacing face,
                                        double power, double radius, double fluxAngle) {
        if (!blockPos.getWorld().isRemote) {
            blockPos.getWorld().setBlockState(
                    blockPos.getPos(), blockPos.getBlockState().withProperty(XbowProps.ACTIVE, true));
        }
        return LasingResult.PASS;
    }

    @Override
    public void lasingFinished(WorldBlockPos pos) {
        if (!pos.getWorld().isRemote) {
            pos.getWorld().setBlockState(pos.getPos(), pos.getBlockState().withProperty(XbowProps.ACTIVE, false));
        }
    }

}
