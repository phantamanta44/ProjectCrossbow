package io.github.phantamanta44.pcrossbow.block.base;

import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.LasingResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public interface ILaserBlock {

    default LasingResult getLasingResult(WorldBlockPos blockPos, Vec3d pos, Vec3d dir,
                                         EnumFacing face, double power, double radius, double fluxAngle) {
        return LasingResult.OBSTRUCT;
    }

    default void lasingFinished(WorldBlockPos pos) {
        // NO-OP
    }

}
