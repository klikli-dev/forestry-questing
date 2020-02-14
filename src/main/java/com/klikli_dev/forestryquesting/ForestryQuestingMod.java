package com.tao.forestryquesting;

import com.tao.forestryquesting.commands.CommandSpecies;
import com.tao.forestryquesting.proxies.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by Tao on 12/14/2017.
 */
@Mod(modid = ForestryQuestingMod.MOD_ID, version = ForestryQuestingMod.VERSION, name = ForestryQuestingMod.NAME)
public class ForestryQuestingMod {
    public static final String MOD_ID = "forestryquesting";
    public static final String VERSION = "1.0.6";
    public static final String NAME = "Forestry Questing";
    public static final String PROXY = "com.tao.forestryquesting.proxies";

    @Mod.Instance(MOD_ID)
    public static ForestryQuestingMod instance;

    @SidedProxy(clientSide = PROXY + ".ClientProxy", serverSide = PROXY + ".CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSpecies());
    }
}
