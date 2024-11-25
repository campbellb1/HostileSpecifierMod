package com.example.hostilemod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHandler {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Map<String, RelationshipGroup> relationshipGroups = new HashMap<>();

    public static class RelationshipGroup {
        public List<String> hostileEntities;
        public List<String> hostileFactions;
        public List<String> targetEntities;
        public List<String> whitelistEntities;

        public RelationshipGroup(List<String> hostileEntities, List<String> hostileFactions, List<String> targetEntities, List<String> whitelistEntities) {
            this.hostileEntities = hostileEntities;
            this.hostileFactions = hostileFactions;
            this.targetEntities = targetEntities;
            this.whitelistEntities = whitelistEntities;
        }
    }

    public static void init(File configFile) {
        loadRelationshipGroups(configFile);
    }

    private static void loadRelationshipGroups(File configFile) {
        if (!configFile.exists()) {
            saveDefaultRelationshipGroups(configFile);
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                Type type = new TypeToken<Map<String, RelationshipGroup>>() {}.getType();
                relationshipGroups = GSON.fromJson(reader, type);

                if (relationshipGroups == null) {
                    relationshipGroups = new HashMap<>();
                }
            } catch (Exception e) {
                System.err.println("Error loading relationship groups: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void saveDefaultRelationshipGroups(File configFile) {
        relationshipGroups.put("default", new RelationshipGroup(
                Arrays.asList("minecraft:cow", "minecraft:pig"),  // Hostile entities
                Arrays.asList("brigand"),                        // Hostile factions
                Arrays.asList("minecraft:player", "minecraft:villager"), // Target entities
                Arrays.asList("minecraft:villager")              // Whitelist entities
        ));

        saveRelationshipGroups(configFile);
    }

    public static void saveRelationshipGroups(File configFile) {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(relationshipGroups, writer);
        } catch (Exception e) {
            System.err.println("Error saving relationship groups: " + e.getMessage());
            e.printStackTrace();
        }
    }
}