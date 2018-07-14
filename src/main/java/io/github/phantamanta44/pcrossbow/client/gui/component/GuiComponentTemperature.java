package io.github.phantamanta44.pcrossbow.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.impl.GuiComponentVerticalBar;
import io.github.phantamanta44.libnine.util.helper.FormatUtils;
import io.github.phantamanta44.libnine.util.math.MathUtils;
import io.github.phantamanta44.pcrossbow.api.tile.IHeatCarrier;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;

public class GuiComponentTemperature extends GuiComponentVerticalBar {

    public GuiComponentTemperature(IHeatCarrier src) {
        super(162, 9, ResConst.COMP_HEAT_BG, ResConst.COMP_HEAT_FG, 2, 2,
                () -> MathUtils.clamp((float)src.getTemperature() / 3000F, 0, 1),
                () -> FormatUtils.formatSI(src.getTemperature(), LangConst.get(LangConst.UNIT_TEMP)));
    }

}
