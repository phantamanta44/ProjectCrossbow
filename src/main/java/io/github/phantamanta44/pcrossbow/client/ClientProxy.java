package io.github.phantamanta44.pcrossbow.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import io.github.phantamanta44.pcrossbow.CommonProxy;
import io.github.phantamanta44.pcrossbow.client.fx.EntityLaser;
import io.github.phantamanta44.pcrossbow.client.handler.ClientTickHandler;
import io.github.phantamanta44.pcrossbow.client.render.RenderLaserBlock;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class ClientProxy extends CommonProxy {

    public static int renderLaserBlock;

    @Override
    public void onInit() {
        super.onInit();
        FMLCommonHandler.instance().bus().register(new ClientTickHandler());
        renderLaserBlock = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new RenderLaserBlock());
    }

    @Override
    public void doLasing(World world, float x, float y, float z, Vec3 dir, float initialIntensity, float initialRadius, float radiusRate) {
        if (world.isRemote) {
            dir = dir.normalize();
            Vec3 initialPos = Vec3.createVectorHelper(x + dir.xCoord * 0.5D, y + dir.yCoord * 0.5D, z + dir.zCoord * 0.5D);
            double range = Math.min(Math.sqrt(initialIntensity / (Math.PI * INTENSITY_CUTOFF)) / radiusRate, 128);
            Vec3 finalPos = initialPos.addVector(range * dir.xCoord, range * dir.yCoord, range * dir.zCoord);
            Vec3 traceStart = initialPos.addVector(dir.xCoord < 0 ? -1 : 0, dir.yCoord < 0 ? -1 : 0, dir.zCoord < 0 ? -1 : 0);
            MovingObjectPosition trace = traceRay(
                    world, traceStart, finalPos, b -> b.isOpaqueCube() || b instanceof IFluidBlock || b instanceof BlockLiquid, true);
            if (trace != null)
                finalPos = trace.hitVec;
            Minecraft.getMinecraft().effectRenderer.addEffect(
                    new EntityLaser(world, initialPos, finalPos, initialRadius, initialIntensity, radiusRate, 0xDD0000));
        } else {
            super.doLasing(world, x, y, z, dir, initialIntensity, initialRadius, radiusRate);
        }
    }

    @Override
    public void breakBlockNaturally(World world, int x, int y, int z) {
        super.breakBlockNaturally(world, x, y, z); // I guess this works?
    }

}
