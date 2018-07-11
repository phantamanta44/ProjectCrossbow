package io.github.phantamanta44.pcrossbow.api.capability;

import net.minecraft.util.math.Vec3d;

public interface ILaserConsumer {

    void consumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle);

    default boolean canConsumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        return true;
    }

    default Vec3d getBeamEndpoint(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
        return pos;
    }

    class Default implements ILaserConsumer {

        Default() {
            // NO-OP
        }

        @Override
        public void consumeBeam(Vec3d pos, Vec3d dir, double power, double radius, double fluxAngle) {
            // NO-OP
        }

    }

}
