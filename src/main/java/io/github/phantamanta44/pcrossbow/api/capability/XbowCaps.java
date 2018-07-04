package io.github.phantamanta44.pcrossbow.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class XbowCaps {

    @CapabilityInject(ILaserConsumer.class)
    public static Capability<ILaserConsumer> LASER_CONSUMER;
    @CapabilityInject(IVectorDirectional.class)
    public static Capability<IVectorDirectional> VECTOR_DIR;

    public static void init() {
        CapabilityManager.INSTANCE.register(
                ILaserConsumer.class, new ILaserConsumer.Serializer(), ILaserConsumer.Default::new);
        CapabilityManager.INSTANCE.register(
                IVectorDirectional.class, new IVectorDirectional.Serializer(), IVectorDirectional.Default::new);
    }

}
