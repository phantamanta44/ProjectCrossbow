package io.github.phantamanta44.pcrossbow.item.base;

import cofh.api.energy.IEnergyContainerItem;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public abstract class ItemBlockPersistentDevice extends ItemBlockPersistentState implements IEnergyContainerItem {

    public ItemBlockPersistentDevice(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        info.add(
                LangConst.get(EnumChatFormatting.GRAY + LangConst.get(LangConst.INFO_ENERGY_STORED) + ": " +
                EnumChatFormatting.AQUA + Integer.toString(getEnergyStored(stack)) + " / " +
                Integer.toString(getMaxEnergyStored(stack)) + " RF"));
    }

}
