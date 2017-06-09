package io.github.phantamanta44.pcrossbow.block;

import cofh.api.block.IDismantleable;
import cpw.mods.fml.common.registry.GameRegistry;
import io.github.phantamanta44.pcrossbow.block.base.BlockModSubs;
import io.github.phantamanta44.pcrossbow.block.base.ItemBlockState;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.item.block.ItemBlockLaser;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

@ItemBlockState
public class BlockLaser extends BlockModSubs implements ITileEntityProvider, IDismantleable {

    public BlockLaser() {
        super(Material.iron, 4);
        setHardness(5F);
        setResistance(7.5F);
        setBlockName(LangConst.BLOCK_LASER_NAME);
    }

    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockLaser.class, name);
        return super.setBlockName(name);
    }

    @Override
    public void registerBlockIcons(IIconRegister registry) { // TODO Implement for other laser types
        icons = new IIcon[] {
                registry.registerIcon(XbowConst.MOD_PREF + LangConst.BLOCK_LASER_NAME + "0_front"),
                registry.registerIcon(XbowConst.MOD_PREF + LangConst.BLOCK_LASER_NAME + "0_side")
        };
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int face) { // TODO Implement for other laser types
        return icons[((TileLaser)world.getTileEntity(x, y, z)).getFacing() == face ? 0 : 1];
    }

    @Override
    public IIcon getIcon(int face, int meta) { // TODO Implement for other laser types
        return icons[face == 3 ? 0 : 1];
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        switch (meta) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                throw new IllegalStateException("Illegal laser with meta: " + Integer.toString(meta));
        }
        return new TileLaser.Test();
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
