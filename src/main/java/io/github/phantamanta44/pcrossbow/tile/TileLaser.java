package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.capability.L9AspectEnergy;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.component.IntReservoir;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.WorldBlockPos;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.helper.WorldUtils;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.client.sound.ContinuousSound;
import io.github.phantamanta44.pcrossbow.client.sound.SingleSound;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

@RegisterTile(XbowConst.MOD_ID)
public abstract class TileLaser extends L9TileEntityTicking {

    public static final int MAX_ENERGY = 400000;

    @AutoSerialize
    private final IntReservoir energy;
    @AutoSerialize
    private final IDatum<EnumFacing> rotation;
    @AutoSerialize
    private final IDatum.OfBool lasing;

    private long lastLasingTime = 0;
    private ContinuousSound sound = null;

    public TileLaser(int size) {
        this.energy = new IntReservoir(MAX_ENERGY);
        this.rotation = IDatum.of(EnumFacing.NORTH);
        this.lasing = IDatum.ofBool(false);
        energy.onQuantityChange((o, n) -> setDirty());
        markRequiresSync();
        setInitialized();
    }

    @Override
    protected ICapabilityProvider initCapabilities() {
        return new CapabilityBroker().with(CapabilityEnergy.ENERGY, new L9AspectEnergy(energy));
    }

    public void setDirection(EnumFacing dir) {
        rotation.set(dir);
        setDirty();
    }

    public EnumFacing getDirection() {
        return rotation.get();
    }

    @Override
    protected void tick() {
        if (energy.getQuantity() > 80 && world.isBlockIndirectlyGettingPowered(pos) > 0) {
            if (energy.draw(80, true) == 80) {
                Vec3d dir = new Vec3d(rotation.get().getDirectionVec());
                Xbow.PROXY.doLasing(world, WorldUtils.getBlockCenter(pos).add(dir.scale(0.5D)), dir,
                        30000D, 0.125D, 0.0025D, new WorldBlockPos(world, pos));
                if (world.isRemote) {
                    long currentTick = world.getTotalWorldTime();
                    if (!lasing.isTrue() && currentTick - lastLasingTime > 30) {
                        Minecraft.getMinecraft().getSoundHandler().playSound(new SingleSound(
                                ResConst.SOUND_LASER_STARTUP[getBlockMetadata()],
                                0.6F, 1F, pos, SoundCategory.BLOCKS));
                    }
                    if (sound == null || sound.isDonePlaying()) {
                        sound = new ContinuousSound(
                                ResConst.SOUND_LASER_LASING[getBlockMetadata()],
                                0.3F, 1F, pos, SoundCategory.BLOCKS, 4); // TODO pitch on power
                        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
                    } else {
                        sound.refresh();
                    }
                    lastLasingTime = currentTick;
                }
                lasing.setBool(true);
                setDirty();
            } else {
                lasing.setBool(false);
            }
        } else {
            lasing.setBool(false);
        }
    }

    @RegisterTile(XbowConst.MOD_ID)
    public static class Test extends TileLaser {

        public Test() {
            super(0);
        }

    }

}
