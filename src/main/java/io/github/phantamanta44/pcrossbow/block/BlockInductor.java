package io.github.phantamanta44.pcrossbow.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.common.registry.GameRegistry;
import io.github.phantamanta44.pcrossbow.api.LaserConsuming;
import io.github.phantamanta44.pcrossbow.block.base.BlockMod;
import io.github.phantamanta44.pcrossbow.block.base.ItemBlockState;
import io.github.phantamanta44.pcrossbow.client.ClientProxy;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockPowered;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

@ItemBlockState
@LaserConsuming
public class BlockInductor extends BlockMod implements ITileEntityProvider, IDismantleable {

    public BlockInductor() {
        super(Material.iron);
        setHardness(5F);
        setResistance(7.5F);
        setBlockName(LangConst.BLOCK_INDUCTOR_NAME);
    }

    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockPowered.class, name);
        return super.setBlockName(name);
    }


    public int getRenderType() {
        return ClientProxy.renderInductorBlock;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void registerBlockIcons(IIconRegister registry) {
        // NO-OP
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileInductor();
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {
        removedByPlayer(world, player, x, y, z, !returnDrops);
        if (!returnDrops)
            compiledDrops.forEach(is -> world.spawnEntityInWorld(new EntityItem(world, x, y, z, is)));
        return compiledDrops;
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {
        return true;
    }

}
