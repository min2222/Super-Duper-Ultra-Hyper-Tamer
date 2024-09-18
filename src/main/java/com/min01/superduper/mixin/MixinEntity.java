package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Entity.class)
public class MixinEntity 
{
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
