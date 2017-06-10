package io.github.phantamanta44.pcrossbow;

import io.github.phantamanta44.pcrossbow.block.XbowBlocks;
import io.github.phantamanta44.pcrossbow.item.XbowItems;
import io.github.phantamanta44.pcrossbow.tile.TileLaser;
import io.github.phantamanta44.pcrossbow.util.NumeralRange;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

public class CommonProxy {

    protected static final double INTENSITY_CUTOFF = 0.1D;

    public void onPreInit(File config) {
        // TODO Load config
        XbowBlocks.init();
        XbowItems.init();
    }

    public void onInit() {
        // TODO Register oredict entries (?)
        // TODO Register recipes
        registerTile(TileLaser.Test.class);
    }

    public void onPostInit() {
        // NO-OP
    }

    @SuppressWarnings("unchecked")
    public void doLasing(World world, float x, float y, float z, Vec3 dir, float initialIntensity, float initialRadius, float radiusRate) {
        dir = dir.normalize();
        Vec3 initialPos = Vec3.createVectorHelper(x + dir.xCoord * 0.5D, y + dir.yCoord * 0.5D, z + dir.zCoord * 0.5D);
        double range = Math.min(Math.sqrt(initialIntensity / (Math.PI * INTENSITY_CUTOFF)) / radiusRate, 128);
        Vec3 maxPotentialPos = initialPos.addVector(range * dir.xCoord, range * dir.yCoord, range * dir.zCoord);
        Vec3 traceStart = initialPos.addVector(dir.xCoord < 0 ? -1 : 0, dir.yCoord < 0 ? -1 : 0, dir.zCoord < 0 ? -1 : 0);
        MovingObjectPosition trace = traceRay(
                world, traceStart, maxPotentialPos, b -> b.isOpaqueCube() || b instanceof IFluidBlock || b instanceof BlockLiquid, true);
        if (trace != null) {
            int tX = trace.blockX, tY = trace.blockY, tZ = trace.blockZ;
            double intensity = initialIntensity /
                    (initialRadius + Math.PI * radiusRate * radiusRate * (Math.pow(tX - x, 2) + Math.pow(tY - y, 2) + Math.pow(tZ - z, 2)));
            Block block = world.getBlock(tX, tY, tZ);
            if (block instanceof IFluidBlock || block instanceof BlockLiquid) {
                Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
                if (fluid != null && intensity * 60 >= fluid.getTemperature(world, tX, tY, tZ)) {
                    breakBlockNaturally(world, tX, tY, tZ);
                    world.playAuxSFX(1004, tX, tY, tZ, 0);
                    world.playAuxSFX(2000, tX, tY, tZ, 1);
                }
            } else {
                float hardness = block.getBlockHardness(world, tX, tY, tZ);
                if (intensity * 0.03D >= hardness && hardness >= 0)
                    breakBlockNaturally(world, tX, tY, tZ);
            }
        }
        Vec3 finalPos = trace != null ? trace.hitVec : maxPotentialPos;
        ((List<Entity>)world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(
                Math.min(initialPos.xCoord, finalPos.xCoord) - 0.1,
                Math.min(initialPos.yCoord, finalPos.yCoord) - 0.1,
                Math.min(initialPos.zCoord, finalPos.zCoord) - 0.1,
                Math.max(initialPos.xCoord, finalPos.xCoord) + 0.1,
                Math.max(initialPos.yCoord, finalPos.yCoord) + 0.1,
                Math.max(initialPos.zCoord, finalPos.zCoord) + 0.1
        ))).stream()
                .filter(e -> e.boundingBox != null && intersectsLine(e.boundingBox, initialPos, finalPos))
                .forEach(e -> {
                    double intensity = initialIntensity /
                            (initialRadius + Math.PI * radiusRate * radiusRate * e.getDistanceSq(initialPos.xCoord, initialPos.yCoord, initialPos.zCoord));
                    if (intensity >= 1) {
                        e.setFire((int)Math.floor(intensity * 10));
                        if (e.hurtResistantTime <= 0)
                            e.attackEntityFrom(DamageSource.inFire, Double.valueOf(intensity).floatValue());
                    }
                });
    }

    private void registerTile(Class<? extends TileEntity> clazz) {
        TileEntity.addMapping(clazz, clazz.getName());
    }

    public boolean intersectsLine(AxisAlignedBB prism, Vec3 lineMin, Vec3 lineMax) {
        lineMin = Vec3.createVectorHelper(
                Math.min(lineMin.xCoord, lineMax.xCoord),
                Math.min(lineMin.yCoord, lineMax.yCoord),
                Math.min(lineMin.zCoord, lineMax.zCoord));
        lineMax = Vec3.createVectorHelper(
                Math.max(lineMin.xCoord, lineMax.xCoord),
                Math.max(lineMin.yCoord, lineMax.yCoord),
                Math.max(lineMin.zCoord, lineMax.zCoord));
        Vec3 k = lineMax.subtract(lineMin).normalize();
        NumeralRange domainX = NumeralRange.between(
                (prism.minX - lineMin.xCoord) / k.xCoord, (prism.maxX - lineMax.xCoord) / k.xCoord);
        if (domainX == null)
            return false;
        NumeralRange domainY = NumeralRange.between(
                (prism.minY - lineMin.yCoord) / k.yCoord, (prism.maxY - lineMax.yCoord) / k.yCoord);
        if (domainY == null)
            return false;
        NumeralRange domainZ = NumeralRange.between(
                (prism.minZ - lineMin.zCoord) / k.zCoord, (prism.maxZ - lineMax.zCoord) / k.zCoord);
        if (domainZ == null)
            return false;
        return domainX.intersect(domainY) && domainX.intersect(domainZ);
    }

    public MovingObjectPosition traceRay(World world, Vec3 start, Vec3 end, Predicate<Block> cutoff) {
        return traceRay(world, start, end, cutoff, false);
    }

    // Adapted from net.minecraft.world.World#func_147447_a(Vec3, Vec3, boolean, boolean, boolean)
    public MovingObjectPosition traceRay(World world, Vec3 start, Vec3 end, Predicate<Block> cutoff, boolean considerInitial) {
        start = Vec3.createVectorHelper(start.xCoord, start.yCoord, start.zCoord);
        end = Vec3.createVectorHelper(end.xCoord, end.yCoord, end.zCoord);
        if (!Double.isNaN(start.xCoord) && !Double.isNaN(start.yCoord) && !Double.isNaN(start.zCoord)) {
            if (!Double.isNaN(end.xCoord) && !Double.isNaN(end.yCoord) && !Double.isNaN(end.zCoord)) {
                int xf = MathHelper.floor_double(end.xCoord);
                int yf = MathHelper.floor_double(end.yCoord);
                int zf = MathHelper.floor_double(end.zCoord);
                int x = MathHelper.floor_double(start.xCoord);
                int y = MathHelper.floor_double(start.yCoord);
                int z = MathHelper.floor_double(start.zCoord);
                Block bi = world.getBlock(x, y, z);
                int n = world.getBlockMetadata(x, y, z);

                if (considerInitial && cutoff.test(bi) && (bi.getCollisionBoundingBoxFromPool(world, x, y, z) != null
                        && (bi.canCollideCheck(n, false)) || (bi instanceof IFluidBlock || bi instanceof BlockLiquid))) {
                    MovingObjectPosition collision = bi.collisionRayTrace(world, x, y, z, start, end);

                    if (collision != null) {
                        return collision;
                    }
                }

                n = 200;

                while (n-- >= 0) {
                    if (x == xf && y == yf && z == zf) {
                        return null;
                    }

                    boolean blockChangesX = true;
                    boolean blockChangesY = true;
                    boolean blockChangesZ = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (xf > x) {
                        d0 = (double)x + 1.0D;
                    } else if (xf < x) {
                        d0 = (double)x + 0.0D;
                    } else {
                        blockChangesX = false;
                    }

                    if (yf > y) {
                        d1 = (double)y + 1.0D;
                    } else if (yf < y) {
                        d1 = (double)y + 0.0D;
                    } else {
                        blockChangesY = false;
                    }

                    if (zf > z) {
                        d2 = (double)z + 1.0D;
                    } else if (zf < z) {
                        d2 = (double)z + 0.0D;
                    } else {
                        blockChangesZ = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double deltaX = end.xCoord - start.xCoord;
                    double deltaY = end.yCoord - start.yCoord;
                    double deltaZ = end.zCoord - start.zCoord;

                    if (blockChangesX) {
                        d3 = (d0 - start.xCoord) / deltaX;
                    }

                    if (blockChangesY) {
                        d4 = (d1 - start.yCoord) / deltaY;
                    }

                    if (blockChangesZ) {
                        d5 = (d2 - start.zCoord) / deltaZ;
                    }

                    boolean flag5 = false;
                    byte b0;

                    if (d3 < d4 && d3 < d5) {
                        if (xf > x) {
                            b0 = 4;
                        } else {
                            b0 = 5;
                        }

                        start.xCoord = d0;
                        start.yCoord += deltaY * d3;
                        start.zCoord += deltaZ * d3;
                    } else if (d4 < d5) {
                        if (yf > y) {
                            b0 = 0;
                        } else {
                            b0 = 1;
                        }

                        start.xCoord += deltaX * d4;
                        start.yCoord = d1;
                        start.zCoord += deltaZ * d4;
                    } else {
                        if (zf > z) {
                            b0 = 2;
                        } else {
                            b0 = 3;
                        }

                        start.xCoord += deltaX * d5;
                        start.yCoord += deltaY * d5;
                        start.zCoord = d2;
                    }

                    Vec3 vec32 = Vec3.createVectorHelper(start.xCoord, start.yCoord, start.zCoord);
                    x = (int)(vec32.xCoord = (double)MathHelper.floor_double(start.xCoord));

                    if (b0 == 5) {
                        --x;
                        ++vec32.xCoord;
                    }

                    y = (int)(vec32.yCoord = (double)MathHelper.floor_double(start.yCoord));

                    if (b0 == 1) {
                        --y;
                        ++vec32.yCoord;
                    }

                    z = (int)(vec32.zCoord = (double)MathHelper.floor_double(start.zCoord));

                    if (b0 == 3) {
                        --z;
                        ++vec32.zCoord;
                    }

                    Block block = world.getBlock(x, y, z);
                    boolean isLiquid = block instanceof IFluidBlock || block instanceof BlockLiquid;

                    if (cutoff.test(block) && (block.getCollisionBoundingBoxFromPool(world, x, y, z) != null || isLiquid)) {
                        if (block.canCollideCheck(world.getBlockMetadata(x, y, z), false) || isLiquid) {
                            MovingObjectPosition collision = block.collisionRayTrace(world, x, y, z, start, end);

                            if (collision != null) {
                                return collision;
                            }
                        }
                    }
                }

                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Adapted from Mekanism's mekanism.common.LaserManager#breakBlock(Coord4D, boolean, World)
    public void breakBlockNaturally(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        block.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        block.breakBlock(world, x, y, z, block, world.getBlockMetadata(x, y, z));
        world.setBlockToAir(x, y, z);
        world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block));
    }

}
