package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.pcrossbow.client.render.tesr.TESRInductor;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

public class BlockInductor extends L9BlockStated {

    public BlockInductor() {
        super(LangConst.BLOCK_INDUCTOR_NAME, Material.REDSTONE_LIGHT);
        setHardness(5F);
        setResistance(7.5F);
        setTileFactory((w, m) -> new TileInductor());
    }

    @Override
    protected void initModel() {
        super.initModel();
        LibNine.PROXY.getRegistrar().queueTESRReg(TileInductor.class, new TESRInductor());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }

}
