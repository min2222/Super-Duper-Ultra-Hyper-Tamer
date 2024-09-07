package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

@Mixin(Projectile.class)
public class MixinProjectile
{
	@Inject(at = @At("HEAD"), method = "onHit", cancellable = true)
    private void onHit(HitResult hitResult, CallbackInfo ci)
    {
		Projectile projectile = Projectile.class.cast(this);
		if(projectile.getOwner() != null)
		{
			Entity owner = projectile.getOwner();
			if(owner instanceof LivingEntity living)
			{
				if(hitResult instanceof EntityHitResult entityHit)
				{
					Entity entity = entityHit.getEntity();
					if(entity instanceof LivingEntity livingHit)
					{
						if(SuperDuperUtil.isTame(living))
						{
							if(SuperDuperUtil.isAllay(SuperDuperUtil.getOwner(living), living, livingHit))
							{
								ci.cancel();
							}
						}
					}
				}
			}
		}
    }
}
