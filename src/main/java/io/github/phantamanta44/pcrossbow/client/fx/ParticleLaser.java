package io.github.phantamanta44.pcrossbow.client.fx;

import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import io.github.phantamanta44.pcrossbow.util.PhysicsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleLaser extends Particle {

    private final Vec3d dir;
    private final double length, rInit, rFinal;

    public ParticleLaser(World world, Vec3d start, Vec3d end, double initialRadius, double power, double fluxAngle, int colour) {
        super(world, start.x, start.y, start.z);
        this.dir = end.subtract(start).normalize();
        this.length = start.distanceTo(end);
        this.rInit = initialRadius;
        this.rFinal = PhysicsUtils.calculateRadius(initialRadius, fluxAngle, length);
        this.particleRed = ((colour & 0xFF0000) >> 16) / 255F;
        this.particleGreen = ((colour & 0x0000FF) >> 8) / 255F;
        this.particleBlue = (colour & 0x0000FF) / 255F;
        this.particleAlpha = Math.min(Math.max(Double.valueOf(power).floatValue() / 300F, 0.102F), 0.2F);
        this.particleGravity = 0;
        this.motionX = this.motionY = this.motionZ = 0;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.particleMaxAge = 5;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public boolean shouldDisableDepth() {
        return false;
    }

    @Override
    public int getBrightnessForRender(float p_189214_1_) {
        return 15;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) setExpired();
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        GL11.glPushMatrix();
        GlStateManager.disableFog();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Entity player = Minecraft.getMinecraft().player;
        GL11.glTranslatef((float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - player.lastTickPosX - (player.posX - player.lastTickPosX) * (double)partialTicks),
                (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - player.lastTickPosY - (player.posY - player.lastTickPosY) * (double)partialTicks),
                (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - player.lastTickPosZ - (player.posZ - player.lastTickPosZ) * (double)partialTicks));
        Vec3d ortho = dir.crossProduct(LinAlUtils.Y_POS);
        ortho = ortho.lengthSquared() == 0 ? LinAlUtils.X_POS : ortho.normalize();
        GlStateManager.rotate((float)Math.acos(LinAlUtils.Y_POS.dotProduct(dir)) * -57.3F,
                (float)ortho.x, (float)ortho.y, (float)ortho.z);

        Tessellator tess = Tessellator.getInstance();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        
        buffer.pos(rFinal, length, rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rFinal, length, -rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rInit, 0, -rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rInit, 0, rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(-rInit, 0, rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rInit, 0, -rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, length, -rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, length, rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(rInit, 0, rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rInit, 0, rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, length, rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rFinal, length, rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(rFinal, length, -rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, length, -rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rInit, 0, -rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rInit, 0, -rInit).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(rFinal, length, rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, length, rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, length, -rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rFinal, length, -rFinal).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        tess.draw();

        GL11.glPopMatrix();
        GlStateManager.enableFog();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
