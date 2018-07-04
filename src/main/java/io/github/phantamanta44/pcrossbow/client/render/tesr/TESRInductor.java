package io.github.phantamanta44.pcrossbow.client.render.tesr;

import io.github.phantamanta44.pcrossbow.client.model.ModelSpinnyThing;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class TESRInductor extends TileEntitySpecialRenderer<TileInductor> {

    private final ModelSpinnyThing core;

    public TESRInductor() {
        this.core = new ModelSpinnyThing(4);
    }

    @Override
    public void render(TileInductor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (MinecraftForgeClient.getRenderPass() != 1) return;
        if (te.getEnergyStored() > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            GlStateManager.color(1F, 0F, 0F, Math.min(te.getEnergyStored() / 2400F, 1F));
            core.render((getWorld().getTotalWorldTime() + partialTicks) / 4F);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }
    }

}
