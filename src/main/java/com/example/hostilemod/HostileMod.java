package com.example.hostilemod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = HostileMod.MODID, name = HostileMod.NAME, version = HostileMod.VERSION)
public class HostileMod {
    public static final String MODID = "hostilemod";
    public static final String NAME = "Hostile Mod";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "hostilemod_relationships.json");
        ConfigHandler.init(configFile);
        MinecraftForge.EVENT_BUS.register(new EntityAttackGoalHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Additional initialization logic can go here
    }
}
