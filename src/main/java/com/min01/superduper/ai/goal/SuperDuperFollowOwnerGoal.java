package com.min01.superduper.ai.goal;

import java.util.EnumSet;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class SuperDuperFollowOwnerGoal extends Goal
{
	public static final int TELEPORT_WHEN_DISTANCE_IS = 12;
	private final Mob mob;
	private LivingEntity owner;
	private final LevelReader level;
	private final double speedModifier;
	private final PathNavigation navigation;
	private int timeToRecalcPath;
	private final float stopDistance;
	private final float startDistance;
	private float oldWaterCost;
	private final boolean canFly;

	public SuperDuperFollowOwnerGoal(Mob mob, double speedModifier, float startDistance, float stopDistance, boolean canFly)
	{
		this.mob = mob;
		this.level = mob.level;
		this.speedModifier = speedModifier;
		this.navigation = mob.getNavigation();
		this.startDistance = startDistance;
		this.stopDistance = stopDistance;
		this.canFly = canFly;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		if(!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) 
		{
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean canUse() 
	{
		LivingEntity livingentity = (LivingEntity) SuperDuperUtil.getOwner(this.mob);
		if(livingentity == null)
		{
			return false;
		} 
		else if(livingentity.isSpectator())
		{
			return false;
		}
		else if(SuperDuperUtil.isSit(this.mob)) 
		{
			return false;
		}
		else if(this.mob.distanceToSqr(livingentity) < (double) (this.startDistance * this.startDistance)) 
		{
			return false;
		}
		else
		{
			this.owner = livingentity;
			return true;
		}
	}

	@Override
	public boolean canContinueToUse() 
	{
		if(this.navigation.isDone())
		{
			return false;
		}
		else if(SuperDuperUtil.isSit(this.mob))
		{
			return false;
		} 
		else 
		{
			return !(this.mob.distanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance));
		}
	}

	@Override
	public void start() 
	{
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
		this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
	}

	@Override
	public void stop()
	{
		this.owner = null;
		this.navigation.stop();
		this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
	}

	@Override
	public void tick() 
	{
		this.mob.getLookControl().setLookAt(this.owner, 10.0F, (float) this.mob.getMaxHeadXRot());
		if(--this.timeToRecalcPath <= 0) 
		{
			this.timeToRecalcPath = this.adjustedTickDelay(10);
			if(!this.mob.isLeashed() && !this.mob.isPassenger())
			{
				if(this.mob.distanceToSqr(this.owner) >= 144.0D)
				{
					this.teleportToOwner();
				} 
				else
				{
					this.navigation.moveTo(this.owner, this.speedModifier);
				}
			}
		}
	}

	private void teleportToOwner()
	{
		BlockPos blockpos = this.owner.blockPosition();
		for(int i = 0; i < 10; ++i) 
		{
			int j = this.randomIntInclusive(-3, 3);
			int k = this.randomIntInclusive(-1, 1);
			int l = this.randomIntInclusive(-3, 3);
			boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
			if(flag)
			{
				return;
			}
		}
	}

	private boolean maybeTeleportTo(int p_25304_, int p_25305_, int p_25306_)
	{
		if(Math.abs((double) p_25304_ - this.owner.getX()) < 2.0D && Math.abs((double) p_25306_ - this.owner.getZ()) < 2.0D) 
		{
			return false;
		} 
		else if(!this.canTeleportTo(new BlockPos(p_25304_, p_25305_, p_25306_)))
		{
			return false;
		} 
		else
		{
			this.mob.moveTo((double) p_25304_ + 0.5D, (double) p_25305_, (double) p_25306_ + 0.5D, this.mob.getYRot(), this.mob.getXRot());
			this.navigation.stop();
			return true;
		}
	}

	private boolean canTeleportTo(BlockPos p_25308_) 
	{
		BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, p_25308_.mutable());
		if(blockpathtypes != BlockPathTypes.WALKABLE) 
		{
			return false;
		} 
		else 
		{
			BlockState blockstate = this.level.getBlockState(p_25308_.below());
			if(!this.canFly && blockstate.getBlock() instanceof LeavesBlock) 
			{
				return false;
			} 
			else
			{
				BlockPos blockpos = p_25308_.subtract(this.mob.blockPosition());
				return this.level.noCollision(this.mob, this.mob.getBoundingBox().move(blockpos));
			}
		}
	}

	private int randomIntInclusive(int p_25301_, int p_25302_)
	{
		return this.mob.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
	}
}
