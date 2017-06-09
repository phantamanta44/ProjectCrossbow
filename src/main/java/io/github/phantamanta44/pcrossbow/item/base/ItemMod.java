package io.github.phantamanta44.pcrossbow.item.base;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.github.phantamanta44.pcrossbow.ProjectCrossbow;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.util.IconHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMod extends Item {

    public ItemMod() {
        super();
        setCreativeTab(ProjectCrossbow.INSTANCE.creativeTab);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        GameRegistry.registerItem(this, name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public String getUnlocalizedNameInefficiently(ItemStack itemstack) {
        return super.getUnlocalizedNameInefficiently(itemstack).replaceAll("item\\.", "item." + XbowConst.MOD_PREF);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister registry) {
        itemIcon = IconHelper.forItem(registry, this);
    }

}