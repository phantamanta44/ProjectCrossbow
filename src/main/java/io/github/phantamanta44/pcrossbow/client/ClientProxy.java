package io.github.phantamanta44.pcrossbow.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import io.github.phantamanta44.pcrossbow.CommonProxy;
import io.github.phantamanta44.pcrossbow.client.fx.EntityLaser;
import io.github.phantamanta44.pcrossbow.client.handler.ClientTickHandler;
import io.github.phantamanta44.pcrossbow.client.render.RenderInductorBlock;
import io.github.phantamanta44.pcrossbow.client.render.RenderLaserBlock;
import io.github.phantamanta44.pcrossbow.client.render.base.TileRenderer;
import io.github.phantamanta44.pcrossbow.tile.TileInductor;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class ClientProxy extends CommonProxy {

    public static int renderLaserBlock;
    public static int renderInductorBlock;

    @Override
    public void onInit() {
        super.onInit();
        FMLCommonHandler.instance().bus().register(new ClientTickHandler());

        renderLaserBlock = RenderingRegistry.getNextAvailableRenderId();
        registerBlockRenderer(new RenderLaserBlock());
        renderInductorBlock = RenderingRegistry.getNextAvailableRenderId();
        registerTileRenderer(TileInductor.class, new RenderInductorBlock());
    }

    @Override
    public void doLasing(World world, float x, float y, float z, Vec3 dir, float power, float initialRadius, float radiusRate) {
        if (world.isRemote) {
            dir = dir.normalize();
            Vec3 initialPos = Vec3.createVectorHelper(x + dir.xCoord * 0.5D, y + dir.yCoord * 0.5D, z + dir.zCoord * 0.5D);
            double range = Math.min(Math.sqrt(power / (Math.PI * INTENSITY_CUTOFF)) / radiusRate, 128);
            Vec3 finalPos = initialPos.addVector(range * dir.xCoord, range * dir.yCoord, range * dir.zCoord);
            Vec3 traceStart = initialPos.addVector(dir.xCoord < 0 ? -1 : 0, dir.yCoord < 0 ? -1 : 0, dir.zCoord < 0 ? -1 : 0);
            MovingObjectPosition trace = traceRay(
                    world, traceStart, finalPos,
                    b -> b.isOpaqueCube() || isLaserConsumer(b) || b instanceof IFluidBlock || b instanceof BlockLiquid, true);
            if (trace != null)
                finalPos = isLaserConsumer(world.getBlock(trace.blockX, trace.blockY, trace.blockZ))
                        ? trace.hitVec.addVector(dir.xCoord * 0.5D, dir.yCoord * 0.5D, dir.zCoord * 0.5D)
                        : trace.hitVec;
            Minecraft.getMinecraft().effectRenderer.addEffect(
                    new EntityLaser(world, initialPos, finalPos, initialRadius, power, radiusRate, 0xDD0000));
        } else {
            super.doLasing(world, x, y, z, dir, power, initialRadius, radiusRate);
        }
    }

    @Override
    public void breakBlockNaturally(World world, int x, int y, int z) {
        super.breakBlockNaturally(world, x, y, z); // I guess this works?
    }

    private <T extends TileEntity> void registerTileRenderer(Class<T> tileType, TileRenderer<T> renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileType, renderer);
        registerBlockRenderer(renderer);
    }

    private void registerBlockRenderer(ISimpleBlockRenderingHandler renderer) {
        RenderingRegistry.registerBlockHandler(renderer);
    }

}
