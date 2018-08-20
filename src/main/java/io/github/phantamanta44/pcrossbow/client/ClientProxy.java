package io.github.phantamanta44.pcrossbow.client;

import io.github.phantamanta44.libnine.client.event.ClientTickHandler;
import io.github.phantamanta44.libnine.util.tuple.IPair;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.CommonProxy;
import io.github.phantamanta44.pcrossbow.LasingResult;
import io.github.phantamanta44.pcrossbow.client.fx.ParticleLaser;
import io.github.phantamanta44.pcrossbow.util.PhysicsUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ClientProxy extends CommonProxy {

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
        IPair<LasingResult, RayTraceResult> result = traceRay(world, initialPos, finalPos, dir, src, power, initialRadius, fluxAngle);
        if (result != null) {
            RayTraceResult trace = result.getB();
            WorldBlockPos finalBlockPos = new WorldBlockPos(world, trace.getBlockPos());
            double distTravelled = trace.hitVec.distanceTo(initialPos);
            if (result.getA() == LasingResult.OBSTRUCT
                        && ClientTickHandler.getTick() % 3 == 0
                        && PhysicsUtils.calculateIntensity(power, initialRadius, fluxAngle, distTravelled) >= 6000D) {
                Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(finalBlockPos.getPos(), trace.sideHit);
            }
            finalPos = trace.hitVec;
        }
        Minecraft.getMinecraft().effectRenderer.addEffect(
                new ParticleLaser(world, initialPos, finalPos, initialRadius, power, fluxAngle, 0xFF1111));
    }

}
