package io.github.phantamanta44.pcrossbow.wsd;

import io.github.phantamanta44.libnine.util.nbt.NBTUtils;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.libnine.wsd.L9WSD;
import io.github.phantamanta44.pcrossbow.Xbow;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.base.ILaserBlock;
import io.github.phantamanta44.pcrossbow.constant.NBTConst;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LaserConsumerTracker extends L9WSD {

    private final Set<BlockPos> openSet, closedSet;
    private World world;

    public LaserConsumerTracker(String id) {
        super(Xbow.INSTANCE, XbowWSDs.LASER_CONSUMER_TRACKER);
        this.openSet = new HashSet<>();
        this.closedSet = new HashSet<>();
        this.world = null;
    }

    public LaserConsumerTracker(World world) {
        super(Xbow.INSTANCE, XbowWSDs.LASER_CONSUMER_TRACKER);
        this.openSet = new HashSet<>();
        this.closedSet = new HashSet<>();
        this.world = world;
    }

    public void markLased(BlockPos pos) {
        closedSet.add(pos);
        openSet.remove(pos);
        markDirty();
    }

    public void update() {
        Iterator<BlockPos> iter = openSet.iterator();
        while (iter.hasNext()) {
            WorldBlockPos pos = new WorldBlockPos(world, iter.next());
            IBlockState state = pos.getBlockState();
            TileEntity tile = pos.getTileEntity();
            if (tile != null && tile.hasCapability(XbowCaps.LASER_CONSUMER, null)) {
                tile.getCapability(XbowCaps.LASER_CONSUMER, null).onLasingFinished();
            } else if (state.getBlock() instanceof ILaserBlock) {
                ((ILaserBlock)state.getBlock()).lasingFinished(pos);
            }
            iter.remove();
        }
        openSet.addAll(closedSet);
        closedSet.clear();
        markDirty();
    }

    @Override
    public void serNBT(NBTTagCompound tag) {
        NBTTagList set = new NBTTagList();
        for (BlockPos pos : openSet) set.appendTag(NBTUtils.serializeBlockPos(pos));
        tag.setTag(NBTConst.OPEN, set);
        set = new NBTTagList();
        for (BlockPos pos : closedSet) set.appendTag(NBTUtils.serializeBlockPos(pos));
        tag.setTag(NBTConst.CLOSED, set);
        tag.setInteger(NBTConst.WORLD, world.provider.getDimension());
    }

    @Override
    public void deserNBT(NBTTagCompound tag) {
        openSet.clear();
        for (NBTBase elem : tag.getTagList(NBTConst.OPEN, Constants.NBT.TAG_STRING)) {
            openSet.add(NBTUtils.deserializeBlockPos((NBTTagCompound)elem));
        }
        closedSet.clear();
        for (NBTBase elem : tag.getTagList(NBTConst.CLOSED, Constants.NBT.TAG_STRING)) {
            closedSet.add(NBTUtils.deserializeBlockPos((NBTTagCompound)elem));
        }
        world = DimensionManager.getWorld(tag.getInteger(NBTConst.WORLD));
    }

}
