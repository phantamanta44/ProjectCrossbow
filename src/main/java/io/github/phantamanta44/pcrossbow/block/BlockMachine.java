package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.gui.GuiIdentity;
import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.libnine.tile.L9TileEntity;
import io.github.phantamanta44.libnine.util.Accrue;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.block.base.IDismantleable;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.client.gui.XbowGuis;
import io.github.phantamanta44.pcrossbow.client.gui.base.GuiMachine;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.inventory.base.ContainerMachine;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockMachine;
import io.github.phantamanta44.pcrossbow.tile.TileLaserFurnace;
import io.github.phantamanta44.pcrossbow.tile.base.TileSimpleProcessor;
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

public class BlockMachine extends L9BlockStated implements IDismantleable {

    public BlockMachine() {
        super(LangConst.BLOCK_MACHINE_NAME, Material.IRON);
        setHardness(5F);
        setResistance(7.5F);
        setTileFactory((w, m) -> Type.values()[m].tileFactory.get());
    }

    @Override
    protected L9ItemBlock initItemBlock() {
        return new ItemBlockMachine(this);
    }

    @Override
    protected void accrueProperties(Accrue<IProperty<?>> props) {
        props.accept(XbowProps.MACHINE_TYPE);
    }

    @Override
    protected void accrueVolatileProperties(Accrue<IProperty<?>> props) {
        props.acceptAll(XbowProps.ROTATION_HOR, XbowProps.ACTIVE);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileSimpleProcessor tile = getTileEntity(world, pos);
        if (tile != null) {
            if (axis == EnumFacing.UP) {
                tile.setDirection(tile.getDirection().rotateY());
            } else if (axis == EnumFacing.DOWN) {
                tile.setDirection(tile.getDirection().rotateYCCW());
            } else {
                tile.setDirection(tile.getDirection() == axis ? axis.getOpposite() : axis);
            }
            world.notifyNeighborsOfStateChange(pos, this, true);
            return true;
        }
        return false;
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
        dropBlockAsItem(world, pos, state, 0);
    }

    @Nullable
    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return EnumFacing.HORIZONTALS;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing face, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            Xbow.INSTANCE.getGuiHandler().openGui(player, state.getValue(XbowProps.MACHINE_TYPE).gui, new WorldBlockPos(world, pos));
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileSimpleProcessor tile = getTileEntity(world, pos);
        return tile == null ? state : state
                .withProperty(XbowProps.ROTATION_HOR, tile.getDirection())
                .withProperty(XbowProps.ACTIVE, tile.isWorking());
    }

    public enum Type implements IStringSerializable {

        FURNACE(TileLaserFurnace::new, XbowGuis.laserFurnace);

        private final Supplier<? extends L9TileEntity> tileFactory;
        private final GuiIdentity<? extends ContainerMachine, ? extends GuiMachine> gui;

        Type(Supplier<? extends L9TileEntity> tileFactory, GuiIdentity<? extends ContainerMachine, ? extends GuiMachine> gui) {
            this.tileFactory = tileFactory;
            this.gui = gui;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
