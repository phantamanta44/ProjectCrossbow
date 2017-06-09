package io.github.phantamanta44.pcrossbow.client.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;

public class SingleSound implements ISound {

    private final ResourceLocation resource;
    private final float vol;
    private final float pitch;
    private final float x, y, z;

    public SingleSound(ResourceLocation resource, float volume, float pitch, int x, int y, int z) {
        this.resource = resource;
        this.vol = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public ResourceLocation getPositionedSoundLocation() {
        return resource;
    }

    @Override
    public boolean canRepeat() {
        return false;
    }

    @Override
    public int getRepeatDelay() {
        return 0;
    }

    @Override
    public float getVolume() {
        return vol;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public float getXPosF() {
        return x;
    }

    @Override
    public float getYPosF() {
        return y;
    }

    @Override
    public float getZPosF() {
        return z;
    }

    @Override
    public AttenuationType getAttenuationType() {
        return AttenuationType.NONE;
    }

}
