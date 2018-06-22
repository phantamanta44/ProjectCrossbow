package io.github.phantamanta44.pcrossbow;

import io.github.phantamanta44.libnine.util.WorldBlockPos;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.XbowBlocks;
import io.github.phantamanta44.pcrossbow.item.XbowItems;
import io.github.phantamanta44.pcrossbow.util.NumeralRange;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

public class CommonProxy {

    protected static final double INTENSITY_CUTOFF = 0.1D;

    public void onPreInit(FMLPreInitializationEvent event) {
        XbowCaps.init();
        // TODO Load config
        XbowBlocks.init();
        XbowItems.init();
    }

    public void onInit(FMLInitializationEvent event) {
        // TODO Register oredict entries (?)
        // TODO Register recipes
    }

    public void onPostInit(FMLPostInitializationEvent event) {
        // NO-OP
    }

    @SuppressWarnings("unchecked")
    public void doLasing(World world, Vec3d pos, Vec3d dir, float power, float initialRadius, float radiusRate) {
        dir = dir.normalize();
        Vec3d initialPos = pos.add(dir.scale(0.5D));
        double range = Math.min(Math.sqrt(power / (Math.PI * INTENSITY_CUTOFF)) / radiusRate, 128);
        Vec3d maxPotentialPos = initialPos.add(dir.scale(range));
        Vec3d traceStart = initialPos.addVector(dir.x < 0 ? -1 : 0, dir.y < 0 ? -1 : 0, dir.z < 0 ? -1 : 0);
        EnumFacing collisionFace = EnumFacing.getFacingFromVector((float)-dir.x, (float)-dir.y, (float)-dir.z);
        RayTraceResult trace = traceRay(
                world, traceStart, maxPotentialPos,
                (b, p) -> b.isOpaqueCube() || isLaserConsumer(p, collisionFace)
                        || b instanceof IFluidBlock || b instanceof BlockLiquid, true);
        if (trace != null) {
            WorldBlockPos finalPos = new WorldBlockPos(world, trace.getBlockPos());
            double distTraveledSq = finalPos.distanceSqToCenter(pos.x, pos.y, pos.z);
            double intensity = power / (initialRadius + Math.PI * radiusRate * radiusRate * distTraveledSq);
            IBlockState state = world.getBlockState(finalPos);
            if (isLaserConsumer(finalPos, collisionFace)) {
                finalPos.getTileEntity().getCapability(XbowCaps.LASER_CONSUMER, collisionFace)
                        .consumeBeam(dir, power, initialRadius + radiusRate * (float)Math.sqrt(distTraveledSq));
            } else if (state.getBlock() instanceof IFluidBlock || state.getBlock() instanceof BlockLiquid) {
                Fluid fluid = FluidRegistry.lookupFluidForBlock(state.getBlock());
                if (fluid != null && intensity * 60 >= fluid.getTemperature(world, finalPos)) {
                    breakBlockNaturally(world, finalPos);
                    world.playEvent(1004, finalPos, 0);
                    world.playEvent(2000, finalPos, 1);
                }
            } else {
                float hardness = state.getBlockHardness(world, finalPos);
                if (intensity * 0.03D >= hardness && hardness >= 0) breakBlockNaturally(world, finalPos);
            }
        }
        Vec3d finalPos = trace != null ? trace.hitVec : maxPotentialPos;
        world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(
                Math.min(initialPos.x, finalPos.x) - 0.1,
                Math.min(initialPos.y, finalPos.y) - 0.1,
                Math.min(initialPos.z, finalPos.z) - 0.1,
                Math.max(initialPos.x, finalPos.x) + 0.1,
                Math.max(initialPos.y, finalPos.y) + 0.1,
                Math.max(initialPos.z, finalPos.z) + 0.1
        )).stream()
                .filter(e -> intersectsLine(e.getEntityBoundingBox(), initialPos, finalPos))
                .forEach(e -> {
                    double intensity = power /
                            (initialRadius + Math.PI * radiusRate * radiusRate * e.getDistanceSq(initialPos.x, initialPos.y, initialPos.z));
                    if (intensity >= 1) {
                        e.setFire((int)Math.floor(intensity * 10));
                        if (e.hurtResistantTime <= 0)
                            e.attackEntityFrom(DamageSource.IN_FIRE, Double.valueOf(intensity).floatValue());
                    }
                });
    }

    public boolean isLaserConsumer(WorldBlockPos pos, EnumFacing face) {
        TileEntity tile = pos.getTileEntity();
        return tile != null && tile.hasCapability(XbowCaps.LASER_CONSUMER, face);
    }

    public boolean intersectsLine(AxisAlignedBB prism, Vec3d lineMin, Vec3d lineMax) {
        lineMin = new Vec3d(
                Math.min(lineMin.x, lineMax.x),
                Math.min(lineMin.y, lineMax.y),
                Math.min(lineMin.z, lineMax.z));
        lineMax = new Vec3d(
                Math.max(lineMin.x, lineMax.x),
                Math.max(lineMin.y, lineMax.y),
                Math.max(lineMin.z, lineMax.z));
        Vec3d k = lineMin.subtract(lineMax).normalize();
        NumeralRange domainX = NumeralRange.between(
                (prism.minX - lineMin.x) / k.x, (prism.maxX - lineMax.x) / k.x);
        if (domainX == null)
            return false;
        NumeralRange domainY = NumeralRange.between(
                (prism.minY - lineMin.y) / k.y, (prism.maxY - lineMax.y) / k.y);
        if (domainY == null)
            return false;
        NumeralRange domainZ = NumeralRange.between(
                (prism.minZ - lineMin.z) / k.z, (prism.maxZ - lineMax.z) / k.z);
        if (domainZ == null)
            return false;
        return domainX.intersect(domainY) && domainX.intersect(domainZ);
    }

    @Nullable
    public RayTraceResult traceRay(World world, Vec3d start, Vec3d end,
                                   BiPredicate<IBlockState, WorldBlockPos> cutoff) {
        return traceRay(world, start, end, cutoff, false);
    }

    @Nullable
    public RayTraceResult traceRay(World world, Vec3d pos, Vec3d end,
                                   BiPredicate<IBlockState, WorldBlockPos> cutoff, boolean considerInitial) {
        if (!Double.isNaN(pos.x) && !Double.isNaN(pos.y) && !Double.isNaN(pos.z)) {
            if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
                int xf = MathHelper.floor(end.x);
                int yf = MathHelper.floor(end.y);
                int zf = MathHelper.floor(end.z);
                int x = MathHelper.floor(pos.x);
                int y = MathHelper.floor(pos.y);
                int z = MathHelper.floor(pos.z);
                BlockPos blockPos = new BlockPos(x, y, z);
                IBlockState si = world.getBlockState(blockPos);
                Block bi = si.getBlock();
                if (considerInitial && cutoff.test(si, new WorldBlockPos(world, blockPos))
                        && (si.getCollisionBoundingBox(world, blockPos) != null
                        && (bi.canCollideCheck(si, false)) || (bi instanceof IFluidBlock || bi instanceof BlockLiquid))) {
                    RayTraceResult collision = si.collisionRayTrace(world, blockPos, pos, end);
                    if (collision != null) {
                        collision.hitVec = pos.subtract(end).normalize().subtract(collision.hitVec);
                        return collision;
                    }
                }
                int n = 200;
                while (n-- >= 0) {
                    if (Double.isNaN(pos.x) || Double.isNaN(pos.y) || Double.isNaN(pos.z)
                            || (x == xf && y == yf && z == zf)) {
                        return null;
                    }
                    boolean blockChangesX = true;
                    boolean blockChangesY = true;
                    boolean blockChangesZ = true;
                    double resultFaceBoundX = 999D;
                    double resultFaceBoundY = 999D;
                    double resultFaceBoundZ = 999D;
                    if (xf > x) {
                        resultFaceBoundX = x + 1D;
                    } else if (xf < x) {
                        resultFaceBoundX = x;
                    } else {
                        blockChangesX = false;
                    }
                    if (yf > y) {
                        resultFaceBoundY = y + 1D;
                    } else if (yf < y) {
                        resultFaceBoundY = y;
                    } else {
                        blockChangesY = false;
                    }
                    if (zf > z) {
                        resultFaceBoundZ = z + 1D;
                    } else if (zf < z) {
                        resultFaceBoundZ = z;
                    } else {
                        blockChangesZ = false;
                    }
                    double fractionInBlockX = 999.0D;
                    double fractionInBlockY = 999.0D;
                    double fractionInBlockZ = 999.0D;
                    double remainingX = end.x - pos.x;
                    double remainingY = end.y - pos.y;
                    double remainingZ = end.z - pos.z;
                    if (blockChangesX) fractionInBlockX = (resultFaceBoundX - pos.x) / remainingX;
                    if (blockChangesY) fractionInBlockY = (resultFaceBoundY - pos.y) / remainingY;
                    if (blockChangesZ) fractionInBlockZ = (resultFaceBoundZ - pos.z) / remainingZ;
                    if (fractionInBlockX == -0.0D) fractionInBlockX = -1.0E-4D;
                    if (fractionInBlockY == -0.0D) fractionInBlockY = -1.0E-4D;
                    if (fractionInBlockZ == -0.0D) fractionInBlockZ = -1.0E-4D;
                    EnumFacing dirTravelled;
                    if (fractionInBlockX < fractionInBlockY && fractionInBlockX < fractionInBlockZ) {
                        dirTravelled = xf > x ? EnumFacing.WEST : EnumFacing.EAST;
                        pos = new Vec3d(resultFaceBoundX, pos.y + remainingY * fractionInBlockX, pos.z + remainingZ * fractionInBlockX);
                    } else if (fractionInBlockY < fractionInBlockZ) {
                        dirTravelled = yf > y ? EnumFacing.DOWN : EnumFacing.UP;
                        pos = new Vec3d(pos.x + remainingX * fractionInBlockY, resultFaceBoundY, pos.z + remainingZ * fractionInBlockY);
                    } else {
                        dirTravelled = zf > z ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        pos = new Vec3d(pos.x + remainingX * fractionInBlockZ, pos.y + remainingY * fractionInBlockZ, resultFaceBoundZ);
                    }
                    x = MathHelper.floor(pos.x) - (dirTravelled == EnumFacing.EAST ? 1 : 0);
                    y = MathHelper.floor(pos.y) - (dirTravelled == EnumFacing.UP ? 1 : 0);
                    z = MathHelper.floor(pos.z) - (dirTravelled == EnumFacing.SOUTH ? 1 : 0);
                    blockPos = new BlockPos(x, y, z);
                    IBlockState currentState = world.getBlockState(blockPos);
                    Block currentBlock = currentState.getBlock();
                    boolean isLiquid = currentBlock instanceof IFluidBlock || currentBlock instanceof BlockLiquid;
                    if (cutoff.test(currentState, new WorldBlockPos(world, blockPos))
                            && (currentState.getCollisionBoundingBox(world, blockPos) != null || isLiquid)) {
                        if (isLiquid || currentBlock.canCollideCheck(currentState, false)) {
                            RayTraceResult collision = currentState.collisionRayTrace(world, blockPos, pos, end);
                            if (collision != null) return collision;
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

    public void breakBlockNaturally(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        state.getBlock().dropBlockAsItem(world, pos, state, 0);
        state.getBlock().breakBlock(world, pos, state);
        world.setBlockToAir(pos);
        world.playEvent(2001, pos, Block.getIdFromBlock(state.getBlock()));
    }

}
