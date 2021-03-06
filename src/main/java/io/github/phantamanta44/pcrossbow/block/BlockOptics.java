package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.block.state.IBlockModelMapper;
import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.ImpossibilityRealizedException;
import io.github.phantamanta44.libnine.util.collection.Accrue;
import io.github.phantamanta44.pcrossbow.block.base.IDismantleable;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.client.render.tesr.TESRFreeRotating;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockOptics;
import io.github.phantamanta44.pcrossbow.tile.TileMirror;
import io.github.phantamanta44.pcrossbow.tile.TileOneWay;
import io.github.phantamanta44.pcrossbow.tile.TileSplitter;
import io.github.phantamanta44.pcrossbow.tile.base.TileFreeRotatingOptics;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.EnumSet;
import java.util.function.Supplier;

public class BlockOptics extends L9BlockStated implements IDismantleable {

    private static final AxisAlignedBB BB_ROTATING_OPTICS = new AxisAlignedBB(0.125D, 0.125D, 0.125D, 0.875D, 0.875D, 0.875D);

    public BlockOptics() {
        super(LangConst.BLOCK_OPTICS_NAME, Material.GLASS);
        setHardness(1F);
        setResistance(4F);
        setSoundType(SoundType.GLASS);
        setTileFactory((w, m) -> getStates().get(m).get(XbowProps.OPTICS_TYPE).createTileEntity());
    }

    @Override
    protected L9ItemBlock initItemBlock() {
        return new ItemBlockOptics(this);
    }

    @Override
    protected void accrueProperties(Accrue<IProperty<?>> props) {
        props.accept(XbowProps.OPTICS_TYPE);
    }

    @Override
    protected void initModel() {
        super.initModel();
        LibNine.PROXY.getRegistrar().queueBlockStateMapperReg(this,
                IBlockModelMapper.toModel(s -> s.getValue(XbowProps.OPTICS_TYPE).getModelName()));
        LibNine.PROXY.getRegistrar().queueTESRReg(TileMirror.class, new TESRFreeRotating<>(ResConst.MODEL_MIRROR, false));
        LibNine.PROXY.getRegistrar().queueTESRReg(TileSplitter.class, new TESRFreeRotating<>(ResConst.MODEL_SPLITTER, true));
        LibNine.PROXY.getRegistrar().queueTESRReg(TileOneWay.class, new TESRFreeRotating<>(ResConst.MODEL_ONE_WAY, true));
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        Type type = state.getValue(XbowProps.OPTICS_TYPE);
        if (type.isRotatingOptics()) return EnumBlockRenderType.INVISIBLE;
        throw new ImpossibilityRealizedException();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return state.getValue(XbowProps.OPTICS_TYPE).isRotatingOptics();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing face, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) return false;
        Type type = state.getValue(XbowProps.OPTICS_TYPE);
        if (type.isRotatingOptics()) {
            TileFreeRotatingOptics tile = getTileEntity(world, pos);
            if (tile != null) {
                if (!world.isRemote) tile.setNorm(player.getLookVec().scale(-1D));
                return true;
            }
        }
        return false;
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
        dropBlockAsItem(world, pos, state, 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Type type = state.getValue(XbowProps.OPTICS_TYPE);
        if (type.isRotatingOptics()) return BB_ROTATING_OPTICS;
        return FULL_BLOCK_AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return state.getValue(XbowProps.OPTICS_TYPE).canRenderInPass(layer);
    }

    public enum Type implements IStringSerializable {

        MIRROR("mirror", true, TileMirror::new,
                BlockRenderLayer.SOLID),
        SPLITTER("splitter", true, TileSplitter::new,
                BlockRenderLayer.SOLID, BlockRenderLayer.TRANSLUCENT),
        ONE_WAY("one_way", true, TileOneWay::new,
                BlockRenderLayer.SOLID, BlockRenderLayer.TRANSLUCENT);

        private final String serializableName;
        private final boolean rotatingOptics;
        private final Supplier<? extends TileEntity> tileFactory;
        private final EnumSet<BlockRenderLayer> passes;

        Type(String serializableName, boolean rotatingOptics, Supplier<? extends TileEntity> tileFactory,
             BlockRenderLayer... passes) {
            this.serializableName = serializableName;
            this.rotatingOptics = rotatingOptics;
            this.tileFactory = tileFactory;
            this.passes = EnumSet.noneOf(BlockRenderLayer.class);
            Collections.addAll(this.passes, passes);
        }

        public String getModelName() {
            return serializableName;
        }

        @Override
        public String getName() {
            return serializableName;
        }

        public boolean isRotatingOptics() {
            return rotatingOptics;
        }

        public TileEntity createTileEntity() {
            return tileFactory.get();
        }

        public boolean canRenderInPass(BlockRenderLayer pass) {
            return passes.contains(pass);
        }

    }

}
