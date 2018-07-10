package io.github.phantamanta44.pcrossbow.client.render.tesr;

import io.github.phantamanta44.pcrossbow.client.model.ModelMirror;
import io.github.phantamanta44.pcrossbow.client.render.tesr.base.TESRFreeRotating;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.tile.TileMirror;
import net.minecraft.client.renderer.GlStateManager;

public class TESRMirror extends TESRFreeRotating<TileMirror> {

    private final ModelMirror model;

    public TESRMirror() {
        this.model = new ModelMirror();
    }

    @Override
    public void doRender(TileMirror te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        bindTexture(ResConst.MODEL_MIRROR);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        model.render();
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            model.render();
            GlStateManager.disableBlend();
        }
    }

}
