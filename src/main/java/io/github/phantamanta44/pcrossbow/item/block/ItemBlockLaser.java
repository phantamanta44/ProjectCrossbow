package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.libnine.client.model.ParameterizedItemModel;
import io.github.phantamanta44.libnine.util.ImpossibilityRealizedException;
import io.github.phantamanta44.libnine.util.helper.FormatUtils;
import io.github.phantamanta44.pcrossbow.block.BlockLaser;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.item.base.ItemBlockPersistentDevice;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockLaser extends ItemBlockPersistentDevice implements ParameterizedItemModel.IParamaterized {
    
    public ItemBlockLaser(BlockLaser block) {
        super(block);
    }

    @Override
    public void getModelMutations(ItemStack stack, ParameterizedItemModel.Mutation m) {
        m.mutate("type", getBlock().getStates().get(stack.getMetadata()).get(XbowProps.LASER_TYPE).getMutationValue());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        switch (getBlock().getStates().get(stack.getMetadata()).get(XbowProps.LASER_TYPE)) {
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
        super.addInformation(stack, world, tooltip, flagIn);
        BlockLaser.Type type = getBlock().getStates().get(stack.getMetadata()).get(XbowProps.LASER_TYPE);
        tooltip.add(TextFormatting.GRAY + LangConst.get(LangConst.INFO_BASE_POWER,
                TextFormatting.WHITE + FormatUtils.formatSI(type.getBasePower(), LangConst.get(LangConst.UNIT_POWER))));
        tooltip.add(TextFormatting.GRAY + LangConst.get(LangConst.INFO_BASE_RADIUS,
                TextFormatting.WHITE + FormatUtils.formatSI(type.getBaseRadius(), LangConst.get(LangConst.UNIT_DIST))));
        tooltip.add(TextFormatting.GRAY + LangConst.get(LangConst.INFO_BASE_FLUX_ANGLE,
                TextFormatting.WHITE + FormatUtils.formatSI(type.getBaseFluxAngle(), LangConst.get(LangConst.UNIT_ANGLE))));
    }

    @Override
    protected int getMaxEnergy(int meta) {
        return (int)Math.ceil(getBlock().getStates().get(meta).get(XbowProps.LASER_TYPE).getBasePower() * 20);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) return false;
        ((TileLaser)world.getTileEntity(pos)).setDirection(EnumFacing.getDirectionFromEntityLiving(pos, player));
        return true;
    }
    
}
