package io.github.phantamanta44.pcrossbow.block.base;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.item.base.ItemBlockWithMetadataAndName;
import io.github.phantamanta44.pcrossbow.util.IconHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockModSubs extends BlockMod {

    protected final int subblockCount;
    protected IIcon[] icons;

    public BlockModSubs(Material material, int blocks) {
        super(material);
        subblockCount = blocks;
    }

    @Override
    public void getSubBlocks(Item parent, CreativeTabs tab, List items) {
        for (int i = 0; i < subblockCount; i++)
            items.add(new ItemStack(parent, 1, i));
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public Block setBlockName(String name) {
        if (GameRegistry.findBlock(XbowConst.MOD_ID, name) == null)
            GameRegistry.registerBlock(this, ItemBlockWithMetadataAndName.class, name);
        return super.setBlockName(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister registry) {
        icons = new IIcon[subblockCount];
        for (int i = 0; i < subblockCount; i++)
            icons[i] = IconHelper.forBlock(registry, this, i);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int face) {
        return icons[world.getBlockMetadata(x, y, z)];
    }

    @Override
    public IIcon getIcon(int id, int meta) {
        return icons[meta];
    }

}
