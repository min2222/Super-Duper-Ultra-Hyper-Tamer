package com.min01.superduper.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import com.min01.superduper.ai.goal.SuperDuperFollowOwnerGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtByTargetGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtTargetGoal;
import com.min01.superduper.capabilities.OwnerCapabilityImpl;
import com.min01.superduper.capabilities.SuperDuperCapabilities;
import com.min01.superduper.config.SuperDuperConfig;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class SuperDuperUtil 
{
	public static void tame(LivingEntity pet, LivingEntity owner)
	{
		if(!isTame(owner))
		{
			setOwner(pet, owner);
			for(int i = 0; i < 7; ++i) 
			{
				double d0 = pet.level.random.nextGaussian() * 0.02D;
				double d1 = pet.level.random.nextGaussian() * 0.02D;
				double d2 = pet.level.random.nextGaussian() * 0.02D;
				pet.level.addParticle(parseParticleForTaming(pet), pet.getRandomX(1.0D), pet.getRandomY() + 0.5D, pet.getRandomZ(1.0D), d0, d1, d2);
			}
			if(pet instanceof Mob mob)
			{
				mob.setTarget(null);
				mob.goalSelector.addGoal(2, new SuperDuperFollowOwnerGoal(mob, parseMovementSpeed(mob), 4.0F, 2.0F, true));
				mob.targetSelector.addGoal(1, new SuperDuperOwnerHurtByTargetGoal(mob));
				mob.targetSelector.addGoal(2, new SuperDuperOwnerHurtTargetGoal(mob));
			}
		}
	}
	
	public static double parseRideOffset(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.rideOffset.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String y = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				return Float.valueOf(y);
			}
		}
		return entity.getBbHeight() * 0.75D;
	}
	
	public static float parseTameChance(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.tameChance.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String chance = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				return Float.valueOf(chance);
			}
		}
		return 100.0F;
	}
	
	public static boolean isBlacklisted(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.blackList.get();
		if(!list.isEmpty())
		{
			return list.contains(location.toString());
		}
		return false;
	}
	
	public static float parseMovementSpeed(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.movementSpeed.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String speed = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				return Float.valueOf(speed);
			}
		}
		return 1.3F;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static <T extends ParticleOptions> ParticleOptions parseParticleForTaming(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.particleWhenTamed.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String particleId = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				ParticleType<T> particleType = (ParticleType<T>) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(particleId));
				try 
				{
					StringReader reader = new StringReader(particleId);
					return particleType.getDeserializer().fromCommand(particleType, reader);
				} 
				catch (CommandSyntaxException e)
				{
					e.printStackTrace();
				}
			}
		}
		return ParticleTypes.HEART;
	}
	
	public static Item parseItemForTaming(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.tamingItems.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String itemId = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				return ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
			}
		}
		return null;
	}
	
	public static boolean isAllay(LivingEntity owner, LivingEntity mob, LivingEntity target)
	{
		return owner == target || isSameOwner(mob, target);
	}
	
	public static boolean isSameOwner(LivingEntity entity, LivingEntity target)
	{
		return isTame(target) ? getOwner(target) == getOwner(entity) : false;
	}
	
	//0 == wandering
	//1 == follow
	//2 == sit
	
	public static boolean isFollow(LivingEntity entity)
	{
		return isTame(entity) && getCommand(entity) == 1;
	}
	
	public static boolean isSit(LivingEntity entity)
	{
		return isTame(entity) && getCommand(entity) == 2;
	}
	
	public static boolean isTame(LivingEntity entity)
	{
		return getOwner(entity) != null;
	}
	
	public static void setCommand(LivingEntity entiy, int command)
	{
		entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).setCommand(command);
	}
	
	public static int getCommand(LivingEntity entiy)
	{
		return entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).getCommand();
	}
	
	public static void setLastHurtByMob(LivingEntity entiy, LivingEntity mob)
	{
		entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).setLastHurtByMob(mob);
	}
	
	public static LivingEntity getLastHurtByMob(LivingEntity entiy)
	{
		return entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).getLastHurtByMob();
	}
	
	public static void setLastHurtMob(LivingEntity entiy, LivingEntity mob)
	{
		entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).setLastHurtMob(mob);
	}
	
	public static LivingEntity getLastHurtMob(LivingEntity entiy)
	{
		return entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).getLastHurtMob();
	}
	
	public static void setOwner(LivingEntity entiy, LivingEntity owner)
	{
		entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).setOwner(owner);
	}
	
	public static LivingEntity getOwner(LivingEntity entiy)
	{
		return (LivingEntity) entiy.getCapability(SuperDuperCapabilities.OWNER).orElseGet(() -> new OwnerCapabilityImpl()).getOwner();
	}
	
	@SuppressWarnings("unchecked")
	public static Entity getEntityByUUID(Level level, UUID uuid)
	{
		Method m = ObfuscationReflectionHelper.findMethod(Level.class, "m_142646_");
		try 
		{
			LevelEntityGetter<Entity> entities = (LevelEntityGetter<Entity>) m.invoke(level);
			return entities.get(uuid);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}