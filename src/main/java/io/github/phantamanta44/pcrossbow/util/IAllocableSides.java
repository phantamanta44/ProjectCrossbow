package io.github.phantamanta44.pcrossbow.util;

import io.github.phantamanta44.libnine.util.world.BlockSide;

public interface IAllocableSides<E extends Enum<E>> {

    void setFace(BlockSide face, E state);

    E getFace(BlockSide face);

}
