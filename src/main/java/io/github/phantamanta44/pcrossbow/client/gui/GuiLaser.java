package io.github.phantamanta44.pcrossbow.client.gui;

import io.github.phantamanta44.libnine.util.helper.FormatUtils;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentEnergy;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentInfo;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentRedstone;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.inventory.ContainerLaser;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;

public class GuiLaser extends GuiBase {

    public GuiLaser(ContainerLaser cont) {
        super(cont, ResConst.GUI_LASER);
        addComponent(new GuiComponentEnergy(cont.getEnergyStorage()));
        addComponent(new GuiComponentRedstone(cont));
        addComponent(new GuiComponentInfo(cont.getTitle(), () -> Arrays.asList(
                LangConst.get(LangConst.INFO_POWER, TextFormatting.WHITE
                        + FormatUtils.formatSI(cont.getPower(), LangConst.get(LangConst.UNIT_POWER))),
                LangConst.get(LangConst.INFO_RADIUS, TextFormatting.WHITE
                        + FormatUtils.formatSI(cont.getRadius(), LangConst.get(LangConst.UNIT_DIST))),
                LangConst.get(LangConst.INFO_FLUX_ANGLE, TextFormatting.WHITE
                        + FormatUtils.formatSI(cont.getFluxAngle(), LangConst.get(LangConst.UNIT_ANGLE))))));
    }

}
