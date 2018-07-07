package io.github.phantamanta44.pcrossbow.client.gui.component;

import io.github.phantamanta44.libnine.client.gui.component.GuiComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiComponentInfo extends GuiComponent {

    private static final String PREFIX = TextFormatting.DARK_GRAY + "- " + TextFormatting.GRAY;

    private final String title;
    private final Supplier<List<String>> src;

    public GuiComponentInfo(String title, @Nullable Supplier<List<String>> src) {
        super(7, 7, 29, 11);
        this.title = title;
        this.src = src;
    }

    @Override
    public void render(float partialTicks, int mX, int mY, boolean mouseOver) {
        // NO-OP
    }

    @Override
    public void renderTooltip(float partialTicks, int mX, int mY) {
        if (src != null) {
            drawTooltip(Stream.concat(Stream.of(title), src.get().stream()
                    .map(l -> PREFIX + l))
                    .collect(Collectors.toList()), mX, mY);
        } else {
            drawTooltip(title, mX, mY);
        }
    }

}
