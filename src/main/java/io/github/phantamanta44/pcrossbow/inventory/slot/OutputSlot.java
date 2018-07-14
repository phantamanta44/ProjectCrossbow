package io.github.phantamanta44.pcrossbow.inventory.slot;

import io.github.phantamanta44.libnine.capability.impl.L9AspectSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class OutputSlot extends SlotItemHandler {

    public OutputSlot(L9AspectSlot slot, int x, int y) {
        super(slot, 0, x, y);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }

}
