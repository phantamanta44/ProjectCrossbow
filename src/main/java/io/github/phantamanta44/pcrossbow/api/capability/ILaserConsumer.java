package io.github.phantamanta44.pcrossbow.api.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public interface ILaserConsumer {

    void consumeBeam(Vec3d direction, float power, float radius);

    class Serializer implements Capability.IStorage<ILaserConsumer> {

        Serializer() {
            // NO-OP
        }

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<ILaserConsumer> capability, ILaserConsumer instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<ILaserConsumer> capability, ILaserConsumer instance, EnumFacing side, NBTBase nbt) {
            // NO-OP
        }

    }

    class Default implements ILaserConsumer {

        Default() {
            // NO-OP
        }

        @Override
        public void consumeBeam(Vec3d direction, float power, float radius) {
            // NO-OP
        }

    }

}
