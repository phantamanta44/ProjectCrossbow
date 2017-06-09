package io.github.phantamanta44.pcrossbow.util;

public class NumeralRange {

    public static NumeralRange between(double lower, double upper) {
        return lower > upper ? null : new NumeralRange(lower, upper);
    }

    private double lower, upper;

    public NumeralRange(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean intersect(NumeralRange o) {
        double newLower = Math.max(o.lower, lower), newUpper = Math.min(o.upper, upper);
        if (lower > upper)
            return false;
        this.lower = newLower;
        this.upper = newUpper;
        return true;
    }

    public boolean exists() {
        return lower <= upper;
    }

    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    public double getUpper() {
        return upper;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

}
