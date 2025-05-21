package com.min01.superduper.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import com.min01.superduper.ai.goal.SuperDuperFollowOwnerGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtByTargetGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtTargetGoal;
import com.min01.superduper.capabilities.ITamerCapability;
import com.min01.superduper.capabilities.TamerCapabilities;
import com.min01.superduper.capabilities.TamerCapabilityImpl;
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
import net.minecraft.world.entity.TamableAnimal;
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
			addPet(owner, pet);
			for(int i = 0; i < 7; ++i) 
			{
				double d0 = pet.level.random.nextGaussian() * 0.02D;
				double d1 = pet.level.random.nextGaussian() * 0.02D;
				double d2 = pet.level.random.nextGaussian() * 0.02D;
				pet.level.addParticle(parseParticleForTaming(pet), pet.getRandomX(1.0D), pet.getRandomY() + 0.5D, pet.getRandomZ(1.0D), d0, d1, d2);
			}
			if(pet instanceof Mob mob)
			{
				mob.setPersistenceRequired();
				mob.setTarget(null);
				mob.goalSelector.addGoal(2, new SuperDuperFollowOwnerGoal(mob, parseMovementSpeed(mob), 4.0F, 2.0F, true));
				mob.targetSelector.addGoal(1, new SuperDuperOwnerHurtByTargetGoal(mob));
				mob.targetSelector.addGoal(2, new SuperDuperOwnerHurtTargetGoal(mob));
			}
		}
	}
	
	public static void setTameCooldown(LivingEntity entity, int cooldown)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setTameCooldown(cooldown);
	}
	
	public static int getTameCooldown(LivingEntity entity)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		return cap.getTameCooldown();
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
	
	public static Item parseHealItem(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.healItem.get();
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
	
	public static float parseHealAmount(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.healAmount.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String amount = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				return Float.valueOf(amount);
			}
		}
		return 1.0F;
	}
	
	public static float parseTameChanceForHandTaming(LivingEntity entity)
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
		return 20.0F / entity.getMaxHealth();
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
	
	public static boolean isRidingBlacklisted(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.ridingBlacklist.get();
		if(!list.isEmpty())
		{
			return list.contains(location.toString());
		}
		return false;
	}
	
	public static boolean isBlacklisted(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.blacklist.get();
		if(!list.isEmpty())
		{
			return list.contains(location.toString());
		}
		return false;
	}
	
	public static boolean isInAttackRange(LivingEntity entity, Entity target)
	{
		return entity.distanceTo(target) <= parseAttackRange(entity);
	}
	
	public static float parseTeleportRange(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.teleportRange.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String range = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				return Float.valueOf(range);
			}
		}
		return 144.0F;
	}
	
	public static float parseAttackRange(LivingEntity entity)
	{
		ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		List<? extends String> list = SuperDuperConfig.attackRange.get();
		for(String string : list)
		{
			String mobId = string.split("=")[0];
			String range = string.split("=")[1];
			if(location.toString().equals(mobId))
			{
				return Float.valueOf(range);
			}
		}
		return 1000.0F;
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
		return 0.5F;
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
		return owner == target || isSameOwner(mob, target) || (target instanceof TamableAnimal animal && animal.isOwnedBy(owner));
	}
	
	public static boolean isSameOwner(LivingEntity entity, LivingEntity target)
	{
		return isTame(target) ? getOwner(target) == getOwner(entity) || (entity instanceof TamableAnimal animal && animal.getOwner() != null && animal.getOwner() == getOwner(target)) : false;
	}
	
	//0 == wandering
	//1 == follow
	//2 == sit
	
	public static boolean isWandering(LivingEntity entity)
	{
		return isTame(entity) && getCommand(entity) == 0;
	}
	
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
	
	public static void addPet(LivingEntity entity, LivingEntity pet)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.addPet(pet.getUUID());
	}
	
	public static List<UUID> getPets(LivingEntity entity)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		return cap.getPets();
	}
	
	public static void setCommand(LivingEntity entity, int command)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setCommand(command);
	}
	
	public static int getCommand(LivingEntity entity)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		return cap.getCommand();
	}
	
	public static void setLastHurtByMob(LivingEntity entity, UUID uuid)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setLastHurtByMob(uuid);
	}
	
	public static void setLastHurtByMob(LivingEntity entity, LivingEntity mob)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setLastHurtByMob(mob.getUUID());
	}
	
	public static LivingEntity getLastHurtByMob(LivingEntity entity)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		Entity mob = getEntityByUUID(entity.level, cap.getLastHurtByMob());
		return (LivingEntity) mob;
	}
	
	public static void setLastHurtMob(LivingEntity entity, UUID uuid)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setLastHurtMob(uuid);
	}
	
	public static void setLastHurtMob(LivingEntity entity, LivingEntity mob)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setLastHurtMob(mob.getUUID());
	}
	
	public static LivingEntity getLastHurtMob(LivingEntity entity)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		Entity mob = getEntityByUUID(entity.level, cap.getLastHurtMob());
		return (LivingEntity) mob;
	}
	
	public static void setOwner(LivingEntity entity, UUID uuid)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setOwner(uuid);
	}
	
	public static void setOwner(LivingEntity entity, LivingEntity owner)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		cap.setOwner(owner.getUUID());
	}
	
	public static LivingEntity getOwner(LivingEntity entity)
	{
		ITamerCapability cap = entity.getCapability(TamerCapabilities.TAMER).orElse(new TamerCapabilityImpl());
		Entity owner = getEntityByUUID(entity.level, cap.getOwner());
		return (LivingEntity) owner;
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
	
	public static float percent(float baseValue, float percent)
	{
		return baseValue * percent / 100;
	}
}
