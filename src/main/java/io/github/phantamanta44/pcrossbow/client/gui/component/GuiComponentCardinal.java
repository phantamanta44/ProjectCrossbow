package io.github.phantamanta44.pcrossbow.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.GuiComponent;
import io.github.phantamanta44.libnine.util.math.Vec2i;
import io.github.phantamanta44.libnine.util.render.GuiUtils;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.util.Cardinal;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

public class GuiComponentCardinal extends GuiComponent {

    private final Vec2i[][] buttonVertices;
    private final Consumer<Cardinal> callback;

    public GuiComponentCardinal(int x, int y, Consumer<Cardinal> callback) {
        super(x, y, 35, 44);
        this.buttonVertices = new Vec2i[26][];
        Vec2i[] vertexOffsets = {
                new Vec2i(x + 3, y), new Vec2i(x + 8, y), new Vec2i(x + 5, y + 3), new Vec2i(x, y + 3)
        };
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (i != 1 || j != 1 || k != 1) {
                        Vec2i pos = new Vec2i(k * 8 - 5 * j + 8, 5 * j + 15 * i);
                        buttonVertices[index++] = Arrays.stream(vertexOffsets).map(pos::add).toArray(Vec2i[]::new);
                    }
                }
            }
        }
        this.callback = callback;
    }

    @Override
    public void render(float partialTicks, int mX, int mY, boolean mouseOver) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (mouseOver) {
            Cardinal activeDir = getMouseOver(mX, mY);
            if (activeDir != null) {
                activeDir.getTexture().draw(x, y, width, height);
            } else {
                ResConst.COMP_CARDINAL_DEFAULT.draw(x, y, width, height);
            }
        } else {
            ResConst.COMP_CARDINAL_DEFAULT.draw(x, y, width, height);
        }
    }

    @Override
    public void renderTooltip(float partialTicks, int mX, int mY) {
        Cardinal activeDir = getMouseOver(mX, mY);
        if (activeDir != null) drawTooltip(activeDir.getDisplayName(), mX, mY);
    }

    @Override
    public boolean onClick(int mX, int mY, int button, boolean mouseOver) {
        if (mouseOver) {
            Cardinal activeDir = getMouseOver(mX, mY);
            if (activeDir != null) {
                callback.accept(activeDir);
                playClickSound();
                return true;
            }
        }
        return false;
    }

    @Nullable
    private Cardinal getMouseOver(int mX, int mY) {
        for (int i = 0; i < buttonVertices.length; i++) {
            if (GuiUtils.isMouseWithin(mX, mY, buttonVertices[i])) return Cardinal.values()[i];
        }
        return null;
    }

}
