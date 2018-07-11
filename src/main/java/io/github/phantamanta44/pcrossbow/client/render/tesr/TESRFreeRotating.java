package io.github.phantamanta44.pcrossbow.client.render.tesr;

import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.libnine.util.math.MathUtils;
import io.github.phantamanta44.pcrossbow.api.capability.IVectorDirectional;
import io.github.phantamanta44.pcrossbow.client.model.IModelMirror;
import io.github.phantamanta44.pcrossbow.client.model.ModelMirror;
import io.github.phantamanta44.pcrossbow.client.model.ModelMirrorTransparent;
import io.github.phantamanta44.pcrossbow.tile.TileOneWay;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;

public class TESRFreeRotating<T extends TileEntity & IVectorDirectional> extends TileEntitySpecialRenderer<T> {

    private final ResourceLocation texture;
    private final IModelMirror model;

    public TESRFreeRotating(ResourceLocation texture, boolean transparent) {
        this.texture = texture;
        this.model = transparent ? new ModelMirrorTransparent() : new ModelMirror();
    }

    @Override
    public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        Vec3d ortho = te.getNorm().crossProduct(LinAlUtils.Y_POS);
        if (ortho.lengthSquared() > 0) {
            ortho = ortho.normalize();
            GlStateManager.rotate((float)Math.acos(LinAlUtils.Y_POS.dotProduct(te.getNorm())) * -MathUtils.R2D_F,
                    (float)ortho.x, (float)ortho.y, (float)ortho.z);
        }
        bindTexture(texture);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        model.render();
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            model.render();
            GlStateManager.disableBlend();
        }
        GlStateManager.popMatrix();
    }

}
