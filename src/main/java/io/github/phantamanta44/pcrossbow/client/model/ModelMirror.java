package io.github.phantamanta44.pcrossbow.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelMirror extends ModelBase {

    private final ModelRenderer body;

    public ModelMirror() {
        body = new ModelRenderer(this);
        body.addBox(-7F, -2F, -7F, 14, 4, 14, 0F);
    }

    public void render() {
        body.render(0.0625F);
    }

}
