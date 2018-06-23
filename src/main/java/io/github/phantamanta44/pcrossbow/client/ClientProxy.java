package io.github.phantamanta44.pcrossbow.client;

import io.github.phantamanta44.libnine.util.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.CommonProxy;
import io.github.phantamanta44.pcrossbow.client.fx.EntityLaser;
import io.github.phantamanta44.pcrossbow.client.handler.ClientTickHandler;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
    }

    @Override
    public void doLasing(World world, Vec3d pos, Vec3d dir, float power, float initialRadius, float radiusRate) {
        if (!world.isRemote) {
            super.doLasing(world, pos, dir, power, initialRadius, radiusRate);
            return;
        }
        dir = dir.normalize();
        Vec3d initialPos = pos.add(dir.scale(0.5D));
        double range = Math.min(Math.sqrt(power / (Math.PI * INTENSITY_CUTOFF)) / radiusRate, 128);
        Vec3d finalPos = initialPos.add(dir.scale(range));
        Vec3d traceStart = initialPos.addVector(dir.x < 0 ? -1 : 0, dir.y < 0 ? -1 : 0, dir.z < 0 ? -1 : 0);
        EnumFacing collisionFace = EnumFacing.getFacingFromVector((float)-dir.x, (float)-dir.y, (float)-dir.z);
        RayTraceResult trace = traceRay(
                world, traceStart, finalPos,
                (b, p) -> b.isOpaqueCube() || isLaserConsumer(p, collisionFace)
                        || b instanceof IFluidBlock || b instanceof BlockLiquid, true);
        if (trace != null) {
            WorldBlockPos finalBlockPos = new WorldBlockPos(world, trace.getBlockPos());
            finalPos = isLaserConsumer(finalBlockPos, collisionFace)
                    ? trace.hitVec.add(dir.scale(0.5D)) : trace.hitVec;
            if (ClientTickHandler.getTick() % 4 == 0) {
                Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(finalBlockPos, finalBlockPos.getBlockState());
            }
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new EntityLaser(world, initialPos, finalPos, initialRadius, power, radiusRate, 0xDD0000));
    }

}
