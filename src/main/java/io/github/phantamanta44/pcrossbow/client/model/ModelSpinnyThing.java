package io.github.phantamanta44.pcrossbow.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelSpinnyThing extends ModelBase {

    private final ModelRenderer thing;

    public ModelSpinnyThing(int radius, int u, int v, int texW, int texH) {
        this(radius, u, v);
        thing.textureWidth = texW;
        thing.textureHeight = texH;
    }

    public ModelSpinnyThing(int radius, int u, int v) {
        thing = new ModelRenderer(this, u, v);
        thing.addBox(-radius, -radius, -radius, 2 * radius, 2 * radius, 2 * radius, 0F);
    }

    public ModelSpinnyThing(int radius) {
        this(radius, 0, 0);
    }

    public void render(float angle) {
        thing.rotateAngleX = angle % 360F;
        thing.rotateAngleY = (-0.8F * angle) % 360F;
        thing.renderWithRotation(0.0625F);
    }

}
