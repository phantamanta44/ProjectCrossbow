package io.github.phantamanta44.pcrossbow.inventory;

import io.github.phantamanta44.libnine.gui.L9Container;
import io.github.phantamanta44.libnine.util.helper.ItemUtils;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerInductor extends L9Container {

    private final TileInductor tile;

    public ContainerInductor(EntityPlayer player, TileInductor tile) {
        super(player.inventory);
        this.tile = tile;
    }

    public ContainerInductor(EntityPlayer player, World world, int x, int y, int z) {
        this(player, (TileInductor)world.getTileEntity(new BlockPos(x, y, z)));
    }

    public double getPower() {
        return tile.getLastPower();
    }

    public double getRadius() {
        return tile.getLastRadius();
    }

    public double getFluxAngle() {
        return tile.getLastFluxAngle();
    }

    public boolean isActive() {
        return tile.getEnergyStored() > 0;
    }

    public String getTitle() {
        return ItemUtils.getColouredBlockName(tile.getWorldPos());
    }

}
