package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.LibNine;
import io.github.phantamanta44.pcrossbow.Xbow;

public class XbowBlocks {

    public static BlockLaser laser;
    public static BlockInductor inductor;
    public static BlockOptics optics;
    public static BlockSensor sensor;
    public static BlockRedstoneWindow redstoneWindow;
    public static BlockMachine machine;

    public static void init() {
        LibNine.PROXY.getRegistrar().begin(Xbow.INSTANCE);
        laser = new BlockLaser();
        inductor = new BlockInductor();
        optics = new BlockOptics();
        sensor = new BlockSensor();
        redstoneWindow = new BlockRedstoneWindow();
        machine = new BlockMachine();
        LibNine.PROXY.getRegistrar().end();
    }

}
