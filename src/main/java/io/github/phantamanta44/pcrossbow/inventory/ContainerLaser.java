package io.github.phantamanta44.pcrossbow.inventory;

import io.github.phantamanta44.libnine.gui.L9Container;
import io.github.phantamanta44.libnine.util.data.ByteUtils;
import io.github.phantamanta44.libnine.util.helper.ItemUtils;
import io.github.phantamanta44.libnine.util.world.IRedstoneControllable;
import io.github.phantamanta44.libnine.util.world.RedstoneBehaviour;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.inventory.slot.LaserSlot;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ContainerLaser extends L9Container implements IRedstoneControllable {

    private final TileLaser tile;

    public ContainerLaser(EntityPlayer player, TileLaser tile) {
        super(player.inventory);
        this.tile = tile;
        addSlotToContainer(new LaserSlot(tile, 0, 45, 35, XbowCaps.LASER_MOD_POWER));
        addSlotToContainer(new LaserSlot(tile, 1, 80, 35, XbowCaps.LASER_MOD_RADIUS));
        addSlotToContainer(new LaserSlot(tile, 2, 115, 35, XbowCaps.LASER_MOD_FLUX_ANGLE));
    }

    public ContainerLaser(EntityPlayer player, World world, int x, int y, int z) {
        this(player, (TileLaser)world.getTileEntity(new BlockPos(x, y, z)));
    }

    public String getTitle() {
        return ItemUtils.getColouredBlockName(tile.getWorldPos());
    }

    @Override
    public RedstoneBehaviour getRedstoneBehaviour() {
        return tile.getRedstoneBehaviour();
    }

    @Override
    public void setRedstoneBehaviour(RedstoneBehaviour behaviour) {
        sendInteraction(new byte[] { (byte)behaviour.ordinal() });
    }

    public IEnergyStorage getEnergyStorage() {
        return tile.getCapability(CapabilityEnergy.ENERGY, null);
    }

    public double getPower() {
        return tile.getPower();
    }

    public double getRadius() {
        return tile.getRadius();
    }

    public double getFluxAngle() {
        return tile.getFluxAngle();
    }

    @Override
    public void onClientInteraction(ByteUtils.Reader data) {
        tile.setRedstoneBehaviour(RedstoneBehaviour.values()[data.readByte()]);
    }

}
