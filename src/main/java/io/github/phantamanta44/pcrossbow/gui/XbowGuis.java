package io.github.phantamanta44.pcrossbow.gui;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.libnine.gui.GuiIdentity;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.client.gui.GuiVectorWrench;
import io.github.phantamanta44.pcrossbow.constant.LangConst;

public class XbowGuis {

    public static final GuiIdentity<ContainerVectorWrench, GuiVectorWrench> vectorWrench
            = new GuiIdentity<>(LangConst.GUI_VECTOR_WRENCH, ContainerVectorWrench.class);

    public static void init() {
        LibNine.PROXY.getRegistrar().begin(Xbow.INSTANCE);
        LibNine.PROXY.getRegistrar().queueGuiServerReg(vectorWrench, ContainerVectorWrench::new);
        LibNine.PROXY.getRegistrar().end();
    }

    public static void initClient() {
        LibNine.PROXY.getRegistrar().begin(Xbow.INSTANCE);
        LibNine.PROXY.getRegistrar().queueGuiClientReg(vectorWrench, (c, p, w, x, y, z) -> new GuiVectorWrench(c));
        LibNine.PROXY.getRegistrar().end();
    }

}
