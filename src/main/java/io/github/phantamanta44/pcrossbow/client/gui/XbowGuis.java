package io.github.phantamanta44.pcrossbow.client.gui;

import io.github.phantamanta44.libnine.InitMe;
import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.gui.GuiIdentity;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import io.github.phantamanta44.pcrossbow.inventory.ContainerInductor;
import io.github.phantamanta44.pcrossbow.inventory.ContainerLaser;
import io.github.phantamanta44.pcrossbow.inventory.ContainerLaserFurnace;
import io.github.phantamanta44.pcrossbow.inventory.ContainerVectorWrench;
import net.minecraftforge.fml.relauncher.Side;

public class XbowGuis {

    public static final GuiIdentity<ContainerVectorWrench, GuiVectorWrench> vectorWrench
            = new GuiIdentity<>(LangConst.GUI_VECTOR_WRENCH, ContainerVectorWrench.class);
    public static final GuiIdentity<ContainerLaser, GuiLaser> laser
            = new GuiIdentity<>(LangConst.GUI_LASER, ContainerLaser.class);
    public static GuiIdentity<ContainerInductor, GuiInductor> inductor
            = new GuiIdentity<>(LangConst.GUI_INDUCTOR, ContainerInductor.class);
    public static GuiIdentity<ContainerLaserFurnace, GuiLaserFurnace> laserFurnace
            = new GuiIdentity<>(LangConst.GUI_LASER_FURNACE, ContainerLaserFurnace.class);

    @InitMe(XbowConst.MOD_ID)
    public static void init() {
        LibNine.PROXY.getRegistrar().queueGuiServerReg(vectorWrench, ContainerVectorWrench::new);
        LibNine.PROXY.getRegistrar().queueGuiServerReg(laser, ContainerLaser::new);
        LibNine.PROXY.getRegistrar().queueGuiServerReg(inductor, ContainerInductor::new);
        LibNine.PROXY.getRegistrar().queueGuiServerReg(laserFurnace, ContainerLaserFurnace::new);
    }

    @InitMe(value = XbowConst.MOD_ID, sides = { Side.CLIENT })
    public static void initClient() {
        LibNine.PROXY.getRegistrar().queueGuiClientReg(vectorWrench, (c, p, w, x, y, z) -> new GuiVectorWrench(c));
        LibNine.PROXY.getRegistrar().queueGuiClientReg(laser, (c, p, w, x, y, z) -> new GuiLaser(c));
        LibNine.PROXY.getRegistrar().queueGuiClientReg(inductor, (c, p, w, x, y, z) -> new GuiInductor(c));
        LibNine.PROXY.getRegistrar().queueGuiClientReg(laserFurnace, (c, p, w, x, y, z) -> new GuiLaserFurnace(c));
    }

}
