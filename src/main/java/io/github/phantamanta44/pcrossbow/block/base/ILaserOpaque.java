package io.github.phantamanta44.pcrossbow.block.base;

import net.minecraft.util.math.Vec3d;

public interface ILaserOpaque {

    default boolean isOpaqueToLaser(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        return true;
    }

}
