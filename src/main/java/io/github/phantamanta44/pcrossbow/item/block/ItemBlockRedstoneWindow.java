package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.item.L9ItemBlockStated;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import net.minecraft.item.ItemStack;

public class ItemBlockRedstoneWindow extends L9ItemBlockStated {

    public ItemBlockRedstoneWindow(L9BlockStated block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getBlock().getStateFromMeta(stack.getMetadata())
                .getValue(XbowProps.REDSTONE_WINDOW_TYPE).getUnlocalizedName();
    }

}
