package io.github.phantamanta44.pcrossbow.client.render.tesr.base;

import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.libnine.util.math.MathUtils;
import io.github.phantamanta44.pcrossbow.api.capability.IVectorDirectional;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;

public abstract class TESRFreeRotating<T extends TileEntity & IVectorDirectional> extends TileEntitySpecialRenderer<T> {

    @Override
    public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        Vec3d reference = LinAlUtils.project(te.getNorm(), LinAlUtils.Y_POS).y >= 0
                ? LinAlUtils.Y_POS : LinAlUtils.Y_NEG;
        Vec3d ortho = te.getNorm().crossProduct(reference);
        if (ortho.lengthSquared() > 0) {
            ortho = ortho.normalize();
            GlStateManager.rotate((float)Math.acos(reference.dotProduct(te.getNorm())) * -MathUtils.R2D_F,
                    (float)ortho.x, (float)ortho.y, (float)ortho.z);
        }
        doRender(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.popMatrix();
    }

    protected abstract void doRender(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha);

}
