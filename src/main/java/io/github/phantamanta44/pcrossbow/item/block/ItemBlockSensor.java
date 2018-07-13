package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.item.L9ItemBlockStated;
import net.minecraft.item.ItemStack;

public class ItemBlockSensor extends L9ItemBlockStated {

    public ItemBlockSensor(L9BlockStated block) {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getBlock().getUnlocalizedName();
    }

}
