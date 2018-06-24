package io.github.phantamanta44.pcrossbow.constant;

import io.github.phantamanta44.pcrossbow.Xbow;
import net.minecraft.util.ResourceLocation;

public class ResConst {

    public static final ResourceLocation[] SOUND_LASER_LASING = new ResourceLocation[] {
            Xbow.INSTANCE.newResourceLocation("tile.laser.lasing0"),
            Xbow.INSTANCE.newResourceLocation("tile.laser.lasing1"),
            Xbow.INSTANCE.newResourceLocation("tile.laser.lasing2"),
            Xbow.INSTANCE.newResourceLocation("tile.laser.lasing3")
    };
    public static final ResourceLocation[] SOUND_LASER_STARTUP = new ResourceLocation[] {
            Xbow.INSTANCE.newResourceLocation("tile.laser.startup0"),
            Xbow.INSTANCE.newResourceLocation("tile.laser.startup1"),
            Xbow.INSTANCE.newResourceLocation("tile.laser.startup2"),
            Xbow.INSTANCE.newResourceLocation("tile.laser.startup3")
    };

    public static final String MODEL_KEY = "textures/model/";

    public static final String MODEL_BLOCK_KEY = MODEL_KEY + "block/";
    public static final ResourceLocation MODEL_INDUCTOR = Xbow.INSTANCE.newResourceLocation(MODEL_BLOCK_KEY + "inductor.png");
    public static final ResourceLocation MODEL_MIRROR = Xbow.INSTANCE.newResourceLocation(MODEL_BLOCK_KEY + "mirror.png");

}
