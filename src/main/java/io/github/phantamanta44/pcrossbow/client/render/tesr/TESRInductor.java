package io.github.phantamanta44.pcrossbow.client.render.tesr;

import io.github.phantamanta44.libnine.util.math.MathUtils;
import io.github.phantamanta44.libnine.util.render.RenderUtils;
import io.github.phantamanta44.pcrossbow.client.model.ModelSpinnyThing;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL14;

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
            RenderUtils.enableFullBrightness();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            GlStateManager.color(1F, 0.0625F, 0.0625F,
                    Math.max(MathHelper.sqrt(Math.min(te.getEnergyStored() / 75000F, 1F)), 0.11F));
            core.render((getWorld().getTotalWorldTime() + partialTicks + (te.getPos().hashCode() % MathUtils.PI_F))
                    * Math.min(te.getEnergyStored(), 200000) / 300000F);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            RenderUtils.restoreLightmap();
            GlStateManager.disableBlend();
        }
    }

}
