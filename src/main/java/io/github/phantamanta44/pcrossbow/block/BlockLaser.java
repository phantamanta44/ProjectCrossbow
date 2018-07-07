package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.util.Accrue;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.block.base.BlockPersistentState;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.client.gui.XbowGuis;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockLaser;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BlockLaser extends BlockPersistentState {

    public BlockLaser() {
        super(LangConst.BLOCK_LASER_NAME, Material.IRON);
        setHardness(5F);
        setResistance(7.5F);
        setTileFactory((w, m) -> getStates().get(m).get(XbowProps.LASER_TYPE).createTileEntity());
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
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileLaser tile = getTileEntity(world, pos);
        if (tile != null) {
            tile.setDirection(tile.getDirection() == axis ? axis.getOpposite() : axis);
            world.notifyNeighborsOfStateChange(pos, this, true);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return EnumFacing.VALUES;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing face, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            Xbow.INSTANCE.getGuiHandler().openGui(player, XbowGuis.laser, new WorldBlockPos(world, pos));
        }
        return true;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileLaser tile = getTileEntity(world, pos);
        return tile != null ? state.withProperty(XbowProps.ROTATION, tile.getDirection()) : state;
    }

    public enum Type implements IStringSerializable {

        TIER_1("tier_1", TileLaser.Tier1::new, 1024, 0.25D, 0.0032D),
        TIER_2("tier_2", TileLaser.Tier2::new,4096, 0.1875D, 0.0024D),
        TIER_3("tier_3", TileLaser.Tier3::new, 32768, 0.125D, 0.0016D),
        TIER_4("tier_4", TileLaser.Tier4::new, 16777216, 0.096D, 0.0008D);

        private final String serializableName;
        private final Supplier<? extends TileLaser> tileFactory;
        private final double basePower;
        private final double baseRadius;
        private final double baseFluxAngle;

        Type(String serializableName, Supplier<? extends TileLaser> tileFactory, double basePower, double baseRadius, double baseFluxAngle) {
            this.serializableName = serializableName;
            this.tileFactory = tileFactory;
            this.basePower = basePower;
            this.baseRadius = baseRadius;
            this.baseFluxAngle = baseFluxAngle;
        }

        public double getBasePower() {
            return basePower;
        }

        public double getBaseRadius() {
            return baseRadius;
        }

        public double getBaseFluxAngle() {
            return baseFluxAngle;
        }

        public String getItemModel() {
            return "laser_" + serializableName;
        }

        @Override
        public String getName() {
            return serializableName;
        }

        public TileLaser createTileEntity() {
            return tileFactory.get();
        }

    }

}
