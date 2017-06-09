package io.github.phantamanta44.pcrossbow.client.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.ResourceLocation;

public class ContinuousSound implements ITickableSound {

    private final ResourceLocation resource;
    private final float volumeFactor;
    private final float pitchFactor;
    private final float x, y, z;
    private final int initialTtl;
    private int ttl;
    private int vol;

    public ContinuousSound(ResourceLocation resource, float volume, float pitch, float x, float y, float z, int ttl) {
        this.resource = resource;
        this.volumeFactor = volume / 100F;
        this.pitchFactor = (pitch - 0.5F) / 100F;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ttl = this.initialTtl = ttl;
        this.vol = 1;
    }

    @Override
    public boolean isDonePlaying() {
        return ttl <= 0 && vol <= 0;
    }

    @Override
    public ResourceLocation getPositionedSoundLocation() {
        return resource;
    }

    @Override
    public boolean canRepeat() {
        return true;
    }

    @Override
    public int getRepeatDelay() {
        return 0;
    }

    @Override
    public float getVolume() {
        return vol * volumeFactor;
    }

    @Override
    public float getPitch() {
        return vol * pitchFactor + 0.5F;
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
        return AttenuationType.LINEAR;
    }

    @Override
    public void update() {
        if (ttl > 0) {
            if (vol < 100)
                vol = Math.min(vol + 5, 100);
            ttl--;
        } else if (vol > 0) {
            vol = Math.max((int)Math.floor(vol - 7), 0);
        }
    }

    public void refresh() {
        ttl = initialTtl;
        vol = Math.max(vol, 1);
    }

}
