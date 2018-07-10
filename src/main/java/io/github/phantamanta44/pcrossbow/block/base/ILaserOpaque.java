package io.github.phantamanta44.pcrossbow.block.base;

import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import net.minecraft.util.math.Vec3d;

public interface ILaserOpaque {

    default boolean isOpaqueToLaser(WorldBlockPos blockPos, Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        return true;
    }

}
