package io.github.phantamanta44.pcrossbow.client.render;

import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.pcrossbow.client.model.ModelMirror;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.tile.TileMirror;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class TESRMirror extends TileEntitySpecialRenderer<TileMirror> {

    private final ModelMirror model;

    public TESRMirror() {
        this.model = new ModelMirror();
    }

    @Override
    public void render(TileMirror te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        Vec3d reference = LinAlUtils.project(te.getNorm(), LinAlUtils.Y_POS).y >= 0
                ? LinAlUtils.Y_POS : LinAlUtils.Y_NEG;
        Vec3d ortho = te.getNorm().crossProduct(reference);
        if (ortho.lengthSquared() > 0) {
            ortho = ortho.normalize();
            GlStateManager.rotate((float)Math.acos(reference.dotProduct(te.getNorm())) * -57.296F,
                    (float)ortho.x, (float)ortho.y, (float)ortho.z);
        }
        bindTexture(ResConst.MODEL_MIRROR);
        model.render();
        GlStateManager.popMatrix();

        Tessellator tess = Tessellator.getInstance();
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(4F);
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);

        GlStateManager.color(0F, 0F, 1F, 1F);
        tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(0, 0, 0).endVertex();
        tess.getBuffer().pos(te.getNorm().x * 1.5D, te.getNorm().y * 1.5D, te.getNorm().z * 1.5D).endVertex();
        tess.draw();

        Vec3d in = new Vec3d(0, 0, 1);
        Vec3d out = LinAlUtils.reflect2D(in, te.getNorm());

        GlStateManager.color(0F, 1F, 0F, 1F);
        tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(0, 0, 0).endVertex();
        tess.getBuffer().pos(in.x * 1.5D, in.y * 1.5D, in.z * 1.5D).endVertex();
        tess.draw();

        GlStateManager.color(1F, 0F, 1F, 1F);
        tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(0, 0, 0).endVertex();
        tess.getBuffer().pos(out.x * 1.5D, out.y * 1.5D, out.z * 1.5D).endVertex();
        tess.draw();

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

}
