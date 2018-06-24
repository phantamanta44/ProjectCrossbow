package io.github.phantamanta44.pcrossbow;

import io.github.phantamanta44.libnine.util.WorldBlockPos;
import io.github.phantamanta44.libnine.util.function.ITriPredicate;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.XbowBlocks;
import io.github.phantamanta44.pcrossbow.item.XbowItems;
import io.github.phantamanta44.pcrossbow.util.NumeralRange;
import io.github.phantamanta44.pcrossbow.util.PhysicsUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

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
    public void doLasing(World world, Vec3d initialPos, Vec3d unnormDir,
                         double power, double initialRadius, double fluxAngle, @Nullable WorldBlockPos src) {
        Vec3d dir = unnormDir.normalize();
        double range = Math.min(PhysicsUtils.calculateRange(power, initialRadius, fluxAngle, INTENSITY_CUTOFF), 128);
        Vec3d maxPotentialPos = initialPos.add(dir.scale(range));
        ITriPredicate<IBlockState, WorldBlockPos, RayTraceResult> pred = (b, p, t) ->
                b.isOpaqueCube() || (t != null && getLaserConsumer(p, dir, power,
                        PhysicsUtils.calculateRadius(initialRadius, fluxAngle, t.hitVec.distanceTo(initialPos)), fluxAngle, t) != null);
        if (src != null) pred = pred.pre((b, p, t) -> !p.equals(src));
        RayTraceResult trace = traceRay(world, initialPos, maxPotentialPos, pred);
        if (trace != null) {
            WorldBlockPos finalBlockPos = new WorldBlockPos(world, trace.getBlockPos());
            double distTravelled = trace.hitVec.distanceTo(initialPos);
            double intensity = PhysicsUtils.calculateIntensity(power, initialRadius, fluxAngle, distTravelled);
            double radius = PhysicsUtils.calculateRadius(initialRadius, fluxAngle, distTravelled);
            IBlockState state = world.getBlockState(finalBlockPos);
            ILaserConsumer consumer = getLaserConsumer(finalBlockPos, dir, power, radius, fluxAngle, trace);
            if (consumer != null) {
                consumer.consumeBeam(trace.hitVec, dir, power, radius, fluxAngle);
            } else {
                float hardness = state.getBlockHardness(world, finalBlockPos);
                if (intensity / 25000D >= hardness && hardness >= 0) {
                    breakBlockNaturally(world, finalBlockPos);
                } else if (state.getBlock().isFlammable(world, finalBlockPos, trace.sideHit) && intensity >= 6000D) {
                    BlockPos adjPos = finalBlockPos.add(trace.sideHit.getDirectionVec());
                    if (world.isAirBlock(adjPos)) world.setBlockState(adjPos, Blocks.FIRE.getDefaultState());
                }
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
                    double intensity = PhysicsUtils.calculateIntensity(power, initialRadius, fluxAngle,
                            e.getDistance(initialPos.x, initialPos.y, initialPos.z));
                    if (intensity >= 5000) {
                        e.setFire((int)Math.floor(intensity / 250D));
                        e.attackEntityFrom(DamageSource.IN_FIRE, Double.valueOf(intensity / 5000D).floatValue());
                    }
                });
    }
    public void doLasing(World world, Vec3d initialPos, Vec3d unnormDir, double power, double initialRadius, double fluxAngle) {
        doLasing(world, initialPos, unnormDir, power, initialRadius, fluxAngle, null);
    }

    @Nullable
    public ILaserConsumer getLaserConsumer(WorldBlockPos pos, Vec3d dir,
                                           double power, double radius, double fluxAngle, RayTraceResult trace) {
        TileEntity tile = pos.getTileEntity();
        if (tile == null || !tile.hasCapability(XbowCaps.LASER_CONSUMER, trace.sideHit)) return null;
        ILaserConsumer aspect = tile.getCapability(XbowCaps.LASER_CONSUMER, trace.sideHit);
        return (aspect != null && aspect.canConsumeBeam(trace.hitVec, dir, power, radius, fluxAngle))
                ? aspect : null;
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
                                   ITriPredicate<IBlockState, WorldBlockPos, RayTraceResult> cutoff) {
        return traceRay(world, start, end, cutoff, true);
    }

    @Nullable
    public RayTraceResult traceRay(World world, Vec3d pos, Vec3d end,
                                   ITriPredicate<IBlockState, WorldBlockPos, RayTraceResult> cutoff, boolean checkInitial) {
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
                RayTraceResult collision = si.collisionRayTrace(world, blockPos, pos, end);
                if (checkInitial && cutoff.test(si, new WorldBlockPos(world, blockPos), collision)
                        && (si.getCollisionBoundingBox(world, blockPos) != null
                        && (bi.canCollideCheck(si, false)) || (bi instanceof IFluidBlock || bi instanceof BlockLiquid))) {
                    if (collision != null) {
                        collision.hitVec = collision.hitVec.subtract(collision.hitVec.subtract(pos).normalize());
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
                    collision = currentState.collisionRayTrace(world, blockPos, pos, end);
                    if (cutoff.test(currentState, new WorldBlockPos(world, blockPos), collision)
                            && (currentState.getCollisionBoundingBox(world, blockPos) != null || isLiquid)) {
                        if (isLiquid || currentBlock.canCollideCheck(currentState, false)) {
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
