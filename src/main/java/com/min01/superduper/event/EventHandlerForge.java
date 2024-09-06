package com.min01.superduper.event;

import com.min01.superduper.SuperDuperUltraHyperTamer;
import com.min01.superduper.ai.goal.SuperDuperFollowOwnerGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtByTargetGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtTargetGoal;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = SuperDuperUltraHyperTamer.MODID, bus = Bus.FORGE)
public class EventHandlerForge
{
	@SubscribeEvent
	public static void onLivingChangeTarget(LivingChangeTargetEvent event)
	{
		LivingEntity living = event.getEntity();
		Entity target = event.getNewTarget();
		if(target != null)
		{
			if(SuperDuperUtil.isTame(living))
			{
				LivingEntity owner = (LivingEntity) SuperDuperUtil.getOwner(living);
				if(SuperDuperUtil.isAllay(owner, living, target))
				{
					event.setCanceled(true);
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
		if(entity instanceof LivingEntity living && !SuperDuperUtil.isBlacklisted(living))
		{
			if(!SuperDuperUtil.isTame(entity))
			{
				Item item = SuperDuperUtil.parseItemForTaming(entity);
				if(item != null)
				{
					if(stack.is(item))
					{
						if(Math.random() <= SuperDuperUtil.parseTameChance(living) / 100.0F)
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
			else if(player == SuperDuperUtil.getOwner(living))
			{
				if(player.isShiftKeyDown())
				{
					int command = SuperDuperUtil.getCommand(living) + 1;
					SuperDuperUtil.setCommand(living, command >= 3 ? 0 : command);
	                player.displayClientMessage(Component.translatable("entity.superduper.all.command_" + SuperDuperUtil.getCommand(living), living.getName()), true);
				}
				else
				{
					player.startRiding(living);
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
			if(SuperDuperUtil.isTame(living))
			{
				if(living instanceof Mob mob)
				{
					mob.goalSelector.addGoal(2, new SuperDuperFollowOwnerGoal(mob, SuperDuperUtil.parseMovementSpeed(mob), 4.0F, 2.0F, true));
					mob.targetSelector.addGoal(1, new SuperDuperOwnerHurtByTargetGoal(mob));
					mob.targetSelector.addGoal(2, new SuperDuperOwnerHurtTargetGoal(mob));
				}
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
	
	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event)
	{
		Projectile projectile = event.getProjectile();
		HitResult hitResult = event.getRayTraceResult();
		if(projectile.getOwner() != null)
		{
			Entity owner = projectile.getOwner();
			if(hitResult instanceof EntityHitResult entityHit)
			{
				Entity entity = entityHit.getEntity();
				if(SuperDuperUtil.isTame(owner))
				{
					if(SuperDuperUtil.isAllay(SuperDuperUtil.getOwner(owner), owner, entity));
				}
			}
		}
	}
}
