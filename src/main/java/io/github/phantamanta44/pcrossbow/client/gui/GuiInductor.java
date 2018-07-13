package io.github.phantamanta44.pcrossbow.client.gui;

import io.github.phantamanta44.libnine.util.helper.FormatUtils;
import io.github.phantamanta44.libnine.util.render.GuiUtils;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentInfo;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.inventory.ContainerInductor;
import io.github.phantamanta44.pcrossbow.util.PhysicsUtils;

public class GuiInductor extends GuiBase {

    private final ContainerInductor cont;

    public GuiInductor(ContainerInductor cont) {
        super(cont, ResConst.GUI_INDUCTOR);
        this.cont = cont;
        addComponent(new GuiComponentInfo(cont.getTitle(), null));
    }

    @Override
    public void drawForeground(float partialTicks, int mX, int mY) {
        if (cont.isActive()) {
            double power = cont.getPower(), radius = cont.getRadius(), fluxAngle = cont.getFluxAngle();
            drawString(FormatUtils.formatSI(power, LangConst.get(LangConst.UNIT_POWER)), 24, 28, ResConst.COL_HUD);
            drawString(FormatUtils.formatSI(radius, LangConst.get(LangConst.UNIT_DIST)), 24, 39, ResConst.COL_HUD);
            drawString(FormatUtils.formatSI(fluxAngle, LangConst.get(LangConst.UNIT_ANGLE)), 24, 50, ResConst.COL_HUD);
            drawString(FormatUtils.formatSI(PhysicsUtils.calculateIntensity(power, radius), LangConst.get(LangConst.UNIT_INT)), 24, 61, ResConst.COL_HUD);
        } else {
            String str = LangConst.get(LangConst.INFO_NOT_APPLICABLE);
            for (int i = 0; i < 4; i++) drawString(str, 24, 28 + 11 * i, ResConst.COL_HUD);
        }
        if (GuiUtils.isMouseOver(13, 28, 7, 7, mX, mY)) {
            drawTooltip(LangConst.get(LangConst.INFO_POWER_NAME), mX, mY);
        } else if (GuiUtils.isMouseOver(13, 39, 7, 7, mX, mY)) {
            drawTooltip(LangConst.get(LangConst.INFO_RADIUS_NAME), mX, mY);
        } else if (GuiUtils.isMouseOver(13, 50, 7, 7, mX, mY)) {
            drawTooltip(LangConst.get(LangConst.INFO_FLUX_ANGLE_NAME), mX, mY);
        } else if (GuiUtils.isMouseOver(13, 61, 7, 7, mX, mY)) {
            drawTooltip(LangConst.get(LangConst.INFO_INTENSITY_NAME), mX, mY);
        }
    }

}
