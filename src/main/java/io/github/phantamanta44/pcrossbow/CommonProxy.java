package io.github.phantamanta44.pcrossbow;

import io.github.phantamanta44.libnine.util.function.ITriPredicate;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.XbowBlocks;
import io.github.phantamanta44.pcrossbow.block.base.ILaserOpaque;
import io.github.phantamanta44.pcrossbow.client.gui.XbowGuis;
import io.github.phantamanta44.pcrossbow.item.XbowItems;
import io.github.phantamanta44.pcrossbow.util.PhysicsUtils;
import io.github.phantamanta44.pcrossbow.util.XbowDamage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;

public class CommonProxy {

    protected static final double INTENSITY_CUTOFF = 0.1D;

    private static final ThreadLocal<Deque<LasingTask>> lasingQueue = new ThreadLocal<>();

    public void onPreInit(FMLPreInitializationEvent event) {
        XbowCaps.init();
        // TODO Load config
        XbowBlocks.init();
        XbowItems.init();
        XbowGuis.init();
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
        if (lasingQueue.get() != null) {
            lasingQueue.get().offer(new LasingTask(world, initialPos, unnormDir, power, initialRadius, fluxAngle, src));
        } else {
            Deque<LasingTask> queue = new LinkedList<>();
            lasingQueue.set(queue);
            doLasing0(world, initialPos, unnormDir, power, initialRadius, fluxAngle, src);
            int iterations = 50;
            while (!queue.isEmpty() && --iterations > 0) queue.pop().doLasing(this);
            lasingQueue.remove();
        }
    }

    public void doLasing(World world, Vec3d initialPos, Vec3d unnormDir, double power, double initialRadius, double fluxAngle) {
        doLasing(world, initialPos, unnormDir, power, initialRadius, fluxAngle, null);
    }

