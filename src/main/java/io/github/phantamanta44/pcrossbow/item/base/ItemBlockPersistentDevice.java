package io.github.phantamanta44.pcrossbow.item.base;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.capability.L9AspectEnergy;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.component.IntReservoir;
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
        if (nbt == null) return null;
        CapabilityBroker provider = new CapabilityBroker()
                .with(CapabilityEnergy.ENERGY, new L9AspectEnergy(new EnergyReservoir(stack, this)));
        return provider;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);
            tooltip.add(
                    LangConst.get(TextFormatting.GRAY + LangConst.get(LangConst.INFO_ENERGY_STORED) + ": " +
                            TextFormatting.AQUA + FormatUtils.formatSI(energy.getEnergyStored(), "FE") + " / " +
                            FormatUtils.formatSI(energy.getMaxEnergyStored(), "FE")));
        }
    }

    protected abstract int getMaxEnergy(NBTTagCompound tag);

    private static class EnergyReservoir extends IntReservoir {

        private final ItemStack stack;

        public EnergyReservoir(ItemStack stack, ItemBlockPersistentDevice item) {
            super(stack.getTagCompound().getInteger(NBTConst.ENERGY), item.getMaxEnergy(stack.getTagCompound()));
            this.stack = stack;
        }

        @Override
        public void setQuantity(int qty) {
            super.setQuantity(qty);
            stack.getTagCompound().setInteger(NBTConst.ENERGY, qty);
        }

    }

}
