package io.github.phantamanta44.pcrossbow.util;

import io.github.phantamanta44.libnine.util.tuple.IPair;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class EnergyUtils {

    // Adapted from RailTech's io.github.phantamanta44.mcrail.railtech.util.EnergyUtils#distribute(Collection<? extends IEnergyConsumer>, long)
    public static int distributeAdj(World world, BlockPos pos, int amount) {
        Collection<IPair<EnumFacing, TileEntity>> cons = Arrays.stream(EnumFacing.values())
                .map(dir -> IPair.of(dir, world.getTileEntity(pos.add(dir.getDirectionVec()))))
                .filter(p -> p.getB() != null
                        && p.getB().hasCapability(CapabilityEnergy.ENERGY, p.getA().getOpposite()))
                .collect(Collectors.toList());
        int toDis = amount;
        for (int i = 0; !cons.isEmpty() && i < 3; i++) {
            int rate = (int)Math.floor((float)toDis / (float)cons.size());
            Iterator<IPair<EnumFacing, TileEntity>> iter = cons.iterator();
            while (iter.hasNext()) {
                IPair<EnumFacing, TileEntity> con = iter.next();
                int transferred = con.getB().getCapability(CapabilityEnergy.ENERGY, con.getA().getOpposite())
                        .receiveEnergy(rate, false);
                if (transferred < 1)
                    iter.remove();
                toDis -= transferred;
            }
        }
        return amount - toDis;
    }

    public static float getPercentage(IEnergyStorage storage) {
        return (float)storage.getEnergyStored() / storage.getMaxEnergyStored();
    }

}
