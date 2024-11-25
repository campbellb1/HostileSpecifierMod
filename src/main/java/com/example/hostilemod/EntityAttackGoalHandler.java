package com.example.hostilemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class EntityAttackGoalHandler {

    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.SpecialSpawn event) {
        Entity entity = event.getEntity();

        for (String groupName : ConfigHandler.relationshipGroups.keySet()) {
            ConfigHandler.RelationshipGroup group = ConfigHandler.relationshipGroups.get(groupName);

            if (isEntityHostile(entity, group.hostileEntities, group.hostileFactions, group.whitelistEntities)) {
                if (entity instanceof EntityCreature) {
                    EntityCreature creature = (EntityCreature) entity;
                    appendHostileGoals(creature, group.targetEntities);
                }
            }
        }
    }

    private boolean isEntityHostile(Entity entity, List<String> hostileEntities, List<String> hostileFactions, List<String> whitelistEntities) {
        ResourceLocation registryName = EntityList.getKey(entity);

        // Check if the entity is explicitly listed as hostile
        boolean isHostile = false;
        if (registryName != null) {
            String entityId = registryName.toString();
            isHostile = hostileEntities.contains(entityId) && !whitelistEntities.contains(entityId);
        }

        // Check if the entity's faction is listed as hostile
        if (!isHostile && entity.getEntityData().hasKey("factionName")) {
            String factionName = entity.getEntityData().getString("factionName");
            isHostile = hostileFactions.contains(factionName) && !whitelistEntities.contains(factionName);
        }

        return isHostile;
    }

    private void appendHostileGoals(EntityCreature creature, List<String> targetEntities) {
        creature.tasks.addTask(5, new EntityAIAttackMelee(creature, 1.0D, false));
        creature.targetTasks.addTask(4, new EntityAIHurtByTarget(creature, true));

        targetEntities.forEach(targetEntry -> {
            try {
                Class<? extends Entity> targetClass = getEntityClass(targetEntry);
                if (targetClass != null && EntityLivingBase.class.isAssignableFrom(targetClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends EntityLivingBase> livingBaseClass = (Class<? extends EntityLivingBase>) targetClass;
                    creature.targetTasks.addTask(6, new EntityAINearestAttackableTarget<>(creature, livingBaseClass, true));
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Could not find entity class for " + targetEntry);
            }
        });
    }

    private Class<? extends Entity> getEntityClass(String registryName) throws ClassNotFoundException {
        ResourceLocation location = new ResourceLocation(registryName);
        if (net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.containsKey(location)) {
            return net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(location).getEntityClass();
        }
        return null;
    }
}
