package io.github.phantamanta44.pcrossbow.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import io.github.phantamanta44.pcrossbow.client.ClientProxy;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderLaserBlock implements ISimpleBlockRenderingHandler {

    // Adapted from net.minecraft.client.renderer.RenderBlocks#renderBlockAsItem(Block, int, float)
    @Override
    public void renderInventoryBlock(Block block, int meta, int modelId, RenderBlocks renderer) {
        renderer.uvRotateSouth = 3;
        renderer.uvRotateTop = 1;
        renderer.uvRotateBottom = 2;
        Tessellator tessellator = Tessellator.instance;
        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        renderer.uvRotateTop = renderer.uvRotateBottom = renderer.uvRotateSouth = 0;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileLaser tile = (TileLaser)world.getTileEntity(x, y, z);
        if (tile != null) {
            switch (tile.getFacing()) {
                case 0:
                    renderer.uvRotateNorth = renderer.uvRotateWest = 1;
                    renderer.uvRotateSouth = renderer.uvRotateEast = 2;
                    break;
                case 1:
                    renderer.uvRotateNorth = renderer.uvRotateWest = 2;
                    renderer.uvRotateSouth = renderer.uvRotateEast = 1;
                    break;
                case 2:
                    renderer.uvRotateNorth = 3;
                    renderer.uvRotateTop = 2;
                    renderer.uvRotateBottom = 1;
                    break;
                case 3:
                    renderer.uvRotateSouth = 3;
                    renderer.uvRotateTop = 1;
                    renderer.uvRotateBottom = 2;
                    break;
                case 4:
                    renderer.uvRotateBottom = renderer.uvRotateTop = renderer.uvRotateWest = 3;
                    break;
                case 5:
                    renderer.uvRotateEast = 3;
                    break;
            }
            renderer.renderStandardBlock(block, x, y, z);
            renderer.uvRotateTop = renderer.uvRotateBottom = renderer.uvRotateNorth =
                    renderer.uvRotateEast = renderer.uvRotateSouth = renderer.uvRotateWest = 0;
        } else {
            renderer.renderStandardBlock(block, x, y, z);
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.renderLaserBlock;
    }

}
