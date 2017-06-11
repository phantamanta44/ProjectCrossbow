package io.github.phantamanta44.pcrossbow.constant;

import net.minecraft.util.ResourceLocation;

public class ResConst {

    public static final ResourceLocation[] SOUND_LASER_LASING = new ResourceLocation[] {
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.lasing0"),
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.lasing1"),
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.lasing2"),
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.lasing3")
    };
    public static final ResourceLocation[] SOUND_LASER_STARTUP = new ResourceLocation[] {
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.startup0"),
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.startup1"),
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.startup2"),
            new ResourceLocation(XbowConst.MOD_ID, "tile.laser.startup3")
    };

    public static final String MODEL_KEY = XbowConst.MOD_PREF + "textures/model/";

    public static final String MODEL_BLOCK_KEY = MODEL_KEY + "block/";
    public static final ResourceLocation MODEL_INDUCTOR = new ResourceLocation(MODEL_BLOCK_KEY + "inductor.png");

}
