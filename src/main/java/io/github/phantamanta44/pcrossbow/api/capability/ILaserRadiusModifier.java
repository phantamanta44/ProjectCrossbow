package io.github.phantamanta44.pcrossbow.api.capability;

public interface ILaserRadiusModifier {

    double modifyRadius(double radius);

    class Default implements ILaserRadiusModifier {

        @Override
        public double modifyRadius(double radius) {
            return radius;
        }

    }

}
