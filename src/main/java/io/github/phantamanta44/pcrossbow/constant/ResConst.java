package io.github.phantamanta44.pcrossbow.constant;

import io.github.phantamanta44.libnine.util.render.TextureRegion;
import io.github.phantamanta44.libnine.util.render.TextureResource;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.util.Cardinal;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class ResConst {

    /*
     * Sounds
     */

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

    /*
     * Model textures
     */

    public static final String MODEL_KEY = "textures/model/";

    public static final String MODEL_BLOCK_KEY = MODEL_KEY + "block/";
    public static final ResourceLocation MODEL_INDUCTOR = Xbow.INSTANCE.newResourceLocation(MODEL_BLOCK_KEY + "inductor.png");
    public static final ResourceLocation MODEL_MIRROR = Xbow.INSTANCE.newResourceLocation(MODEL_BLOCK_KEY + "mirror.png");
    public static final ResourceLocation MODEL_SPLITTER = Xbow.INSTANCE.newResourceLocation(MODEL_BLOCK_KEY + "splitter.png");

    /*
     * GUI textures
     */

    public static final String GUI_KEY = "textures/gui/";
    public static final ResourceLocation GUI_VECTOR_WRENCH = Xbow.INSTANCE.newResourceLocation(GUI_KEY + "vector_wrench.png");

    /*
     * Component textures
     */

    public static final String COMP_KEY = "textures/gui/component/";

    public static final TextureResource COMP_SUBMIT = Xbow.INSTANCE.newTextureResource(COMP_KEY + "submit.png", 39, 13);
    public static final TextureRegion COMP_SUBMIT_ACTIVE = COMP_SUBMIT.getRegion(0, 0, 13, 13);
    public static final TextureRegion COMP_SUBMIT_DISABLED = COMP_SUBMIT.getRegion(13, 0, 13, 13);
    public static final TextureRegion COMP_SUBMIT_HOVER = COMP_SUBMIT.getRegion(26, 0, 13, 13);

    public static final TextureResource COMP_CARDINAL = Xbow.INSTANCE.newTextureResource(COMP_KEY + "cardinal.png", 35, 1188);
    public static final TextureRegion COMP_CARDINAL_DEFAULT = COMP_CARDINAL.getRegion(0, 0, 35, 44);
    public static final TextureRegion[] COMP_CARDINAL_DIRS = Arrays.stream(Cardinal.values())
            .map(c -> COMP_CARDINAL.getRegion(0, c.ordinal() * 44 + 44, 35, 44))
            .toArray(TextureRegion[]::new);

    /*
     * Colours
     */

    public static final int COL_HUD = 0x25f100;
    public static final int COL_HUD_DISABLED = 0x585858;

}
