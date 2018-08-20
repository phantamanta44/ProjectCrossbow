package io.github.phantamanta44.pcrossbow.api.capability;

import io.github.phantamanta44.libnine.InitMe;
import io.github.phantamanta44.libnine.capability.StatelessCapabilitySerializer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

@SuppressWarnings("NullableProblems")
public class XbowCaps {

    @CapabilityInject(ILaserConsumer.class)
    public static Capability<ILaserConsumer> LASER_CONSUMER;
    @CapabilityInject(IVectorDirectional.class)
    public static Capability<IVectorDirectional> VECTOR_DIR;
    @CapabilityInject(ILaserPowerModifier.class)
    public static Capability<ILaserPowerModifier> LASER_MOD_POWER;
    @CapabilityInject(ILaserRadiusModifier.class)
    public static Capability<ILaserRadiusModifier> LASER_MOD_RADIUS;
    @CapabilityInject(ILaserFluxAngleModifier.class)
    public static Capability<ILaserFluxAngleModifier> LASER_MOD_FLUX_ANGLE;

    @InitMe
    public static void init() {
        CapabilityManager.INSTANCE.register(
                ILaserConsumer.class, new StatelessCapabilitySerializer<>(), ILaserConsumer.Default::new);
        CapabilityManager.INSTANCE.register(
                IVectorDirectional.class, new StatelessCapabilitySerializer<>(), IVectorDirectional.Default::new);
        CapabilityManager.INSTANCE.register(
                ILaserPowerModifier.class, new StatelessCapabilitySerializer<>(), ILaserPowerModifier.Default::new);
        CapabilityManager.INSTANCE.register(
                ILaserRadiusModifier.class, new StatelessCapabilitySerializer<>(), ILaserRadiusModifier.Default::new);
        CapabilityManager.INSTANCE.register(
                ILaserFluxAngleModifier.class, new StatelessCapabilitySerializer<>(), ILaserFluxAngleModifier.Default::new);
    }

}
