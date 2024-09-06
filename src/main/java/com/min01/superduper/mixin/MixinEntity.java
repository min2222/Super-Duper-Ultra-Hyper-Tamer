package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class MixinEntity 
{
	@Inject(at = @At("HEAD"), method = "isAlliedTo", cancellable = true)
	protected void isAlliedTo(Entity target, CallbackInfoReturnable<Boolean> cir)
	{
		
	}
	
	@Inject(at = @At("HEAD"), method = "getPassengersRidingOffset", cancellable = true)
	protected void getPassengersRidingOffset(CallbackInfoReturnable<Double> cir)
	{
		
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
