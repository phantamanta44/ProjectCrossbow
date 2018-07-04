package io.github.phantamanta44.pcrossbow.util;

import io.github.phantamanta44.libnine.util.render.TextureRegion;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import net.minecraft.util.math.Vec3d;

import static io.github.phantamanta44.pcrossbow.util.PhysicsUtils.C2;
import static io.github.phantamanta44.pcrossbow.util.PhysicsUtils.C3;

public enum Cardinal {

    UP_NORTH_WEST(-C3, C3, -C3),
    UP_NORTH(0, C2, -C2),
    UP_NORTH_EAST(C3, C3, -C3),
    UP_WEST(-C2, C2, 0),
    UP(0, 1, 0),
    UP_EAST(C2, C2, 0),
    UP_SOUTH_WEST(-C3, C3, C3),
    UP_SOUTH(0, C2, C2),
    UP_SOUTH_EAST(C3, C3, C3),
    NORTH_WEST(-C2, 0, -C2),
    NORTH(0, 0, -1),
    NORTH_EAST(C2, 0, -C2),
    WEST(-1, 0, 0),
    EAST(1, 0, 0),
    SOUTH_WEST(-C2, 0, C2),
    SOUTH(0, 0, 1),
    SOUTH_EAST(C2, 0, C2),
    DOWN_NORTH_WEST(-C3, -C3, -C3),
    DOWN_NORTH(0, -C2, -C2),
    DOWN_NORTH_EAST(C3, -C3, -C3),
    DOWN_WEST(-C2, -C2, 0),
    DOWN(0, -1, 0),
    DOWN_EAST(C2, -C2, 0),
    DOWN_SOUTH_WEST(-C3, -C3, C3),
    DOWN_SOUTH(0, -C2, C2),
    DOWN_SOUTH_EAST(C3, -C3, C3);

    private final String unlocalizedName;
    private final Vec3d vec;

    Cardinal(double x, double y, double z) {
        this.unlocalizedName = LangConst.getCardinalName(this);
        this.vec = new Vec3d(x, y, z);
    }

    public Vec3d getVector() {
        return vec;
    }

    public TextureRegion getTexture() {
        return ResConst.COMP_CARDINAL_DIRS[ordinal()];
    }

    public String getDisplayName() {
        return LangConst.get(unlocalizedName);
    }

}
