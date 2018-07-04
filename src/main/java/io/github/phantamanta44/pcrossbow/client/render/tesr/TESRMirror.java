package io.github.phantamanta44.pcrossbow.client.render.tesr;

import io.github.phantamanta44.pcrossbow.client.model.ModelMirror;
import io.github.phantamanta44.pcrossbow.client.render.tesr.base.TESRFreeRotating;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.tile.TileMirror;

public class TESRMirror extends TESRFreeRotating<TileMirror> {

    private final ModelMirror model;

    public TESRMirror() {
        this.model = new ModelMirror();
    }

    @Override
    public void doRender(TileMirror te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        bindTexture(ResConst.MODEL_MIRROR);
        model.render();
    }

}
