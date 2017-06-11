package io.github.phantamanta44.pcrossbow.client.model.base;

import net.minecraft.client.model.ModelBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public abstract class TileModel<T extends TileEntity> extends ModelBase {

    public abstract void render(T tile, float scale, Consumer<ResourceLocation> bindTexture);

    public abstract void render(float scale, Consumer<ResourceLocation> bindTexture);

}
