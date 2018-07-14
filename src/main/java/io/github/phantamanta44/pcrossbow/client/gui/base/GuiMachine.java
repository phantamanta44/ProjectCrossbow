package io.github.phantamanta44.pcrossbow.client.gui.base;

import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentInfo;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentRedstone;
import io.github.phantamanta44.pcrossbow.inventory.base.ContainerMachine;
import net.minecraft.util.ResourceLocation;

public class GuiMachine<T extends ContainerMachine> extends GuiBase {

    protected final T cont;

    public GuiMachine(T cont, ResourceLocation bg) {
        super(cont, bg);
        this.cont = cont;
        addComponent(new GuiComponentRedstone(cont));
        addComponent(new GuiComponentInfo(cont.getTitle(), null));
    }

}
