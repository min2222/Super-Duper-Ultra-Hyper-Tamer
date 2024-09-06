package com.min01.superduper.ai.goal;

import java.util.EnumSet;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class SuperDuperOwnerHurtByTargetGoal extends TargetGoal 
{
	private final Mob mob;
	private LivingEntity ownerLastHurtBy;
	private int timestamp;

	public SuperDuperOwnerHurtByTargetGoal(Mob mob) 
	{
		super(mob, false);
		this.mob = mob;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
	public boolean canUse() 
	{
		if(SuperDuperUtil.isTame(this.mob) && !SuperDuperUtil.isSit(this.mob))
		{
			LivingEntity livingentity = (LivingEntity) SuperDuperUtil.getOwner(this.mob);
			if(livingentity == null)
			{
				return false;
			}
			else 
			{
				this.ownerLastHurtBy = livingentity.getLastHurtByMob();
				int i = livingentity.getLastHurtByMobTimestamp();
				return i != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.forNonCombat());
			}
		} 
		else
		{
			return false;
		}
	}

	@Override
	public void start()
	{
		this.mob.setTarget(this.ownerLastHurtBy);
		LivingEntity livingentity = (LivingEntity) SuperDuperUtil.getOwner(this.mob);
		if(livingentity != null) 
		{
			this.timestamp = livingentity.getLastHurtByMobTimestamp();
		}
		super.start();
	}
}
