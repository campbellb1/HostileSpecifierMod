package com.example.hostilemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityList;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntityAttackGoalHandler {

    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.SpecialSpawn event) {
        Entity entity = event.getEntity();

        if (entity instanceof EntityCreature && shouldAddAttackGoal(entity)) {
            EntityCreature creature = (EntityCreature) entity;

            creature.tasks.addTask(5, new EntityAIAttackMelee(creature, 1.0D, false));  // Lower priority for appended behavior
            creature.targetTasks.addTask(4, new EntityAIHurtByTarget(creature, true));   // Lower priority for appended behavior

            ConfigHandler.targetEntities.forEach(targetEntry -> {
                if (targetEntry.endsWith(":*")) {
                    String namespace = targetEntry.replace(":*", "");

                    // Iterate through all registered entities to find matching namespaces
                    ForgeRegistries.ENTITIES.getValuesCollection().stream()
                            .filter(entry -> entry.getRegistryName() != null && entry.getRegistryName().getResourceDomain().equals(namespace))
                            .forEach(entry -> addTargetTask(creature, entry.getEntityClass()));
                } else {
                    try {
                        Class<? extends Entity> targetClass = getEntityClass(targetEntry);
                        if (targetClass != null) {
                            addTargetTask(creature, targetClass);
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("Could not find entity class for " + targetEntry);
                    }
                }
            });

            System.out.println("Appended hostile goals to entity: " + entity.getName());
        }
    }

    private boolean shouldAddAttackGoal(Entity entity) {
        ResourceLocation registryName = EntityList.getKey(entity);
        return registryName != null &&
                ConfigHandler.hostileEntities.contains(registryName.toString()) &&
                !ConfigHandler.whitelistEntities.contains(registryName.toString());
    }

    private void addTargetTask(EntityCreature creature, Class<? extends Entity> targetClass) {
        // Only add as a target if the target class extends EntityLivingBase
        if (EntityLivingBase.class.isAssignableFrom(targetClass)) {
            @SuppressWarnings("unchecked")
            Class<? extends EntityLivingBase> livingBaseClass = (Class<? extends EntityLivingBase>) targetClass;
            creature.targetTasks.addTask(6, new EntityAINearestAttackableTarget<>(creature, livingBaseClass, true));
        }
    }

    private Class<? extends Entity> getEntityClass(String registryName) throws ClassNotFoundException {
        ResourceLocation location = new ResourceLocation(registryName);
        if (ForgeRegistries.ENTITIES.containsKey(location)) {
            return ForgeRegistries.ENTITIES.getValue(location).getEntityClass();
        }
        return null;
    }
}