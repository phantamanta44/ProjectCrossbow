package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.item.L9ItemBlock;
import io.github.phantamanta44.pcrossbow.block.base.BlockPersistentState;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockPowered;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

import java.util.List;

public class BlockInductor extends BlockPersistentState {

    public BlockInductor() {
        super(LangConst.BLOCK_INDUCTOR_NAME, Material.REDSTONE_LIGHT);
        setHardness(5F);
        setResistance(7.5F);
        setTileFactory((w, m) -> new TileInductor());
    }

    @Override
    protected void collectProperties(List<IProperty<?>> props) {
        // NO-OP
    }

    @Override
    protected L9ItemBlock initItemBlock() {
        return new ItemBlockPowered(this, TileInductor.MAX_ENERGY);
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
