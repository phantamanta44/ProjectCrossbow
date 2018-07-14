package io.github.phantamanta44.pcrossbow.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.GuiComponent;
import io.github.phantamanta44.libnine.util.render.GuiUtils;
import io.github.phantamanta44.libnine.util.world.IRedstoneControllable;
import io.github.phantamanta44.libnine.util.world.RedstoneBehaviour;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;

public class GuiComponentRedstone extends GuiComponent {

    private final IRedstoneControllable target;

    public GuiComponentRedstone(IRedstoneControllable target) {
        super(10, 21, 21, 7);
        this.target = target;
    }

    @Override
    public void render(float partialTicks, int mX, int mY, boolean mouseOver) {
        int currentX = x;
        for (RedstoneBehaviour behaviour : RedstoneBehaviour.values()) {
            if (target.getRedstoneBehaviour() == behaviour) {
                ResConst.COMP_REDSTONE_BG.draw(currentX, y, 7, 7);
                ResConst.COMP_REDSTONE_CROSSHAIR.draw(currentX, y, 7, 7);
            } else if (GuiUtils.isMouseOver(currentX, y, 7, 7, mX, mY)) {
                ResConst.COMP_REDSTONE_CROSSHAIR.draw(currentX, y, 7, 7);
            }
            ResConst.getCompRedstoneIcon(behaviour).draw(currentX, y, 7, 7);
            currentX += 7;
        }
    }

    @Override
    public void renderTooltip(float partialTicks, int mX, int mY) {
        if (GuiUtils.isMouseOver(x, y, 7, 7, mX, mY)) {
            drawTooltip(LangConst.get(LangConst.INFO_REDSTONE_IGNORED), mX, mY);
        } else if (GuiUtils.isMouseOver(x + 7, y, 7, 7, mX, mY)) {
            drawTooltip(LangConst.get(LangConst.INFO_REDSTONE_DIRECT), mX, mY);
        } else if (GuiUtils.isMouseOver(x + 14, y, 7, 7, mX, mY)) {
            drawTooltip(LangConst.get(LangConst.INFO_REDSTONE_INVERTED), mX, mY);
        }
    }

    @Override
    public boolean onClick(int mX, int mY, int button, boolean mouseOver) {
        if (mouseOver) {
            if (GuiUtils.isMouseOver(x, y, 7, 7, mX, mY)) {
                target.setRedstoneBehaviour(RedstoneBehaviour.IGNORED);
            } else if (GuiUtils.isMouseOver(x + 7, y, 7, 7, mX, mY)) {
                target.setRedstoneBehaviour(RedstoneBehaviour.DIRECT);
            } else if (GuiUtils.isMouseOver(x + 14, y, 7, 7, mX, mY)) {
                target.setRedstoneBehaviour(RedstoneBehaviour.INVERTED);
            }
            playClickSound();
            return true;
        }
        return false;
    }

}
