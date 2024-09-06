package com.min01.superduper.event;

import com.min01.superduper.SuperDuperUltraHyperTamer;
import com.min01.superduper.ai.goal.SuperDuperFollowOwnerGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtByTargetGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtTargetGoal;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = SuperDuperUltraHyperTamer.MODID, bus = Bus.FORGE)
public class EventHandlerForge
{
	@SubscribeEvent
	public static void onLivingTick(LivingTickEvent event)
	{
		LivingEntity living = event.getEntity();
		if(SuperDuperUtil.isTame(living))
		{
			LivingEntity owner = (LivingEntity) SuperDuperUtil.getOwner(living);
			if(owner != null)
			{
				if(living instanceof Mob mob)
				{
					if(mob.getTarget() != null)
					{
						if(SuperDuperUtil.isAllay(owner, mob, mob.getTarget()) || owner.getLastHurtByMob() == null || owner.getLastHurtMob() == null)
						{
							mob.setTarget(null);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();
		Entity entity = event.getTarget();
		if(entity instanceof LivingEntity living)
		{
			if(!SuperDuperUtil.isTame(entity))
			{
				Item item = SuperDuperUtil.parseItemForTaming(entity);
				if(item != null)
				{
					if(stack.is(item))
					{
						SuperDuperUtil.tame(living, player);
						if(!player.getAbilities().instabuild)
						{
							stack.shrink(1);
						}
						event.setCancellationResult(InteractionResult.SUCCESS);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		Entity entity = event.getEntity();
		if(SuperDuperUtil.isTame(entity))
		{
			if(entity instanceof Mob mob)
			{
				mob.goalSelector.addGoal(2, new SuperDuperFollowOwnerGoal(mob, 1.3D, 4.0F, 2.0F, true));
				mob.targetSelector.addGoal(1, new SuperDuperOwnerHurtByTargetGoal(mob));
				mob.targetSelector.addGoal(2, new SuperDuperOwnerHurtTargetGoal(mob));
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
			if(SuperDuperUtil.isTame(entity))
			{
				Entity owner = SuperDuperUtil.getOwner(entity);
				if(SuperDuperUtil.isAllay(owner, entity, living))
				{
					event.setCanceled(true);
				}
			}
		}
	}
}
