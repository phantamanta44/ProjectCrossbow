package io.github.phantamanta44.pcrossbow.inventory;

import io.github.phantamanta44.libnine.util.data.ByteUtils;
import io.github.phantamanta44.libnine.util.world.BlockSide;
import io.github.phantamanta44.libnine.util.world.IAllocableSides;
import io.github.phantamanta44.pcrossbow.api.tile.IHeatCarrier;
import io.github.phantamanta44.pcrossbow.inventory.base.ContainerMachine;
import io.github.phantamanta44.pcrossbow.inventory.slot.OutputSlot;
import io.github.phantamanta44.pcrossbow.tile.TileLaserFurnace;
import io.github.phantamanta44.pcrossbow.util.SlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerLaserFurnace extends ContainerMachine<TileLaserFurnace> implements IHeatCarrier, IAllocableSides<SlotType.BasicIO> {

    public ContainerLaserFurnace(EntityPlayer player, World world, int x, int y, int z) {
        super(player, world, x, y, z);
        addSlotToContainer(new SlotItemHandler(tile.getInputSlot(), 0, 63, 35));
        addSlotToContainer(new OutputSlot(tile.getOutputSlot(), 97, 35));
    }

    @Override
    public double getTemperature() {
        return tile.getTemperature();
    }

    public float getProgress() {
        return tile.getProgress();
    }

    @Override
    public void setFace(BlockSide face, SlotType.BasicIO state) {
        sendInteraction(new byte[] { (byte)0, (byte)face.ordinal(), (byte)state.ordinal() });
    }

    @Override
    public SlotType.BasicIO getFace(BlockSide face) {
        return tile.getFace(face);
    }

    @Override
    protected void doClientInteraction(byte opcode, ByteUtils.Reader data) {
        if (opcode == 0) {
            tile.setFace(BlockSide.values()[data.readByte()], SlotType.BasicIO.values()[data.readByte()]);
        }
    }

}
