package io.github.phantamanta44.pcrossbow.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

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

    public static boolean intersectsLine(AxisAlignedBB prism, Vec3d lineMin, Vec3d lineMax) {
        double x1 = lineMin.x, x2 = lineMax.x, dx = x2 - x1;
        double y1 = lineMin.y, y2 = lineMax.y, dy = y2 - y1;
        double z1 = lineMin.z, z2 = lineMax.z, dz = z2 - z1;
        double dydx = dy / dx, dzdx = dz / dx, dzdy = dz / dy;
        double inter1, inter2;
        // project to xy plane
        inter1 = (prism.minX - x1) * dydx + y1;
        inter2 = (prism.maxX - x1) * dydx + y1;
        if (Math.min(inter1, inter2) > prism.maxY || Math.max(inter1, inter2) < prism.minY) return false;
        // project to xz plane
        inter1 = (prism.minX - x1) * dzdx + z1;
        inter2 = (prism.maxX - x1) * dzdx + z1;
        if (Math.min(inter1, inter2) > prism.maxZ || Math.max(inter1, inter2) < prism.minZ) return false;
        // project to yz plane
        inter1 = (prism.minY - y1) * dzdy + z1;
        inter2 = (prism.maxY - y1) * dzdy + z1;
        return !(Math.min(inter1, inter2) > prism.maxZ || Math.max(inter1, inter2) < prism.minZ);
    }

}
