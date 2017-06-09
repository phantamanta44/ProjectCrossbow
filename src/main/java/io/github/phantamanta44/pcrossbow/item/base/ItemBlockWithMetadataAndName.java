package io.github.phantamanta44.pcrossbow.item.base;

import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockWithMetadataAndName extends ItemBlockWithMetadata {

    public ItemBlockWithMetadataAndName(Block block) {
        super(block, block);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
        return super.getUnlocalizedNameInefficiently(par1ItemStack).replaceAll("tile.", "tile." + XbowConst.MOD_PREF);
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + par1ItemStack.getItemDamage();
    }

}
