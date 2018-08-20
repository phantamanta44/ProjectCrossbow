package io.github.phantamanta44.pcrossbow;

import io.github.phantamanta44.libnine.util.tuple.IPair;
import io.github.phantamanta44.libnine.util.world.WorldBlockPos;
import io.github.phantamanta44.libnine.util.world.WorldUtils;
import io.github.phantamanta44.pcrossbow.api.capability.ILaserConsumer;
import io.github.phantamanta44.pcrossbow.api.capability.XbowCaps;
import io.github.phantamanta44.pcrossbow.block.base.ILaserBlock;
import io.github.phantamanta44.pcrossbow.util.PhysicsUtils;
import io.github.phantamanta44.pcrossbow.util.XbowDamage;
import io.github.phantamanta44.pcrossbow.wsd.XbowWSDs;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;

public class CommonProxy {

    protected static final double INTENSITY_CUTOFF = 0.1D;

    protected static final ThreadLocal<Deque<LasingTask>> lasingQueue = new ThreadLocal<>();

    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        // TODO Load config
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
        IPair<LasingResult, RayTraceResult> result = traceRay(world, initialPos, maxPotentialPos, dir, src, power, initialRadius, fluxAngle);
        RayTraceResult trace = result == null ? null : result.getB();
        Vec3d finalPos = trace != null ? trace.hitVec : maxPotentialPos;
        world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(
                Math.min(initialPos.x, finalPos.x) - 0.1,
                Math.min(initialPos.y, finalPos.y) - 0.1,
                Math.min(initialPos.z, finalPos.z) - 0.1,
                Math.max(initialPos.x, finalPos.x) + 0.1,
                Math.max(initialPos.y, finalPos.y) + 0.1,
                Math.max(initialPos.z, finalPos.z) + 0.1
        )).stream()
                .filter(e -> PhysicsUtils.intersectsLine(e.getEntityBoundingBox(), initialPos, finalPos))
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
            IBlockState state = finalBlockPos.getBlockState();
            if (result.getA() == LasingResult.OBSTRUCT) {
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

    @Nullable
    protected ILaserConsumer getLaserConsumer(WorldBlockPos pos, EnumFacing dir) {
        TileEntity tile = pos.getTileEntity();
        return (tile != null && tile.hasCapability(XbowCaps.LASER_CONSUMER, dir))
                ? tile.getCapability(XbowCaps.LASER_CONSUMER, dir) : null;
    }

    protected LasingResult tryLasing(IBlockState state, WorldBlockPos pos, @Nullable RayTraceResult trace,
                                   Vec3d initialPos, Vec3d dir, double power, double initialRadius, double fluxAngle) {
        if (trace == null) return LasingResult.PASS;
        double radius = PhysicsUtils.calculateRadius(initialRadius, fluxAngle, trace.hitVec.distanceTo(initialPos));
        ILaserConsumer consumer = getLaserConsumer(pos, trace.sideHit);
        if (consumer != null) {
            trace.hitVec = consumer.getBeamEndpoint(trace.hitVec, dir, trace.sideHit, power, radius, fluxAngle);
            Xbow.INSTANCE.getWsdManager().get(XbowWSDs.LASER_CONSUMER_TRACKER, pos.getWorld()).markLased(pos.getPos());
            return consumer.consumeBeam(trace.hitVec, dir, trace.sideHit, power, radius, fluxAngle);
        }
        if (state.getBlock() instanceof ILaserBlock) {
            Xbow.INSTANCE.getWsdManager().get(XbowWSDs.LASER_CONSUMER_TRACKER, pos.getWorld()).markLased(pos.getPos());
            return ((ILaserBlock)state.getBlock()).getLasingResult(pos, trace.hitVec, dir, trace.sideHit, power, radius, fluxAngle);
        }
        return state.isOpaqueCube() ? LasingResult.OBSTRUCT : LasingResult.PASS;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Xbow.INSTANCE.getWsdManager().get(XbowWSDs.LASER_CONSUMER_TRACKER, event.world).update();
        }
    }

    @Nullable
    protected IPair<LasingResult, RayTraceResult> traceRay(World world, Vec3d initialPos, Vec3d end, Vec3d dir, @Nullable WorldBlockPos src,
                                                         double power, double initialRadius, double fluxAngle) {
        if (!Double.isNaN(initialPos.x) && !Double.isNaN(initialPos.y) && !Double.isNaN(initialPos.z)) {
            if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
                int xf = MathHelper.floor(end.x);
                int yf = MathHelper.floor(end.y);
                int zf = MathHelper.floor(end.z);
                int x = MathHelper.floor(initialPos.x);
                int y = MathHelper.floor(initialPos.y);
                int z = MathHelper.floor(initialPos.z);
                BlockPos blockPos = new BlockPos(x, y, z);
                RayTraceResult collision;
                LasingResult lasing;
                if (src == null || !blockPos.equals(src.getPos())) {
                    IBlockState si = world.getBlockState(blockPos);
                    Block bi = si.getBlock();
                    collision = si.collisionRayTrace(world, blockPos, initialPos, end);
                    lasing = tryLasing(si, new WorldBlockPos(world, blockPos), collision, initialPos, dir, power, initialRadius, fluxAngle);
                    if (lasing != LasingResult.PASS && (si.getCollisionBoundingBox(world, blockPos) != null
                            && (bi.canCollideCheck(si, false)) || (bi instanceof IFluidBlock || bi instanceof BlockLiquid))) {
                        if (collision != null) {
                            collision.hitVec = collision.hitVec.subtract(dir);
                            return IPair.of(lasing, collision);
                        }
                    }
                }
                Vec3d pos = initialPos;
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
                    lasing = tryLasing(currentState, new WorldBlockPos(world, blockPos), collision, initialPos, dir,
                            power, initialRadius, fluxAngle);
                    if (lasing != LasingResult.PASS && (currentState.getCollisionBoundingBox(world, blockPos) != null || isLiquid)) {
                        if (isLiquid || currentBlock.canCollideCheck(currentState, false)) {
                            if (collision != null) return IPair.of(lasing, collision);
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

    protected static class LasingTask {

        protected final World world;
        protected final Vec3d initialPos;
        protected final Vec3d unnormDir;
        protected final double power;
        protected final double initialRadius;
        protected final double fluxAngle;
        @Nullable
        protected final WorldBlockPos src;

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
