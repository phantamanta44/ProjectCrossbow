package io.github.phantamanta44.pcrossbow.util;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class EnergyHelper {

    // Adapted from RailTech's io.github.phantamanta44.mcrail.railtech.util.EnergyUtils#distribute(Collection<? extends IEnergyConsumer>, long)
    public static int distributeAdj(World world, int x, int y, int z, int amount) {
        Collection<Pair<ForgeDirection, TileEntity>> cons = Arrays.stream(ForgeDirection.VALID_DIRECTIONS)
                .map(dir -> Pair.of(dir, world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)))
                .filter(p -> p.getValue() != null && p.getValue() instanceof IEnergyReceiver)
                .collect(Collectors.toList());
        int toDis = amount;
        for (int i = 0; !cons.isEmpty() && i < 3; i++) {
            int rate = (int)Math.floor((float)toDis / (float)cons.size());
            Iterator<Pair<ForgeDirection, TileEntity>> iter = cons.iterator();
            while (iter.hasNext()) {
                Pair<ForgeDirection, TileEntity> con = iter.next();
                int transferred = ((IEnergyReceiver)con.getValue()).receiveEnergy(con.getKey(), rate, false);
                if (transferred < 1)
                    iter.remove();
                toDis -= transferred;
            }
        }
        return amount - toDis;
    }

}
