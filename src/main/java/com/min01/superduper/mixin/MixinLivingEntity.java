package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends MixinEntity
{
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
						boolean jumping = ObfuscationReflectionHelper.getPrivateValue(LivingEntity.class, SuperDuperUtil.getOwner(living), "f_20899_");
						if(jumping)
						{
							if(living.onGround() || living.isInWater())
							{
								this.jumpFromGround();
							}
						}
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
	
	@Shadow
	protected void jumpFromGround()
	{
		
	}
}
