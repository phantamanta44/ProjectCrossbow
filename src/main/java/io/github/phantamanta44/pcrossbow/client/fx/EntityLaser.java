package io.github.phantamanta44.pcrossbow.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityLaser extends EntityFX {

    private final Vec3 dir;
    private final double length, rInit, rFinal;

    public EntityLaser(World world, Vec3 start, Vec3 end, float initialRadius, double power, double decay, int colour) {
        super(world, start.xCoord, start.yCoord, start.zCoord);
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
        if (particleAge++ >= particleMaxAge)
            setDead();
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public void renderParticle(Tessellator tess, float partialTick, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Entity player = Minecraft.getMinecraft().thePlayer;
        GL11.glTranslatef((float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTick - player.lastTickPosX - (player.posX - player.lastTickPosX) * (double)partialTick),
                (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTick - player.lastTickPosY - (player.posY - player.lastTickPosY) * (double)partialTick),
                (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTick - player.lastTickPosZ - (player.posZ - player.lastTickPosZ) * (double)partialTick));
        GL11.glRotated(270 - Math.atan2(dir.zCoord, dir.xCoord) * 180 / Math.PI, 0, 1, 0);
        GL11.glRotated(Math.atan2(dir.yCoord, Math.abs(dir.zCoord)) * 180 / Math.PI, 1, 0, 0);

        tess.startDrawingQuads();
        tess.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);

        tess.addVertex(rFinal, rFinal, length);
        tess.addVertex(rFinal, -rFinal, length);
        tess.addVertex(rInit, -rInit, 0);
        tess.addVertex(rInit, rInit, 0);

        tess.addVertex(-rInit, rInit, 0);
        tess.addVertex(-rInit, -rInit, 0);
        tess.addVertex(-rFinal, -rFinal, length);
        tess.addVertex(-rFinal, rFinal, length);

        tess.addVertex(rInit, rInit, 0);
        tess.addVertex(-rInit, rInit, 0);
        tess.addVertex(-rFinal, rFinal, length);
        tess.addVertex(rFinal, rFinal, length);

        tess.addVertex(rFinal, -rFinal, length);
        tess.addVertex(-rFinal, -rFinal, length);
        tess.addVertex(-rInit, -rInit, 0);
        tess.addVertex(rInit, -rInit, 0);

        tess.addVertex(rFinal, rFinal, length);
        tess.addVertex(-rFinal, rFinal, length);
        tess.addVertex(-rFinal, -rFinal, length);
        tess.addVertex(rFinal, -rFinal, length);

        tess.draw();

        GL11.glPopMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
