package io.github.phantamanta44.pcrossbow.item.base;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.pcrossbow.item.base.ItemBlockPersistentDevice;
import net.minecraft.nbt.NBTTagCompound;

public class ItemBlockPowered extends ItemBlockPersistentDevice {

    private final int maxEnergy;

    public ItemBlockPowered(L9BlockStated block, int maxEnergy) {
        super(block);
        this.maxEnergy = maxEnergy;
    }

    @Override
    protected int getMaxEnergy(int meta) {
        return maxEnergy;
    }

}
