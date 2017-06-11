package io.github.phantamanta44.pcrossbow.client.model;

import io.github.phantamanta44.pcrossbow.client.model.base.TileModel;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

public class ModelInductorBlock extends TileModel<TileInductor> {

    private final ModelRenderer edge, vertex, glass, core;

    public ModelInductorBlock() {
        edge = new ModelRenderer(this);
        edge.setRotationPoint(0F, 0F, 0F);
        edge.setTextureSize(64, 64);
        edge.addBox(-8F, -8F, -5F, 3, 3, 10);
        edge.mirror = true;

        vertex = new ModelRenderer(this);
        vertex.setRotationPoint(0F, 0F, 0F);
        vertex.setTextureSize(64, 64);
        vertex.addBox(-8F, -8F, -8F, 3, 3, 3);
        vertex.mirror = true;

        glass = new ModelRenderer(this);
        glass.setRotationPoint(0F, 0F, 0F);
        glass.setTextureSize(64, 64);
        glass.setTextureOffset(0, 13);
        glass.addBox(-6F, -6F, -6F, 12, 12, 12);
        glass.mirror = true;

        core = new ModelRenderer(this);
        core.setRotationPoint(0F, 0F, 0F);
        core.setTextureSize(64, 64);
        core.setTextureOffset(16, 0);
        core.addBox(-2F, -2F, -2F, 4, 4, 4);
        core.mirror = true;
    }

    @Override
    public void render(TileInductor tile, float scale, Consumer<ResourceLocation> bindTexture) {
        bindTexture.accept(ResConst.MODEL_INDUCTOR);
        GL11.glDepthMask(true);
        switch (MinecraftForgeClient.getRenderPass()) {
            case 0:
                renderFrame(scale);
                break;
            case 1:
                renderCore(scale);
                break;
        }
        GL11.glDepthMask(false);
    }

    @Override
    public void render(float scale, Consumer<ResourceLocation> bindTexture) {
        bindTexture.accept(ResConst.MODEL_INDUCTOR);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        renderCore(scale);
        renderFrame(scale);
    }

    private void renderFrame(float scale) {
        for (int yRot = 0; yRot < 4; yRot++) {
            edge.rotateAngleY = vertex.rotateAngleY = yRot * 1.5707963267948966F;
            for (int xRot = 0; xRot < 2; xRot++) {
                edge.rotateAngleX = vertex.rotateAngleX = xRot * 1.5707963267948966F;
                edge.render(scale);
                vertex.render(scale);
            }
            edge.rotateAngleX = 3.1415926535897932F;
            edge.render(scale);
        }
    }

    private void renderCore(float scale) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        core.render(scale);
        glass.render(scale);
        GL11.glDisable(GL11.GL_BLEND);
    }

}
