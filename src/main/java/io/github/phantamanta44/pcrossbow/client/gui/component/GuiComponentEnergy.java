package io.github.phantamanta44.pcrossbow.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.impl.GuiComponentVerticalBar;
import io.github.phantamanta44.libnine.util.helper.FormatUtils;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.util.EnergyUtils;
import net.minecraftforge.energy.IEnergyStorage;

public class GuiComponentEnergy extends GuiComponentVerticalBar {

    public GuiComponentEnergy(IEnergyStorage src) {
        super(162, 9, ResConst.COMP_ENERGY_BG, ResConst.COMP_ENERGY_FG, 2, 2,
                () -> EnergyUtils.getPercentage(src), () -> LangConst.get(LangConst.INFO_FRACTION,
                        FormatUtils.formatSI(src.getEnergyStored(), LangConst.get(LangConst.UNIT_ENERGY)),
                        FormatUtils.formatSI(src.getMaxEnergyStored(), LangConst.get(LangConst.UNIT_ENERGY))));
    }

}
