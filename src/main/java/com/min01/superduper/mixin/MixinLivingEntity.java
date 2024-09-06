package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends MixinEntity
{
	@Override
	protected void isAlliedTo(Entity target, CallbackInfoReturnable<Boolean> cir)
	{
		LivingEntity living = LivingEntity.class.cast(this);
		if(SuperDuperUtil.isTame(living))
		{
			Entity owner = SuperDuperUtil.getOwner(living);
			if(SuperDuperUtil.isAllay(owner, living, target))
			{
				cir.setReturnValue(true);
			}
		}
	}
	
	@Override
	protected void getPassengersRidingOffset(CallbackInfoReturnable<Double> cir)
	{
		LivingEntity living = LivingEntity.class.cast(this);
		if(SuperDuperUtil.isTame(living))
		{
			if(living.getFirstPassenger() != null)
			{
				if(SuperDuperUtil.getOwner(living) == living.getFirstPassenger())
				{
					cir.setReturnValue(SuperDuperUtil.parseMountOffset(living));
				}
			}
		}
	}
	
	@ModifyVariable(at = @At("HEAD"), method = "travel")
	private Vec3 travel(Vec3 vec3)
	{
		LivingEntity living = LivingEntity.class.cast(this);
		if(SuperDuperUtil.isTame(living))
		{
			if(living.getFirstPassenger() != null)
			{
				if(SuperDuperUtil.getOwner(living) == living.getFirstPassenger())
				{
					Entity entity = living.getFirstPassenger();
					if(entity instanceof Player player)
					{
						Vec3 travelVector = new Vec3(player.xxa, player.yya, player.zza);
				        if(player.zza != 0 || player.xxa != 0)
				        {
				        	this.setRot(player.getYRot(), player.getXRot() * 0.25F);
				            living.setYHeadRot(player.getYHeadRot());
				            ((Mob) living).setTarget(null);
				        }
				        return travelVector;
					}
				}
			}
			if(SuperDuperUtil.isSit(living))
			{
				return Vec3.ZERO;
			}
		}
		return vec3;
	}
}
