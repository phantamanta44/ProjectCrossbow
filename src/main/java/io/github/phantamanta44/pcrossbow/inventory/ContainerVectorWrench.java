package io.github.phantamanta44.pcrossbow.inventory;

import io.github.phantamanta44.libnine.gui.L9Container;
import io.github.phantamanta44.libnine.util.data.ByteUtils;
import io.github.phantamanta44.pcrossbow.api.capability.IVectorDirectional;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ContainerVectorWrench extends L9Container implements IVectorDirectional {

    private final IVectorDirectional target;

    public ContainerVectorWrench(EntityPlayer player, IVectorDirectional target) {
        super(player.inventory);
        this.target = target;
    }

    public ContainerVectorWrench(EntityPlayer player, World world, int x, int y, int z) {
        this(player, world.getTileEntity(new BlockPos(x, y, z)).getCapability(XbowCaps.VECTOR_DIR, null));
    }

    @Override
    public Vec3d getNorm() {
        return target.getNorm();
    }

    @Override
    public void setNorm(Vec3d dir) {
        sendInteraction(ByteUtils.writer().writeVec3d(dir).toArray());
    }

    @Override
    public void onClientInteraction(ByteUtils.Reader data) {
        Vec3d v = data.readVec3d().normalize();
        target.setNorm(v.normalize());
    }

}
