package com.min01.superduper.event;

import com.min01.superduper.SuperDuperUltraHyperTamer;
import com.min01.superduper.ai.goal.SuperDuperFollowOwnerGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtByTargetGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtTargetGoal;
import com.min01.superduper.capabilities.ITamerCapability;
import com.min01.superduper.capabilities.TamerCapabilities;
import com.min01.superduper.command.TameCommand;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = SuperDuperUltraHyperTamer.MODID, bus = Bus.FORGE)
public class EventHandlerForge
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
    	TameCommand.register(event.getDispatcher());
    }
    
	@SubscribeEvent
	public static void onLivingTick(LivingTickEvent event)
	{
		LivingEntity living = event.getEntity();
		living.getCapability(TamerCapabilities.TAMER).ifPresent(ITamerCapability::update);
		if(living instanceof Mob mob)
		{
			if(SuperDuperUtil.isTame(mob))
			{
				LivingEntity owner = SuperDuperUtil.getOwner(mob);
				if(!mob.level.isClientSide)
				{
					if(mob.getTarget() == null)
					{
						if(owner.getLastHurtByMob() != null)
						{
							if(!SuperDuperUtil.isAllay(owner, mob, owner.getLastHurtByMob()))
							{
								if(SuperDuperUtil.isWandering(mob) || SuperDuperUtil.isInAttackRange(mob, owner.getLastHurtByMob()))
								{
									mob.setTarget(owner.getLastHurtByMob());
									SuperDuperUtil.setLastHurtByMob(mob, owner.getLastHurtByMob());
								}
							}
						}
						if(owner.getLastHurtMob() != null)
						{
							if(!SuperDuperUtil.isAllay(owner, mob, owner.getLastHurtMob()))
							{
								if(SuperDuperUtil.isWandering(mob) || SuperDuperUtil.isInAttackRange(mob, owner.getLastHurtMob()))
								{
									mob.setTarget(owner.getLastHurtMob());
									SuperDuperUtil.setLastHurtMob(mob, owner.getLastHurtMob());
								}
							}
						}
					}
					else
					{
						LivingEntity target = mob.getTarget();
						if(SuperDuperUtil.getLastHurtByMob(mob) != null)
						{
							if(target != SuperDuperUtil.getLastHurtByMob(mob))
							{
								mob.setTarget(null);
							}
						}
						if(SuperDuperUtil.getLastHurtMob(mob) != null)
						{
							if(target != SuperDuperUtil.getLastHurtMob(mob))
							{
								mob.setTarget(null);
							}
						}
						if(SuperDuperUtil.getLastHurtByMob(mob) == null && SuperDuperUtil.getLastHurtMob(mob) == null)
						{
							mob.setTarget(null);
						}
						if(SuperDuperUtil.isAllay(owner, mob, target))
						{
							mob.setTarget(null);
						}
						if(!SuperDuperUtil.isInAttackRange(mob, target))
						{
							mob.setTarget(null);
						}
					}
				}
			}
			if(mob instanceof TamableAnimal animal)
			{
				if(!animal.level.isClientSide)
				{
					if(animal.getTarget() != null)
					{
						if(SuperDuperUtil.isAllay(animal.getOwner(), animal, animal.getTarget()))
						{
							animal.setTarget(null);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity living)
		{
			if(living instanceof Mob mob)
			{
				if(!mob.level.isClientSide)
				{
					mob.goalSelector.addGoal(2, new SuperDuperFollowOwnerGoal(mob, SuperDuperUtil.parseMovementSpeed(mob), 4.0F, 2.0F, true));
					mob.targetSelector.addGoal(1, new SuperDuperOwnerHurtByTargetGoal(mob));
					mob.targetSelector.addGoal(2, new SuperDuperOwnerHurtTargetGoal(mob));
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event)
	{
		LivingEntity living = event.getEntity();
		if(SuperDuperUtil.isTame(living))
		{
			LivingEntity owner = SuperDuperUtil.getOwner(living);
			if(!living.level.isClientSide && living.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && owner instanceof ServerPlayer) 
			{
				owner.sendSystemMessage(living.getCombatTracker().getDeathMessage());
			}
		}
	}
	
	@SubscribeEvent
	public static void onLivingAttack(LivingAttackEvent event)
	{
		Entity entity = event.getSource().getEntity();
		LivingEntity living = event.getEntity();
		if(entity != null)
		{
			if(entity instanceof LivingEntity attacker)
			{
				if(SuperDuperUtil.isTame(attacker))
				{
					LivingEntity owner = SuperDuperUtil.getOwner(attacker);
					if(SuperDuperUtil.isAllay(owner, attacker, living))
					{
						event.setCanceled(true);
					}
				}
			}
		}
	}
}
