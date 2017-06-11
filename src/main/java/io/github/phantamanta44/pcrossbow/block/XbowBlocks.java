package io.github.phantamanta44.pcrossbow.block;

public class XbowBlocks {

    public static BlockLaser laser;
    public static BlockInductor inductor;

    public static void init() {
        laser = new BlockLaser();
        inductor = new BlockInductor();
    }

}
