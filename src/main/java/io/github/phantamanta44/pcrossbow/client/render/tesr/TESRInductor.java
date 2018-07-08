package io.github.phantamanta44.pcrossbow.client.render.tesr;

import io.github.phantamanta44.pcrossbow.client.model.ModelSpinnyThing;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
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
            float colourIntensity = MathHelper.sqrt(Math.min(te.getEnergyStored() / 10000F, 1F));
            GlStateManager.color(colourIntensity, 0F, 0F, colourIntensity);
            core.render((getWorld().getTotalWorldTime() + partialTicks) * Math.min(te.getEnergyStored(), 200000) / 400000F);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
        }
    }

}
