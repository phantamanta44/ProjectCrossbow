package io.github.phantamanta44.pcrossbow.client.model;

import io.github.phantamanta44.libnine.util.math.MathUtils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.MinecraftForgeClient;

public class ModelSplitter extends ModelBase {

    private final ModelRenderer glass;
    private final ModelRenderer longX;
    private final ModelRenderer shortZ;

    public ModelSplitter() {
        glass = new ModelRenderer(this);
        glass.addBox(-6F, -2F, -6F, 12, 4, 12);
        longX = new ModelRenderer(this, 0, 16);
        longX.addBox(-7F, -2F, -7F, 14, 4, 1);
        shortZ = new ModelRenderer(this, 30, 16);
        shortZ.addBox(-7F, -2F, -6F, 1, 4, 12);
    }

    public void render() {
        if (MinecraftForgeClient.getRenderPass() == 0) {
            longX.rotateAngleY = 0F;
            longX.render(0.0625F);
            longX.rotateAngleY = MathUtils.PI_F;
            longX.render(0.0625F);
            shortZ.rotateAngleY = 0F;
            shortZ.render(0.0625F);
            shortZ.rotateAngleY = MathUtils.PI_F;
            shortZ.render(0.0625F);
        } else {
            GlStateManager.enableBlend();
            glass.render(0.0625F);
            GlStateManager.disableBlend();
        }
    }

}
