package io.github.phantamanta44.pcrossbow.inventory.base;

import io.github.phantamanta44.libnine.gui.L9Container;
import io.github.phantamanta44.libnine.util.data.ByteUtils;
import io.github.phantamanta44.libnine.util.helper.ItemUtils;
import io.github.phantamanta44.libnine.util.world.IRedstoneControllable;
import io.github.phantamanta44.libnine.util.world.RedstoneBehaviour;
import io.github.phantamanta44.pcrossbow.tile.base.TileSimpleProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerMachine<T extends TileSimpleProcessor> extends L9Container implements IRedstoneControllable {

    protected final T tile;

    public ContainerMachine(EntityPlayer player, T tile) {
        super(player.inventory);
        this.tile = tile;
    }

    @SuppressWarnings("unchecked")
    public ContainerMachine(EntityPlayer player, World world, int x, int y, int z) {
        this(player, (T)world.getTileEntity(new BlockPos(x, y, z)));
    }

    public String getTitle() {
        return ItemUtils.getColouredBlockName(tile.getWorldPos());
    }

    public RedstoneBehaviour getRedstoneBehaviour() {
        return tile.getRedstoneBehaviour();
    }

    @Override
    public void setRedstoneBehaviour(RedstoneBehaviour behaviour) {
        sendInteraction(new byte[] { (byte)-128, (byte)behaviour.ordinal() });
    }

    @Override
    public void onClientInteraction(ByteUtils.Reader data) {
        byte opcode = data.readByte();
        if (opcode == -128) {
            tile.setRedstoneBehaviour(RedstoneBehaviour.values()[data.readByte()]);
        } else {
            doClientInteraction(opcode, data);
        }
    }

    protected void doClientInteraction(byte opcode, ByteUtils.Reader data) {
        // NO-OP
    }

}
