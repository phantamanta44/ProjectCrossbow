package io.github.phantamanta44.pcrossbow.tile;

import io.github.phantamanta44.libnine.capability.impl.L9AspectEnergy;
import io.github.phantamanta44.libnine.capability.impl.L9AspectInventory;
import io.github.phantamanta44.libnine.capability.provider.CapabilityBroker;
import io.github.phantamanta44.libnine.client.sound.SingleSound;
import io.github.phantamanta44.libnine.component.reservoir.IIntReservoir;
import io.github.phantamanta44.libnine.component.reservoir.RatedIntReservoir;
import io.github.phantamanta44.libnine.component.reservoir.SimpleIntReservoir;
import io.github.phantamanta44.libnine.tile.L9TileEntityTicking;
import io.github.phantamanta44.libnine.tile.RegisterTile;
import io.github.phantamanta44.libnine.util.data.ByteUtils;
import io.github.phantamanta44.libnine.util.data.serialization.AutoSerialize;
import io.github.phantamanta44.libnine.util.data.serialization.IDatum;
import io.github.phantamanta44.libnine.util.world.IRedstoneControllable;
import io.github.phantamanta44.libnine.util.world.RedstoneBehaviour;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.BlockLaser;
import io.github.phantamanta44.pcrossbow.client.sound.LaserSound;
import io.github.phantamanta44.pcrossbow.constant.ResConst;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public abstract class TileLaser extends L9TileEntityTicking implements IRedstoneControllable {

    private final BlockLaser.Type type;

    @AutoSerialize
    private final IIntReservoir energy;
    @AutoSerialize
    private final LaserInv inventory;
    @AutoSerialize
    private final IDatum<EnumFacing> rotation;
    @AutoSerialize
    private final IDatum<RedstoneBehaviour> redstone;
    @AutoSerialize
    private final IDatum.OfBool lasing;

    private double cachedPower, cachedRadius, cachedFluxAngle;
    private boolean shouldRefreshStats;

    /*
     * Client stuff
     */
    private EnumFacing clientFace;
    private long lastLasingTime;
    private LaserSound sound;

    public TileLaser(BlockLaser.Type type) {
        this.type = type;
        this.energy = new SimpleIntReservoir((int)Math.ceil(type.getBasePower() * 20));
        this.inventory = new LaserInv(this);
        this.rotation = IDatum.of(EnumFacing.NORTH);
        this.redstone = IDatum.of(RedstoneBehaviour.DIRECT);
        this.lasing = IDatum.ofBool(false);
        this.cachedPower = 0;
        this.cachedRadius = type.getBaseRadius();
        this.cachedFluxAngle = type.getBaseFluxAngle();
        this.shouldRefreshStats = true;
        this.clientFace = EnumFacing.NORTH;
        this.lastLasingTime = 0L;
        this.sound = null;
        energy.onQuantityChange((o, n) -> setDirty());
        markRequiresSync();
        setInitialized();
    }

    @Override
    protected ICapabilityProvider initCapabilities() {
        return new CapabilityBroker()
                .with(CapabilityEnergy.ENERGY, new L9AspectEnergy(new RatedIntReservoir(energy, -1, 0)))
                .with(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventory);
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
        if (energy.getQuantity() > 0 && getRedstoneBehaviour().canWork(getWorldPos())) {
            int power = energy.draw((int)Math.ceil(getPower()), !world.isRemote);
            if (power > 0) {
                Vec3d dir = new Vec3d(rotation.get().getDirectionVec());
                Xbow.PROXY.doLasing(world, WorldUtils.getBlockCenter(pos).add(dir.scale(0.5D)), dir,
                        power, getRadius(), getFluxAngle(), getWorldPos());
                if (world.isRemote) {
                    long currentTick = world.getTotalWorldTime();
                    if (!lasing.isTrue() && currentTick - lastLasingTime > 30) {
                        Minecraft.getMinecraft().getSoundHandler().playSound(new SingleSound(
                                ResConst.SOUND_LASER_STARTUP[getBlockMetadata()],
                                0.6F, 1F, pos, SoundCategory.BLOCKS));
                    }
                    if (sound == null || sound.isDonePlaying()) {
                        sound = new LaserSound(
                                ResConst.SOUND_LASER_LASING[getBlockMetadata()],
                                0.325F - 0.18F * Math.min(power / 30000F, 1),
                                0.8F + 0.8F * Math.min(power / 30000F, 1),
                                pos, SoundCategory.BLOCKS, 4);
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

    public double getPower() {
        tryRefreshStats();
        return cachedPower;
    }

    public double getRadius() {
        tryRefreshStats();
        return cachedRadius;
    }

    public double getFluxAngle() {
        tryRefreshStats();
        return cachedFluxAngle;
    }

    private void tryRefreshStats() {
        if (shouldRefreshStats) {
            ItemStack stack = inventory.getStackInSlot(0);
            if (stack.hasCapability(XbowCaps.LASER_MOD_POWER, null)) {
                cachedPower = stack.getCapability(XbowCaps.LASER_MOD_POWER, null).modifyPower(type.getBasePower());
            } else {
                cachedPower = 0;
            }
            stack = inventory.getStackInSlot(1);
            if (stack.hasCapability(XbowCaps.LASER_MOD_RADIUS, null)) {
                cachedRadius = stack.getCapability(XbowCaps.LASER_MOD_RADIUS, null).modifyRadius(type.getBaseRadius());
            } else {
                cachedRadius = type.getBaseRadius();
            }
            stack = inventory.getStackInSlot(2);
            if (stack.hasCapability(XbowCaps.LASER_MOD_FLUX_ANGLE, null)) {
                cachedFluxAngle = stack.getCapability(XbowCaps.LASER_MOD_FLUX_ANGLE, null).modifyFluxAngle(type.getBaseFluxAngle());
            } else {
                cachedFluxAngle = type.getBaseFluxAngle();
            }
            shouldRefreshStats = false;
        }
    }

    @Override
    public RedstoneBehaviour getRedstoneBehaviour() {
        return redstone.get();
    }

    @Override
    public void setRedstoneBehaviour(RedstoneBehaviour behaviour) {
        redstone.set(behaviour);
        setDirty();
    }

    @Override
    public void deserializeBytes(ByteUtils.Reader data) {
        super.deserializeBytes(data);
        if (clientFace != getDirection()) {
            world.markBlockRangeForRenderUpdate(pos, pos);
            clientFace = getDirection();
        }
    }

    @RegisterTile(XbowConst.MOD_ID)
    public static class Tier1 extends TileLaser {

        public Tier1() {
            super(BlockLaser.Type.TIER_1);
        }

    }

    @RegisterTile(XbowConst.MOD_ID)
    public static class Tier2 extends TileLaser {

        public Tier2() {
            super(BlockLaser.Type.TIER_2);
        }

    }

    @RegisterTile(XbowConst.MOD_ID)
    public static class Tier3 extends TileLaser {

        public Tier3() {
            super(BlockLaser.Type.TIER_3);
        }

    }

    @RegisterTile(XbowConst.MOD_ID)
    public static class Tier4 extends TileLaser {

        public Tier4() {
            super(BlockLaser.Type.TIER_4);
        }

    }

    private static class LaserInv extends L9AspectInventory {

        private final TileLaser tile;

        public LaserInv(TileLaser tile) {
            super(3);
            this.tile = tile;
            withPredicate(0, s -> s.hasCapability(XbowCaps.LASER_MOD_POWER, null));
            withPredicate(1, s -> s.hasCapability(XbowCaps.LASER_MOD_RADIUS, null));
            withPredicate(2, s -> s.hasCapability(XbowCaps.LASER_MOD_FLUX_ANGLE, null));
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            super.setStackInSlot(slot, stack);
            tile.shouldRefreshStats = true;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

    }

}
