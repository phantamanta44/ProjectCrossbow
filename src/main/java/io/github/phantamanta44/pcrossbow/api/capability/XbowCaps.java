package io.github.phantamanta44.pcrossbow.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class XbowCaps {

    @CapabilityInject(ILaserConsumer.class)
    public static Capability<ILaserConsumer> LASER_CONSUMER;

    public static void init() {
        CapabilityManager.INSTANCE.register(
                ILaserConsumer.class, new ILaserConsumer.Serializer(), ILaserConsumer.Default::new);
    }

}
