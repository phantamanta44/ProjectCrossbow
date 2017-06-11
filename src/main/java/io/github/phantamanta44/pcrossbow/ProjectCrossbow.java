package io.github.phantamanta44.pcrossbow;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import io.github.phantamanta44.pcrossbow.block.XbowBlocks;
import io.github.phantamanta44.pcrossbow.constant.LangConst;
import io.github.phantamanta44.pcrossbow.constant.XbowConst;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = XbowConst.MOD_ID, version = XbowConst.VERSION)
public class ProjectCrossbow {

    @Mod.Instance(XbowConst.MOD_ID)
    public static ProjectCrossbow INSTANCE;

    @SidedProxy(
            clientSide = "io.github.phantamanta44.pcrossbow.client.ClientProxy",
            serverSide = "io.github.phantamanta44.pcrossbow.CommonProxy")
    public static CommonProxy PROXY;

    public static Logger LOGGER = LogManager.getLogger(XbowConst.MOD_ID);

    public CreativeTabs creativeTab;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        creativeTab = new CreativeTabs(LangConst.CREATIVE_TAB) {

            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(XbowBlocks.laser);
            }

        };
        PROXY.onPreInit(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
		PROXY.onInit();
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit();
    }

}
