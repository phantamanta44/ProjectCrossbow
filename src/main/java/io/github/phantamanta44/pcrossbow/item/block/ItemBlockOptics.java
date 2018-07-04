package io.github.phantamanta44.pcrossbow.item.block;

import io.github.phantamanta44.libnine.block.L9BlockStated;
import io.github.phantamanta44.libnine.block.state.VirtualState;
import io.github.phantamanta44.libnine.item.L9ItemBlockStated;
import io.github.phantamanta44.pcrossbow.block.base.XbowProps;

public class ItemBlockOptics extends L9ItemBlockStated {

    public ItemBlockOptics(L9BlockStated block) {
        super(block);
    }

    @Override
    public String getModelName(VirtualState state) {
        return state.get(XbowProps.OPTICS_TYPE).getModelName();
    }

}
