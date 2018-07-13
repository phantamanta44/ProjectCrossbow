package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.block.base.IDismantleable;
import io.github.phantamanta44.pcrossbow.client.gui.XbowGuis;
import io.github.phantamanta44.pcrossbow.client.render.tesr.TESRInductor;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInductor extends L9BlockStated implements IDismantleable {

    public BlockInductor() {
        super(LangConst.BLOCK_INDUCTOR_NAME, Material.REDSTONE_LIGHT);
        setHardness(0.35F);
        setSoundType(SoundType.GLASS);
        setTileFactory((w, m) -> new TileInductor());
    }

    @Override
    protected void initModel() {
        super.initModel();
        LibNine.PROXY.getRegistrar().queueTESRReg(TileInductor.class, new TESRInductor());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing face, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            Xbow.INSTANCE.getGuiHandler().openGui(player, XbowGuis.inductor, new WorldBlockPos(world, pos));
        }
        return true;
    }

    @Override
    public void dismantle(EntityPlayer player, World world, BlockPos pos, IBlockState state, EnumFacing face) {
        dropBlockAsItem(world, pos, state, 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileInductor tile = getTileEntity(world, pos);
        return (tile != null && tile.getEnergyStored() > 0) ? 15 : 0;
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

}
