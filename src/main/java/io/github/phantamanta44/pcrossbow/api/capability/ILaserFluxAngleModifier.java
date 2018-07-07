package io.github.phantamanta44.pcrossbow.api.capability;

public interface ILaserFluxAngleModifier {

    double modifyFluxAngle(double fluxAngle);

    class Default implements ILaserFluxAngleModifier {

        @Override
        public double modifyFluxAngle(double fluxAngle) {
            return fluxAngle;
        }

    }

}