    @SuppressWarnings("deprecation")
    protected void doLasing0(World world, Vec3d initialPos, Vec3d unnormDir,
                             double power, double initialRadius, double fluxAngle, @Nullable WorldBlockPos src) {
        Vec3d dir = unnormDir.normalize();
        double range = Math.min(PhysicsUtils.calculateRange(power, initialRadius, fluxAngle, INTENSITY_CUTOFF), 128);
        Vec3d maxPotentialPos = initialPos.add(dir.scale(range));
        ITriPredicate<IBlockState, WorldBlockPos, RayTraceResult> pred
                = (s, p, t) -> isLaserOpaque(s, p, t, initialPos, dir, power, initialRadius, fluxAngle);
        if (src != null) pred = pred.pre((b, p, t) -> !p.equals(src));
        RayTraceResult trace = traceRay(world, initialPos, maxPotentialPos, pred);
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
                    if (intensity >= 10000) {
                        e.setFire((int)Math.floor(intensity / 500D));
                        e.attackEntityFrom(XbowDamage.SRC_LASER, Double.valueOf(intensity / 10000D).floatValue());
                    }
                });
        if (trace != null) {
            WorldBlockPos finalBlockPos = new WorldBlockPos(world, trace.getBlockPos());
            double distTravelled = trace.hitVec.distanceTo(initialPos);
            double intensity = PhysicsUtils.calculateIntensity(power, initialRadius, fluxAngle, distTravelled);
            double radius = PhysicsUtils.calculateRadius(initialRadius, fluxAngle, distTravelled);
            IBlockState state = finalBlockPos.getBlockState();
            ILaserConsumer consumer = getLaserConsumer(finalBlockPos, dir, power, radius, fluxAngle, trace);
            if (consumer != null) trace.hitVec = consumer.getBeamEndpoint(trace.hitVec, dir, power, radius, fluxAngle);
            if (consumer != null && consumer.canConsumeBeam(trace.hitVec, dir, power, radius, fluxAngle)) {
                consumer.consumeBeam(trace.hitVec, dir, power, radius, fluxAngle);
            } else {
                float hardness = state.getBlockHardness(world, finalBlockPos.getPos());
                boolean shouldSetFire = true;
                if (intensity / 25000D >= hardness && hardness >= 0) {
                    ItemStack blockStack = finalBlockPos.getBlockState().getBlock()
                            .getPickBlock(state, null, world, finalBlockPos.getPos(), null);
                    ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(blockStack);
                    if (!smeltResult.isEmpty()) {
                        if (smeltResult.getItem() instanceof ItemBlock) {
                            Block smeltResultBlock = ((ItemBlock)smeltResult.getItem()).getBlock();
                            world.setBlockState(finalBlockPos.getPos(),
                                    smeltResultBlock.getStateFromMeta(smeltResult.getMetadata()));
                        } else {
                            world.setBlockToAir(finalBlockPos.getPos());
                            WorldUtils.dropItem(finalBlockPos, smeltResult.copy());
                        }
                        world.playEvent(2004, finalBlockPos.getPos(), 0);
                        shouldSetFire = false;
                    }
                } else if (intensity < 6000D) {
                    shouldSetFire = false;
                }
                if (shouldSetFire && state.getBlock().isFlammable(world, finalBlockPos.getPos(), trace.sideHit)) {
                    BlockPos adjPos = finalBlockPos.getPos().add(trace.sideHit.getDirectionVec());
                    if (world.isAirBlock(adjPos)) world.setBlockState(adjPos, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    protected boolean isLaserOpaque(IBlockState state, WorldBlockPos pos, @Nullable RayTraceResult trace,
                                    Vec3d initialPos, Vec3d dir, double power, double initialRadius, double fluxAngle) {
        if (trace == null) return false;
        double radius = PhysicsUtils.calculateRadius(initialRadius, fluxAngle, trace.hitVec.distanceTo(initialPos));
        return state.isOpaqueCube()
                || (getLaserConsumer(pos, dir, power, radius, fluxAngle, trace) != null)
                || (state.getBlock() instanceof ILaserOpaque && ((ILaserOpaque)state.getBlock())
                        .isOpaqueToLaser(pos, trace.hitVec, dir, power, radius, fluxAngle));
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
        double x1 = lineMin.x, x2 = lineMax.x, dx = x2 - x1;
        double y1 = lineMin.y, y2 = lineMax.y, dy = y2 - y1;
        double z1 = lineMin.z, z2 = lineMax.z, dz = z2 - z1;
        double dydx = dy / dx, dzdx = dz / dx, dzdy = dz / dy;
        double inter1, inter2;
        // project to xy plane
        inter1 = (prism.minX - x1) * dydx + y1;
        inter2 = (prism.maxX - x1) * dydx + y1;
        if (Math.min(inter1, inter2) > prism.maxY || Math.max(inter1, inter2) < prism.minY) return false;
        // project to xz plane
        inter1 = (prism.minX - x1) * dzdx + z1;
        inter2 = (prism.maxX - x1) * dzdx + z1;
        if (Math.min(inter1, inter2) > prism.maxZ || Math.max(inter1, inter2) < prism.minZ) return false;
        // project to yz plane
        inter1 = (prism.minY - y1) * dzdy + z1;
        inter2 = (prism.maxY - y1) * dzdy + z1;
        return !(Math.min(inter1, inter2) > prism.maxZ && Math.max(inter1, inter2) < prism.minZ);
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

    private static class LasingTask {

        private final World world;
        private final Vec3d initialPos;
        private final Vec3d unnormDir;
        private final double power;
        private final double initialRadius;
        private final double fluxAngle;
        @Nullable
        private final WorldBlockPos src;

        LasingTask(World world, Vec3d initialPos, Vec3d unnormDir,
                   double power, double initialRadius, double fluxAngle, @Nullable WorldBlockPos src) {
            this.world = world;
            this.initialPos = initialPos;
            this.unnormDir = unnormDir;
            this.power = power;
            this.initialRadius = initialRadius;
            this.fluxAngle = fluxAngle;
            this.src = src;
        }

        void doLasing(CommonProxy proxy) {
            proxy.doLasing0(world, initialPos, unnormDir, power, initialRadius, fluxAngle, src);
        }

    }

}
