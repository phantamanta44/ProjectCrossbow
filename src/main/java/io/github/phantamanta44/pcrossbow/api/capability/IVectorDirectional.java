package io.github.phantamanta44.pcrossbow.api.capability;

import io.github.phantamanta44.libnine.util.math.LinAlUtils;
import net.minecraft.util.math.Vec3d;

public interface IVectorDirectional {
    
    Vec3d getNorm();
    
    void setNorm(Vec3d dir);

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
