package io.github.phantamanta44.pcrossbow.util;

import io.github.phantamanta44.libnine.util.render.TextureRegion;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;

public class SlotType {

    public enum BasicIO implements IType<BasicIO> {

        NONE(ResConst.COMP_SLOTS_NONE, LangConst.INFO_SLOT_NONE),
        INPUT(ResConst.COMP_SLOTS_INPUT, LangConst.INFO_SLOT_INPUT),
        OUTPUT(ResConst.COMP_SLOTS_OUTPUT, LangConst.INFO_SLOT_OUTPUT);

        private final TextureRegion tex;
        private final String l10nKey;

        BasicIO(TextureRegion tex, String l10nKey) {
            this.tex = tex;
            this.l10nKey = l10nKey;
        }

        @Override
        public TextureRegion getTexture() {
            return tex;
        }

        @Override
        public String getTooltip() {
            return LangConst.get(l10nKey);
        }

        @Override
        public BasicIO prev() {
            return values()[(ordinal() + values().length - 1) % values().length];
        }

        @Override
        public BasicIO next() {
            return values()[(ordinal() + 1) % values().length];
        }

    }

    public interface IType<T extends IType<T>> {

        TextureRegion getTexture();

        String getTooltip();

        T prev();

        T next();

    }

}
