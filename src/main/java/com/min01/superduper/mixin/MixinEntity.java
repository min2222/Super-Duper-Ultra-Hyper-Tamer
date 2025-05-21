package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.superduper.config.SuperDuperConfig;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Entity.class)
public class MixinEntity 
{
	@Inject(at = @At("RETURN"), method = "interact", cancellable = true)
	protected void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
	{
		ItemStack stack = player.getItemInHand(hand);
		Entity entity = Entity.class.cast(this);
		if(cir.getReturnValue() != InteractionResult.PASS)
			return;
		if(entity instanceof LivingEntity living && !SuperDuperUtil.isBlacklisted(living))
		{
			if(!SuperDuperUtil.isTame(living))
			{
				Item item = SuperDuperUtil.parseItemForTaming(living);
				if(item != null)
				{
					if(stack.is(item))
					{
						if(Math.random() <= SuperDuperUtil.parseTameChance(living) / 100.0F)
						{
							SuperDuperUtil.tame(living, player);
						}
						else
						{
							for(int i = 0; i < 7; ++i) 
							{
								double d0 = living.level.random.nextGaussian() * 0.02D;
								double d1 = living.level.random.nextGaussian() * 0.02D;
								double d2 = living.level.random.nextGaussian() * 0.02D;
								living.level.addParticle(ParticleTypes.SMOKE, living.getRandomX(1.0D), living.getRandomY() + 0.5D, living.getRandomZ(1.0D), d0, d1, d2);
							}
						}
						if(!player.getAbilities().instabuild)
						{
							stack.shrink(1);
						}
					}
				}
				else if(SuperDuperConfig.handTame.get())
				{
					if(stack.isEmpty() && living.getHealth() <= SuperDuperUtil.percent(living.getMaxHealth(), 10))
					{
						if(SuperDuperUtil.getTameCooldown(player) <= 0)
						{
							float chance = SuperDuperUtil.parseTameChanceForHandTaming(living);
							if(Math.random() <= chance)
							{
								SuperDuperUtil.tame(living, player);
							}
							else
							{
								for(int i = 0; i < 7; ++i) 
								{
									double d0 = living.level.random.nextGaussian() * 0.02D;
									double d1 = living.level.random.nextGaussian() * 0.02D;
									double d2 = living.level.random.nextGaussian() * 0.02D;
									living.level.addParticle(ParticleTypes.SMOKE, living.getRandomX(1.0D), living.getRandomY() + 0.5D, living.getRandomZ(1.0D), d0, d1, d2);
								}
								SuperDuperUtil.setTameCooldown(player, (int) Math.floor(living.getMaxHealth() / 20) * 20);
							}
						}
						else
						{
			                player.displayClientMessage(Component.translatable("entity.superduper.cooldown", SuperDuperUtil.getTameCooldown(player)), true);
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
					if(player.getMainHandItem().isEmpty())
					{
						if(!SuperDuperUtil.isRidingBlacklisted(living))
						{
							player.startRiding(living);
						}
					}
					else
					{
						Item item = SuperDuperUtil.parseHealItem(living);
						if(item != null)
						{
							if(stack.is(item))
							{
								float amount = SuperDuperUtil.parseHealAmount(living);
								living.heal(amount);
								if(!player.getAbilities().instabuild)
								{
									stack.shrink(1);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Inject(at = @At("HEAD"), method = "isAlliedTo", cancellable = true)
	protected void isAlliedTo(Entity target, CallbackInfoReturnable<Boolean> cir)
	{
		Entity entity = Entity.class.cast(this);
		if(entity instanceof LivingEntity living)
		{
			if(SuperDuperUtil.isTame(living) && target instanceof LivingEntity livingTarget)
			{
				LivingEntity owner = SuperDuperUtil.getOwner(living);
				if(SuperDuperUtil.isAllay(owner, living, livingTarget))
				{
					cir.setReturnValue(true);
				}
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "getPassengersRidingOffset", cancellable = true)
	protected void getPassengersRidingOffset(CallbackInfoReturnable<Double> cir)
	{
		Entity entity = Entity.class.cast(this);
		if(entity instanceof LivingEntity living)
		{
			if(SuperDuperUtil.isTame(living))
			{
				if(living.getFirstPassenger() != null)
				{
					if(SuperDuperUtil.getOwner(living) == living.getFirstPassenger())
					{
						cir.setReturnValue(SuperDuperUtil.parseRideOffset(living));
					}
				}
			}
		}
	}
    
    @Shadow
    protected void setRot(float p_19916_, float p_19917_)
    {
    	
    }
    
    @Shadow
    protected void tryCheckInsideBlocks()
    {
    	
    }
}
