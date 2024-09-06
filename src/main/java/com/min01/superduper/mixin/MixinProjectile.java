package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.Entity;
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
			if(hitResult instanceof EntityHitResult entityHit)
			{
				Entity entity = entityHit.getEntity();
				if(SuperDuperUtil.isTame(owner))
				{
					if(SuperDuperUtil.isAllay(SuperDuperUtil.getOwner(owner), owner, entity))
					{
						ci.cancel();
					}
				}
			}
		}
    }
}
