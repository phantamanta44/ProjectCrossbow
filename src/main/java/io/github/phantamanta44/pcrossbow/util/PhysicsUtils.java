package io.github.phantamanta44.pcrossbow.util;

public class PhysicsUtils {

    public static final double C2 = 1D / Math.sqrt(2D);
    public static final double C3 = 1D / Math.sqrt(3D);

    public static double calculateRange(double power, double initialRadius, double fluxAngle, double maxIntensity) {
        return (Math.sqrt(power / (Math.PI * maxIntensity)) - initialRadius) / Math.tan(fluxAngle);
    }

    public static double calculateRadius(double initialRadius, double fluxAngle, double distance) {
        return initialRadius + Math.tan(fluxAngle) * distance;
    }

    public static double calculateIntensity(double power, double radius) {
        return power / (Math.PI * radius * radius);
    }

    public static double calculateIntensity(double power, double initialRadius, double fluxAngle, double distance) {
        return calculateIntensity(power, calculateRadius(initialRadius, fluxAngle, distance));
    }

}
