package io.github.phantamanta44.pcrossbow.client.render.tesr;

import io.github.phantamanta44.pcrossbow.client.model.ModelSplitter;
import io.github.phantamanta44.pcrossbow.client.render.tesr.base.TESRFreeRotating;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.tile.TileSplitter;

public class TESRSplitter extends TESRFreeRotating<TileSplitter> {

    private final ModelSplitter model;

    public TESRSplitter() {
        this.model = new ModelSplitter();
    }

    @Override
    protected void doRender(TileSplitter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        bindTexture(ResConst.MODEL_SPLITTER);
        model.render();
    }

}
