package io.github.phantamanta44.pcrossbow.inventory.slot;

import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LaserSlot extends SlotItemHandler {

    private final Capability<?> cap;

    public LaserSlot(TileLaser laser, int slot, int x, int y, Capability<?> cap) {
        super(laser.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), slot, x, y);
        this.cap = cap;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.hasCapability(cap, null);
    }

}
