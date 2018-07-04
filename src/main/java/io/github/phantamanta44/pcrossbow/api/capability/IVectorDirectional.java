package io.github.phantamanta44.pcrossbow.api.capability;

import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public interface IVectorDirectional {
    
    Vec3d getNorm();
    
    void setNorm(Vec3d dir);

    class Serializer implements Capability.IStorage<IVectorDirectional> {

        Serializer() {
            // NO-OP
        }

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IVectorDirectional> capability, IVectorDirectional instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IVectorDirectional> capability, IVectorDirectional instance, EnumFacing side, NBTBase nbt) {
            // NO-OP
        }

    }

    class Default implements IVectorDirectional {

        private Vec3d norm;

        Default() {
            this.norm = LinAlUtils.Y_POS;
        }

        @Override
        public Vec3d getNorm() {
            return norm;
        }

        @Override
        public void setNorm(Vec3d dir) {
            this.norm = dir;
        }

    }
    
}
