package com.example.hostilemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;

public class EntityAttackGoalHandler {

    // This event listener is called when any entity joins the world
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();


        // Handle spawn for entity
        handleEntitySpawn(entity);
    }

    // This event listener is called when a living entity spawns
    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.SpecialSpawn event) {
        Entity entity = event.getEntity();

        // Debug print for entity spawn event

        // Handle spawn for entity
        handleEntitySpawn(entity);
    }

    // Method to handle spawning of entities
    private void handleEntitySpawn(Entity entity) {

        // Iterate over all relationship groups defined in the configuration
        for (String groupName : ConfigHandler.relationshipGroups.keySet()) {
            ConfigHandler.RelationshipGroup group = ConfigHandler.relationshipGroups.get(groupName);

            // Check if the entity belongs to a hostile group
            if (isEntityHostile(entity, group.hostileEntities, group.hostileFactions, group.whitelistEntities)) {
                if (entity instanceof EntityCreature) {
                    EntityCreature creature = (EntityCreature) entity;
                    // Append hostile AI goals to the entity
                    appendHostileGoals(creature, group.targetEntities);
                }
            }
        }
    }

    // Method to check if an entity is hostile based on various parameters
    private boolean isEntityHostile(Entity entity, List<String> hostileEntities, List<String> hostileFactions, List<String> whitelistEntities) {
        ResourceLocation registryName = EntityList.getKey(entity);

        // Check if the entity is explicitly listed as hostile
        boolean isHostile = false;
        if (registryName != null) {
            String entityId = registryName.toString();
            isHostile = hostileEntities.contains(entityId) && !whitelistEntities.contains(entityId);
        }

        // Get the full NBT data to check the faction
        NBTTagCompound nbtData = entity.writeToNBT(new NBTTagCompound());

        // Check if the entity has the factionName NBT tag
        if (!isHostile && nbtData.hasKey("factionName")) {
            String factionName = nbtData.getString("factionName");
            isHostile = hostileFactions.contains(factionName) && !whitelistEntities.contains(factionName);
        }

        return isHostile;
    }

    public void appendHostileGoals(EntityLivingBase creature, List<String> targetEntities) {
        // Check if the creature is an instance of EntityCreature (which has targetTasks)
        if (creature instanceof EntityCreature) {
            EntityCreature entityCreature = (EntityCreature) creature;

            // Iterate through the list of target entities (could include wildcards like "millenaire:*")
            targetEntities.forEach(targetEntry -> {
                try {
                    if (targetEntry.contains("*")) {
                        // If the entry contains a wildcard (e.g., "millenaire:*"), handle the wildcard
                        String prefix = targetEntry.split(":")[0]; // Get the mod prefix (e.g., "millenaire")
                        handleWildcardTarget(entityCreature, prefix); // Pass EntityCreature instead of EntityLivingBase
                    } else {
                        // Otherwise, try to resolve the specific entity by its registry name
                        Class<? extends Entity> targetClass = getEntityClass(targetEntry);
                        if (targetClass != null && EntityLivingBase.class.isAssignableFrom(targetClass)) {
                            @SuppressWarnings("unchecked")
                            Class<? extends EntityLivingBase> livingBaseClass = (Class<? extends EntityLivingBase>) targetClass;

                            // Check if targetClass is an instance of EntityCreature
                            if (EntityCreature.class.isAssignableFrom(livingBaseClass)) {
                                // Add AI goal to target nearest attackable entity
                                entityCreature.targetTasks.addTask(6, new EntityAINearestAttackableTarget<>(entityCreature, livingBaseClass, true));
                            } else {
                                System.err.println("Target class is not a subclass of EntityCreature: " + livingBaseClass.getName());
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    // Log an error if the entity class cannot be found
                    System.err.println("Could not find entity class for " + targetEntry);
                }
            });
        } else {
            System.err.println("The creature is not an instance of EntityCreature, so targetTasks cannot be accessed.");
        }
    }


    private void handleWildcardTarget(EntityLivingBase creature, String prefix) {
        // Iterate over all registered entities in ForgeRegistries
        for (ResourceLocation entityName : ForgeRegistries.ENTITIES.getKeys()) {
            // Split the entity name into namespace and path
            String entityString = entityName.toString();
            String[] parts = entityString.split(":");

            // If the namespace matches the prefix (wildcard) passed, handle this entity
            if (parts.length == 2 && parts[0].equals(prefix)) {
                try {
                    // Get the class associated with the registry name (entityName)
                    Class<? extends Entity> targetClass = ForgeRegistries.ENTITIES.getValue(entityName).getEntityClass();
                    if (targetClass != null && EntityLivingBase.class.isAssignableFrom(targetClass)) {
                        @SuppressWarnings("unchecked")
                        Class<? extends EntityLivingBase> livingBaseClass = (Class<? extends EntityLivingBase>) targetClass;
                        // Add AI goal to target nearest attackable entity
                        if (creature instanceof EntityCreature) {
                            EntityCreature entityCreature = (EntityCreature) creature;
                            entityCreature.targetTasks.addTask(6, new EntityAINearestAttackableTarget<>(entityCreature, livingBaseClass, true));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Could not find entity class for wildcard match: " + entityName);
                }
            }
        }
    }


    // Helper method to get the entity class from a registry name (e.g., "minecraft:zombie")
    private Class<? extends Entity> getEntityClass(String registryName) throws ClassNotFoundException {
        ResourceLocation location = new ResourceLocation(registryName);

        // Retrieve the EntityEntry for the registry name
        if (ForgeRegistries.ENTITIES.containsKey(location)) {
            // Get the class for the entity
            Class<? extends Entity> entityClass = ForgeRegistries.ENTITIES.getValue(location).getEntityClass();
            return entityClass;
        } else {
            System.out.println("Entity class not found for registry name: " + registryName); // Debug print
        }
        return null;
    }

}
