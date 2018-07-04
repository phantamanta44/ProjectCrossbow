package io.github.phantamanta44.pcrossbow.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class MiscRenders {
    
    public static void renderVector(Vec3d vec, double x, double y, double z, float r, float g, float b) {
        Tessellator tess = Tessellator.getInstance();
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(4F);
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);

        GlStateManager.color(r, g, b, 1F);
        tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(0, 0, 0).endVertex();
        tess.getBuffer().pos(vec.x, vec.y, vec.z).endVertex();
        tess.draw();

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
    
}
