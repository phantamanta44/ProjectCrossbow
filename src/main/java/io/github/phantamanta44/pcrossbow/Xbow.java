package io.github.phantamanta44.pcrossbow;

import io.github.phantamanta44.libnine.Virtue;
import io.github.phantamanta44.libnine.util.L9CreativeTab;
import io.github.phantamanta44.pcrossbow.block.XbowBlocks;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = XbowConst.MOD_ID, version = XbowConst.VERSION, useMetadata = true)
public class Xbow extends Virtue {

    @Mod.Instance(XbowConst.MOD_ID)
    public static Xbow INSTANCE;

    @SidedProxy(
            clientSide = "io.github.phantamanta44.pcrossbow.client.ClientProxy",
            serverSide = "io.github.phantamanta44.pcrossbow.CommonProxy")
    public static CommonProxy PROXY;

    public static Logger LOGGER;

    public Xbow() {
        super(XbowConst.MOD_ID, new L9CreativeTab(XbowConst.MOD_ID, () -> new ItemStack(XbowBlocks.laser)));
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        PROXY.onPreInit(event);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
		PROXY.onInit(event);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit(event);
    }

}
