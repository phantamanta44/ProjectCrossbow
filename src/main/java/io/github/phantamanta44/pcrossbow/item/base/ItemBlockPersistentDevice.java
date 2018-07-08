package io.github.phantamanta44.pcrossbow.item.base;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.capability.impl.L9AspectEnergy;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBrokerLazy;
import io.github.phantamanta44.libnine.component.reservoir.SimpleIntReservoir;
import io.github.phantamanta44.libnine.util.helper.FormatUtils;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemBlockPersistentDevice extends ItemBlockPersistentState {

    public ItemBlockPersistentDevice(L9BlockStated block) {
        super(block);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapabilityBrokerLazy(c -> {
            if (c == CapabilityEnergy.ENERGY) {
                if (stack.hasTagCompound()) return new L9AspectEnergy(new EnergyReservoir(stack, this));
            }
            return null;
        });
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, world, tooltip, flagIn);
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);
            tooltip.add(TextFormatting.GREEN + LangConst.get(LangConst.INFO_ENERGY_STORED,
                    TextFormatting.WHITE + FormatUtils.formatSI(energy.getEnergyStored(), LangConst.get(LangConst.UNIT_ENERGY)),
                    FormatUtils.formatSI(energy.getMaxEnergyStored(), LangConst.get(LangConst.UNIT_ENERGY))));
        }
    }

    protected abstract int getMaxEnergy(int meta);

    private static class EnergyReservoir extends SimpleIntReservoir {

        private final ItemStack stack;

        EnergyReservoir(ItemStack stack, ItemBlockPersistentDevice item) {
            super(item.getMaxEnergy(stack.getMetadata()));
            this.stack = stack;
            deserializeNBT(stack.getTagCompound()
                    .getCompoundTag(NBTConst.ITEM_BLOCK_STATE)
                    .getCompoundTag(NBTConst.ENERGY));
        }

        @Override
        public void setQuantity(int qty) {
            super.setQuantity(qty);
            serializeNBT(stack.getTagCompound()
                    .getCompoundTag(NBTConst.ITEM_BLOCK_STATE)
                    .getCompoundTag(NBTConst.ENERGY));
        }

    }

}
