package com.example.hostilemod;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ConfigHandler {
    public static List<String> hostileEntities;
    public static List<String> whitelistEntities;
    public static List<String> targetEntities;
    private static Configuration config;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        config.load();

        hostileEntities = Arrays.asList(config.getStringList("hostileEntities", Configuration.CATEGORY_GENERAL,
                new String[]{"minecraft:cow", "minecraft:pig"}, "List of entity registry names that should be made hostile"));
        whitelistEntities = Arrays.asList(config.getStringList("whitelistEntities", Configuration.CATEGORY_GENERAL,
                new String[]{"minecraft:villager"}, "List of entity registry names that should not be attacked by modified hostile entities"));
        targetEntities = Arrays.asList(config.getStringList("targetEntities", Configuration.CATEGORY_GENERAL,
                new String[]{"minecraft:player", "minecraft:villager", "millenaire:*"},
                "List of entity registry names or namespaces that should be targeted by hostile entities"));

        if (config.hasChanged()) {
            config.save();
        }
    }
}