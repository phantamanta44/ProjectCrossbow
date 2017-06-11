package io.github.phantamanta44.pcrossbow.api;

import net.minecraft.util.Vec3;

public interface ILaserConsumer {

    void consumeBeam(Vec3 direction, float power, float radius);

}
