package io.github.phantamanta44.pcrossbow.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.GuiComponent;
import io.github.phantamanta44.libnine.util.math.Vec2i;
import io.github.phantamanta44.libnine.util.render.GuiUtils;
import io.github.phantamanta44.libnine.util.world.BlockSide;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.util.IAllocableSides;
import io.github.phantamanta44.pcrossbow.util.SlotType;

public class GuiComponentSideAlloc<T extends Enum<T> & SlotType.IType<T>> extends GuiComponent {

    private final IAllocableSides<T> sides;
    private final Vec2i[] buttonOrigins;

    public GuiComponentSideAlloc(IAllocableSides<T> sides) {
        super(10, 30, 21, 21);
        this.sides = sides;
        this.buttonOrigins = new Vec2i[BlockSide.values().length];
        for (BlockSide side : BlockSide.values()) {
            switch (side) {
                case FRONT:
                    this.buttonOrigins[side.ordinal()] = new Vec2i(x + 7, y + 7);
                    break;
                case BACK:
                    this.buttonOrigins[side.ordinal()] = new Vec2i(x + 14, y + 14);
                    break;
                case UP:
                    this.buttonOrigins[side.ordinal()] = new Vec2i(x + 7, y);
                    break;
                case LEFT:
                    this.buttonOrigins[side.ordinal()] = new Vec2i(x, y + 7);
                    break;
                case DOWN:
                    this.buttonOrigins[side.ordinal()] = new Vec2i(x + 7, y + 14);
                    break;
                case RIGHT:
                    this.buttonOrigins[side.ordinal()] = new Vec2i(x + 14, y + 7);
                    break;
            }
        }
    }

    @Override
    public void render(float partialTicks, int mX, int mY, boolean mouseOver) {
        for (BlockSide side : BlockSide.values()) {
            Vec2i origin = buttonOrigins[side.ordinal()];
            ResConst.COMP_SLOTS_BG.draw(origin.getX(), origin.getY(), 7, 7);
            sides.getFace(side).getTexture().draw(origin.getX(), origin.getY(), 7, 7);
            if (GuiUtils.isMouseOver(origin.getX(), origin.getY(), 7, 7, mX, mY)) {
                ResConst.COMP_SLOTS_CROSSHAIR.draw(origin.getX(), origin.getY(), 7, 7);
            }
        }
    }

    @Override
    public void renderTooltip(float partialTicks, int mX, int mY) {
        for (BlockSide side : BlockSide.values()) {
            Vec2i origin = buttonOrigins[side.ordinal()];
            if (GuiUtils.isMouseOver(origin.getX(), origin.getY(), 7, 7, mX, mY)) {
                drawTooltip(sides.getFace(side).getTooltip(), mX, mY);
            }
        }
    }

    @Override
    public boolean onClick(int mX, int mY, int button, boolean mouseOver) {
        if (mouseOver) {
            for (BlockSide side : BlockSide.values()) {
                Vec2i origin = buttonOrigins[side.ordinal()];
                if (GuiUtils.isMouseOver(origin.getX(), origin.getY(), 7, 7, mX, mY)) {
                    if (button == 0) {
                        sides.setFace(side, sides.getFace(side).next());
                    } else if (button == 1) {
                        sides.setFace(side, sides.getFace(side).prev());
                    } else {
                        return false;
                    }
                    playClickSound();
                    return true;
                }
            }
        }
        return false;
    }

}
