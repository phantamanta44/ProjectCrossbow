package io.github.phantamanta44.pcrossbow.client.gui.base;

import io.github.phantamanta44.libnine.client.gui.L9GuiContainer;
import io.github.phantamanta44.libnine.gui.L9Container;
import net.minecraft.util.ResourceLocation;

public class GuiBase extends L9GuiContainer {

    public GuiBase(L9Container container, ResourceLocation bg) {
        super(container, bg);
    }

    @Override
    public void drawForeground(float partialTicks, int mX, int mY) {
        // NO-OP
    }

}
