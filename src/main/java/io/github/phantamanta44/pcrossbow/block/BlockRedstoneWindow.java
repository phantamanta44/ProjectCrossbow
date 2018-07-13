package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.block.state.IBlockModelMapper;
import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.Accrue;
import io.github.phantamanta44.libnine.util.function.IBoolUnaryOperator;
import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.LasingResult;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.block.base.IDismantleable;
import io.github.phantamanta44.pcrossbow.block.base.ILaserBlock;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockRedstoneWindow;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockRedstoneWindow extends L9BlockStated implements IDismantleable, ILaserBlock {

    public BlockRedstoneWindow() {
        super(LangConst.BLOCK_REDSTONE_WINDOW_NAME, Material.REDSTONE_LIGHT);
        setHardness(0.35F);
        setSoundType(SoundType.GLASS);
    }

    @Override
    protected L9ItemBlock initItemBlock() {
        return new ItemBlockRedstoneWindow(this);
    }

    @Override
    protected void accrueProperties(Accrue<IProperty<?>> props) {
        props.acceptAll(XbowProps.REDSTONE_WINDOW_TYPE, XbowProps.ACTIVE);
    }

    @Override
    protected IBlockState initDefaultState(IBlockState state) {
        return state.withProperty(XbowProps.REDSTONE_WINDOW_TYPE, Type.DIRECT)
                .withProperty(XbowProps.ACTIVE, false);
    }

    @Override
    protected void initModel() {
        super.initModel();
        LibNine.PROXY.getRegistrar().queueBlockStateMapperReg(this,
                IBlockModelMapper.toVariant(getInternalName(), s -> isActive(s) ? "active" : "normal"));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, getMetaFromState(getDefaultState())));
        items.add(new ItemStack(this, 1, getMetaFromState(getDefaultState()
                .withProperty(XbowProps.REDSTONE_WINDOW_TYPE, Type.INVERTED))));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state.withProperty(XbowProps.ACTIVE, false));
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        updateState(world, pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        updateState(world, pos, state);
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
        dropBlockAsItem(world, pos, state, 0);
    }

    @Override
    public LasingResult getLasingResult(WorldBlockPos blockPos, Vec3d pos, Vec3d dir, EnumFacing face,
                                        double power, double radius, double fluxAngle) {
        IBlockState state = blockPos.getBlockState();
        if (isActive(state)) {
            Xbow.PROXY.doLasing(blockPos.getWorld(), pos, LinAlUtils.reflect2D(dir.scale(-1D), LinAlUtils.getDir(face)),
                    power, radius, fluxAngle, blockPos);
            return LasingResult.CONSUME;
        }
        return LasingResult.PASS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        if (isActive(state)) return layer == BlockRenderLayer.SOLID;
        return layer == BlockRenderLayer.TRANSLUCENT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightOpacity(IBlockState state) {
        return isActive(state) ? 255 : 0;
    }

    private static boolean isActive(IBlockState state) {
        return state.getValue(XbowProps.REDSTONE_WINDOW_TYPE).mapper.apply(state.getValue(XbowProps.ACTIVE));
    }

    private static void updateState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.withProperty(XbowProps.ACTIVE, world.isBlockIndirectlyGettingPowered(pos) > 0));
    }

    public enum Type implements IStringSerializable {

        DIRECT(o -> o),
        INVERTED(o -> !o);

        private final IBoolUnaryOperator mapper;

        Type(IBoolUnaryOperator mapper) {
            this.mapper = mapper;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public String getUnlocalizedName() {
            return XbowBlocks.redstoneWindow.getUnlocalizedName() + "_" + getName();
        }

    }

}
