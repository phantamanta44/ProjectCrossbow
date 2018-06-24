package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.Accrue;
import io.github.phantamanta44.pcrossbow.block.base.BlockPersistentState;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockLaser;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockLaser extends BlockPersistentState {

    public BlockLaser() {
        super(LangConst.BLOCK_LASER_NAME, Material.IRON);
        setHardness(5F);
        setResistance(7.5F);
        setTileFactory((w, m) -> {
            switch (getStates().get(m).get(XbowProps.LASER_TYPE)) {
                case TIER_1:
                    break;
                case TIER_2:
                    break;
                case TIER_3:
                    break;
                case TIER_4:
                    break;
            }
            return new TileLaser.Test();
        });
    }

    @Override
    protected void accrueProperties(Accrue<IProperty<?>> props) {
        props.accept(XbowProps.LASER_TYPE);
    }

    @Override
    protected void accrueVolatileProperties(Accrue<IProperty<?>> props) {
        props.accept(XbowProps.ROTATION);
    }

    @Override
    protected L9ItemBlock initItemBlock() {
        return new ItemBlockLaser(this);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileLaser tile = getTileEntity(world, pos);
        return tile != null ? state.withProperty(XbowProps.ROTATION, tile.getDirection()) : state;
    }

    public enum Type implements IStringSerializable {

        TIER_1("tier_1"),
        TIER_2("tier_2"),
        TIER_3("tier_3"),
        TIER_4("tier_4");

        private final String serializableName;

        Type(String serializableName) {
            this.serializableName = serializableName;
        }

        public String getItemModel() {
            return "laser_" + serializableName;
        }

        @Override
        public String getName() {
            return serializableName;
        }

    }

}
