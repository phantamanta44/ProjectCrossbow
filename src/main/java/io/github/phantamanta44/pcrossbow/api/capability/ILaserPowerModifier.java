package io.github.phantamanta44.pcrossbow.api.capability;

public interface ILaserPowerModifier {

    double modifyPower(double power);

    class Default implements ILaserPowerModifier {

        @Override
        public double modifyPower(double power) {
            return power;
        }

    }

}
