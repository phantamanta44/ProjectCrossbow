package io.github.phantamanta44.pcrossbow.api.capability;

import io.github.phantamanta44.pcrossbow.LasingResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public interface ILaserConsumer {

    LasingResult consumeBeam(Vec3d pos, Vec3d dir, EnumFacing face, double power, double radius, double fluxAngle);

    default Vec3d getBeamEndpoint(Vec3d pos, Vec3d dir, EnumFacing face, double power, double radius, double fluxAngle) {
        return pos;
    }

    default void onLasingFinished() {
        // NO-OP
    }

    class Default implements ILaserConsumer {

        Default() {
            // NO-OP
        }

        @Override
        public LasingResult consumeBeam(Vec3d pos, Vec3d dir, EnumFacing face, double power, double radius, double fluxAngle) {
            return LasingResult.CONSUME;
        }

    }

}
