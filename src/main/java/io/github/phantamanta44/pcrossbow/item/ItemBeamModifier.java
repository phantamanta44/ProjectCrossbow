package io.github.phantamanta44.pcrossbow.item;

import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.client.model.ParameterizedItemModel;
import io.github.phantamanta44.libnine.item.L9ItemSubs;
import io.github.phantamanta44.libnine.util.ImpossibilityRealizedException;
import io.github.phantamanta44.libnine.util.helper.FormatUtils;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.DoubleFunction;

public class ItemBeamModifier extends L9ItemSubs implements ParameterizedItemModel.IParamaterized {

    private final Type type;

    public ItemBeamModifier(String name, Type type) {
        super(name, Tier.values().length);
        this.type = type;
    }

    @Override
    protected String getModelName(int variant) {
        return "beam_mod/" + getInternalName();
    }

    @Override
    public void getModelMutations(ItemStack stack, ParameterizedItemModel.Mutation m) {
        m.mutate("tier", Tier.values()[stack.getMetadata()].getMutationValue());
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return ((ItemBeamModifier)stack.getItem()).type.getCapabilities(Tier.values()[stack.getMetadata()]);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        switch (Tier.values()[stack.getMetadata()]) {
            case TIER_0:
            case TIER_1:
                return EnumRarity.COMMON;
            case TIER_2:
                return EnumRarity.UNCOMMON;
            case TIER_3:
                return EnumRarity.RARE;
            case TIER_4:
                return EnumRarity.EPIC;
        }
        throw new ImpossibilityRealizedException();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + LangConst.get(type.getTooltipKey(),
                TextFormatting.WHITE + FormatUtils.formatPercentage(type.tiers[stack.getMetadata()])));
    }

    public enum Type {

        POWER(XbowCaps.LASER_MOD_POWER, LangConst.INFO_MOD_POWER,
                m -> p -> p * m, 1D, 2D, 3D, 5D, 0.5D),
        RADIUS(XbowCaps.LASER_MOD_RADIUS, LangConst.INFO_MOD_RADIUS,
                m -> p -> p * m, 0.9D, 0.85D, 0.8D, 0.75D, 1.15D),
        FLUX_ANGLE(XbowCaps.LASER_MOD_FLUX_ANGLE, LangConst.INFO_MOD_FLUX_ANGLE,
                m -> p -> p * m, 0.96D, 0.93D, 0.91D, 0.895D, 1.1D);

        private final Capability cap;
        private final String ttKey;
        private final DoubleFunction factory;
        private final double[] tiers;

        <T> Type(Capability<T> cap, String ttKey, DoubleFunction<T> factory,
                 double t1, double t2, double t3, double t4, double t0) {
            this.cap = cap;
            this.ttKey = ttKey;
            this.factory = factory;
            this.tiers = new double[] { t1, t2, t3, t4, t0 };
        }

        @SuppressWarnings("unchecked")
        public ICapabilityProvider getCapabilities(Tier tier) {
            return new CapabilityBroker().with(cap, factory.apply(getMultiplier(tier)));
        }

        public String getTooltipKey() {
            return ttKey;
        }

        public double getMultiplier(Tier tier) {
            return tiers[tier.ordinal()];
        }

    }

    public enum Tier {

        TIER_1, TIER_2, TIER_3, TIER_4, TIER_0;

        public String getMutationValue() {
            return name().toLowerCase();
        }

    }

}
