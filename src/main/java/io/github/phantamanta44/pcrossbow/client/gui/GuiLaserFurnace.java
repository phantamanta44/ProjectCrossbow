package io.github.phantamanta44.pcrossbow.client.gui;

import io.github.phantamanta44.pcrossbow.client.gui.base.GuiMachine;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentSideAlloc;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentTemperature;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.inventory.ContainerLaserFurnace;
import net.minecraft.client.Minecraft;

public class GuiLaserFurnace extends GuiMachine<ContainerLaserFurnace> {

    public GuiLaserFurnace(ContainerLaserFurnace cont) {
        super(cont, ResConst.GUI_LASER_FURNACE);
        addComponent(new GuiComponentTemperature(cont));
        addComponent(new GuiComponentSideAlloc<>(cont));
    }

    @Override
    public void drawForeground(float partialTicks, int mX, int mY) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ResConst.GUI_LASER_FURNACE);
        drawTexturedModalRect(82, 42, 176, 0, Math.min((int)Math.floor(12 * cont.getProgress()), 12), 2);
    }

}
