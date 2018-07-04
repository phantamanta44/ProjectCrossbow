package io.github.phantamanta44.pcrossbow.client.gui;

import io.github.phantamanta44.libnine.client.gui.L9GuiContainer;
import io.github.phantamanta44.libnine.client.gui.component.GuiComponentTextInput;
import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.pcrossbow.client.gui.component.GuiComponentCardinal;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.gui.ContainerVectorWrench;
import io.github.phantamanta44.pcrossbow.util.VecSer;
import net.minecraft.util.math.Vec3d;

public class GuiVectorWrench extends L9GuiContainer {

    public GuiVectorWrench(ContainerVectorWrench cont) {
        super(cont, ResConst.GUI_VECTOR_WRENCH);
        GuiComponentTextInput textInput = new GuiComponentTextInput(11, 62, 135, 24,
                ResConst.COMP_SUBMIT_ACTIVE, ResConst.COMP_SUBMIT_HOVER, ResConst.COMP_SUBMIT_DISABLED,
                ResConst.COL_HUD, ResConst.COL_HUD_DISABLED,
                VecSer::isValidVector, s -> {
                        Vec3d vec = VecSer.deserialize(s);
                        vec = vec.lengthSquared() == 0 ? LinAlUtils.Y_POS : vec.normalize();
                        cont.setNorm(vec);
                }, VecSer.serialize(cont.getNorm()));
        addComponent(textInput);
        addComponent(new GuiComponentCardinal(69, 14, d -> {
            cont.setNorm(d.getVector());
            textInput.setValue(VecSer.serialize(cont.getNorm()));
        }));
    }

    @Override
    public void drawForeground(float partialTicks, int mX, int mY) {
        // NO-OP
    }

}
