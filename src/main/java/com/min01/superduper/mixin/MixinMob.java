package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.min01.superduper.ai.goal.SuperDuperFollowOwnerGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtByTargetGoal;
import com.min01.superduper.ai.goal.SuperDuperOwnerHurtTargetGoal;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public class MixinMob 
{
	@Inject(at = @At("TAIL"), method = "registerGoals", cancellable = true)
	protected void registerGoals(CallbackInfo ci)
	{
		Mob mob = Mob.class.cast(this);
		mob.goalSelector.addGoal(2, new SuperDuperFollowOwnerGoal(mob, SuperDuperUtil.parseMovementSpeed(mob), 4.0F, 2.0F, true));
		mob.targetSelector.addGoal(1, new SuperDuperOwnerHurtByTargetGoal(mob));
		mob.targetSelector.addGoal(2, new SuperDuperOwnerHurtTargetGoal(mob));
	}
}
