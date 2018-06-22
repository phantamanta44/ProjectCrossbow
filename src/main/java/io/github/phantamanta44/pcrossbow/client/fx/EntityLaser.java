package io.github.phantamanta44.pcrossbow.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityLaser extends Particle {

    private final Vec3d dir;
    private final double length, rInit, rFinal;

    public EntityLaser(World world, Vec3d start, Vec3d end, float initialRadius, double power, double decay, int colour) {
        super(world, start.x, start.y, start.z);
        dir = end.subtract(start);
        length = dir.lengthVector();
        this.rInit = 0.03125 * initialRadius;
        this.rFinal = rInit + decay * length;
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
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        if (particleAge++ >= particleMaxAge) setExpired();
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Entity player = Minecraft.getMinecraft().player;
        GL11.glTranslatef((float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - player.lastTickPosX - (player.posX - player.lastTickPosX) * (double)partialTicks),
                (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - player.lastTickPosY - (player.posY - player.lastTickPosY) * (double)partialTicks),
                (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - player.lastTickPosZ - (player.posZ - player.lastTickPosZ) * (double)partialTicks));
        GL11.glRotated(270 - Math.atan2(dir.z, dir.x) * 180 / Math.PI, 0, 1, 0);
        GL11.glRotated(Math.atan2(dir.y, Math.abs(dir.z)) * 180 / Math.PI, 1, 0, 0);

        Tessellator tess = Tessellator.getInstance();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        
        buffer.pos(rFinal, rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rFinal, -rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rInit, -rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rInit, rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(-rInit, rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rInit, -rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, -rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(rInit, rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rInit, rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rFinal, rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(rFinal, -rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, -rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rInit, -rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rInit, -rInit, 0).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        buffer.pos(rFinal, rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(-rFinal, -rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();
        buffer.pos(rFinal, -rFinal, length).color(particleRed, particleGreen, particleBlue, particleAlpha).endVertex();

        tess.draw();

        GL11.glPopMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
