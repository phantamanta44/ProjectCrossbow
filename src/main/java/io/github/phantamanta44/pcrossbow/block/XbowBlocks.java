package io.github.phantamanta44.pcrossbow.block;

import io.github.phantamanta44.libnine.InitMe;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;

@SuppressWarnings("NullableProblems")
public class XbowBlocks {

    public static BlockLaser laser;
    public static BlockInductor inductor;
    public static BlockOptics optics;
    public static BlockSensor sensor;
    public static BlockRedstoneWindow redstoneWindow;
    public static BlockMachine machine;

    @InitMe(XbowConst.MOD_ID)
    public static void init() {
        laser = new BlockLaser();
        inductor = new BlockInductor();
        optics = new BlockOptics();
        sensor = new BlockSensor();
        redstoneWindow = new BlockRedstoneWindow();
        machine = new BlockMachine();
    }

}
