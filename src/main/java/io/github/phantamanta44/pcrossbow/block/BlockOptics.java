package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.block.state.IBlockModelMapper;
import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.Accrue;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.client.render.TESRMirror;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockOptics;
import io.github.phantamanta44.pcrossbow.tile.TileMirror;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOptics extends L9BlockStated {

    private static final AxisAlignedBB BB_MIRROR = new AxisAlignedBB(0.125D, 0.125D, 0.125D, 0.875D, 0.875D, 0.875D);

    public BlockOptics() {
        super(LangConst.BLOCK_OPTICS_NAME, Material.GLASS);
        setHardness(1F);
        setResistance(4F);
        setTileFactory((w, m) -> {
            switch (getStates().get(m).get(XbowProps.OPTICS_TYPE)) {
                case MIRROR:
                    return new TileMirror();
            }
            throw new IllegalStateException("Invalid optics meta!");
        });
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
        LibNine.PROXY.getRegistrar().queueBlockStateMapperReg(this, IBlockModelMapper.toModel(s -> {
            switch ((Type)s.getProperties().get(XbowProps.OPTICS_TYPE)) {
                case MIRROR:
                    return "mirror";
            }
            throw new IllegalStateException("Invalid optics blockstate!");
        }));
        LibNine.PROXY.getRegistrar().queueTESRReg(TileMirror.class, new TESRMirror());
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        switch ((Type)state.getProperties().get(XbowProps.OPTICS_TYPE)) {
            case MIRROR:
                return EnumBlockRenderType.INVISIBLE;
        }
        throw new IllegalStateException("Invalid optics blockstate!");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing face, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) return false;
        Type type = (Type)state.getProperties().get(XbowProps.OPTICS_TYPE);
        if (type == Type.MIRROR) {
            TileMirror tile = getTileEntity(world, pos);
            if (tile != null) {
                if (!world.isRemote) tile.setNorm(player.getLookVec().scale(-1D));
                return true;
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Type type = (Type)state.getProperties().get(XbowProps.OPTICS_TYPE);
        if (type == Type.MIRROR) {
            return BB_MIRROR;
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public enum Type implements IStringSerializable {

        MIRROR("mirror");

        private final String serializableName;

        Type(String serializableName) {
            this.serializableName = serializableName;
        }

        public String getItemModel() {
            return serializableName;
        }

        @Override
        public String getName() {
            return serializableName;
        }

    }

}
