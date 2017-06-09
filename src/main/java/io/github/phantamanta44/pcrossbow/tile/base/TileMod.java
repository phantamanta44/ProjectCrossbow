package io.github.phantamanta44.pcrossbow.tile.base;

import io.github.phantamanta44.pcrossbow.util.VanillaPacketDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileMod extends TileEntity {

    protected boolean init = false;

    protected abstract void tick();

    public void markForUpdate() {
        if (worldObj != null && !worldObj.isRemote)
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(worldObj, xCoord, yCoord, zCoord);
        markDirty();
    }

    public boolean isInitialized() {
        return init;
    }

    @Override
    public void updateEntity() {
        if (init)
            tick();
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        readFromNBT(packet.func_148857_g());
    }


}