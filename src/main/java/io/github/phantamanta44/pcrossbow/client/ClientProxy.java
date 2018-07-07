package io.github.phantamanta44.pcrossbow.client;

import io.github.phantamanta44.libnine.client.event.ClientTickHandler;
import io.github.phantamanta44.libnine.util.function.ITriPredicate;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.CommonProxy;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.client.fx.ParticleLaser;
import io.github.phantamanta44.pcrossbow.client.gui.XbowGuis;
import io.github.phantamanta44.pcrossbow.util.PhysicsUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

public class ClientProxy extends CommonProxy {

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        super.onPreInit(event);
        XbowGuis.initClient();
    }

    @Override
    public void doLasing0(World world, Vec3d initialPos, Vec3d unnormDir,
                         double power, double initialRadius, double fluxAngle, @Nullable WorldBlockPos src) {
        if (!world.isRemote) {
            super.doLasing0(world, initialPos, unnormDir, power, initialRadius, fluxAngle, src);
            return;
        }
        Vec3d dir = unnormDir.normalize();
        double range = Math.min(PhysicsUtils.calculateRange(power, initialRadius, fluxAngle, INTENSITY_CUTOFF), 128);
        Vec3d finalPos = initialPos.add(dir.scale(range));
        ITriPredicate<IBlockState, WorldBlockPos, RayTraceResult> pred = (b, p, t) ->
                b.isOpaqueCube() || (t != null && getLaserConsumer(p, dir, power,
                        PhysicsUtils.calculateRadius(initialRadius, fluxAngle, t.hitVec.distanceTo(initialPos)), fluxAngle, t) != null);
        if (src != null) pred = pred.pre((b, p, t) -> !p.equals(src));
        RayTraceResult trace = traceRay(world, initialPos, finalPos, pred);
        if (trace != null) {
            WorldBlockPos finalBlockPos = new WorldBlockPos(world, trace.getBlockPos());
            finalPos = trace.hitVec;
            double distTravelled = trace.hitVec.distanceTo(initialPos);
            double radius = PhysicsUtils.calculateRadius(initialRadius, fluxAngle, distTravelled);
            ILaserConsumer consumer = getLaserConsumer(finalBlockPos, dir, power, radius, fluxAngle, trace);
            if (consumer != null) {
                consumer.consumeBeam(trace.hitVec, dir, power, radius, fluxAngle);
            } else if (ClientTickHandler.getTick() % 3 == 0
                        && PhysicsUtils.calculateIntensity(power, initialRadius, fluxAngle, distTravelled) >= 6000D) {
                Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(finalBlockPos.getPos(), trace.sideHit);
            }
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new ParticleLaser(world, initialPos, finalPos, initialRadius, power, fluxAngle, 0xDD0000));
    }

}
