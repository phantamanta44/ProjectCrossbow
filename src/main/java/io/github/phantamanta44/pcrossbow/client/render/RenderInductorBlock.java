package io.github.phantamanta44.pcrossbow.client.render;

import io.github.phantamanta44.pcrossbow.client.ClientProxy;
import io.github.phantamanta44.pcrossbow.client.model.ModelInductorBlock;
import io.github.phantamanta44.pcrossbow.client.render.base.TileRenderer;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;

public class RenderInductorBlock extends TileRenderer<TileInductor> {

    public RenderInductorBlock() {
        super(ClientProxy.renderInductorBlock, new ModelInductorBlock());
    }

}
