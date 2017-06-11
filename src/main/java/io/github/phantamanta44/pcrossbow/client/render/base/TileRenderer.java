package io.github.phantamanta44.pcrossbow.client.render.base;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import io.github.phantamanta44.pcrossbow.client.model.base.TileModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class TileRenderer<T extends TileEntity> extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {

    private final int renderId;
    private final TileModel<T> model;

    public TileRenderer(int renderId, TileModel<T> model) {
        this.renderId = renderId;
        this.model = model;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        model.render(0.0625F, this::bindTexture);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        model.render((T)tile, 0.0625F, this::bindTexture);
        GL11.glPopMatrix();
    }

}
