package com.min01.superduper.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.min01.superduper.network.SuperDuperNetwork;
import com.min01.superduper.network.UpdateTamerCapabilityPacket;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraftforge.network.PacketDistributor;

public class TamerCapabilityImpl implements ITamerCapability
{
	private LivingEntity entity;
	private UUID ownerUUID;
	private UUID lastHurtByMobUUID;
	private UUID lastHurtMobUUID;
	private int command;
	private int cooldown;
	private final List<UUID> pets = new ArrayList<>();
	
	@Override
	public CompoundTag serializeNBT() 
	{
		CompoundTag nbt = new CompoundTag();
		if(this.ownerUUID != null)
		{
			nbt.putUUID("OwnerUUID", this.ownerUUID);
		}
		if(this.lastHurtByMobUUID != null)
		{
			nbt.putUUID("LastHurtByUUID", this.lastHurtByMobUUID);
		}
		if(this.lastHurtMobUUID != null)
		{
			nbt.putUUID("LastHurtUUID", this.lastHurtMobUUID);
		}
		nbt.putInt("Command", this.command);
		nbt.putInt("Cooldown", this.cooldown);
		ListTag list = new ListTag();
		new ArrayList<>(this.pets).forEach(t -> 
		{
			CompoundTag tag = new CompoundTag();
			tag.putUUID("PetUUID", t);
			list.add(tag);
		});
		nbt.put("Pets", list);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		if(nbt.contains("OwnerUUID"))
		{
			this.ownerUUID = nbt.getUUID("OwnerUUID");
		}
		if(nbt.contains("LastHurtByUUID"))
		{
			this.lastHurtByMobUUID = nbt.getUUID("LastHurtByUUID");
		}
		if(nbt.contains("LastHurtUUID"))
		{
			this.lastHurtMobUUID = nbt.getUUID("LastHurtUUID");
		}
		this.command = nbt.getInt("Command");
		this.cooldown = nbt.getInt("Cooldown");
		ListTag list = nbt.getList("Pets", 10);
		for(int i = 0; i < list.size(); i++)
		{
			CompoundTag tag = list.getCompound(i);
			this.pets.add(tag.getUUID("PetUUID"));
		}
	}

	@Override
	public void setEntity(LivingEntity entity)
	{
		this.entity = entity;
	}
	
	@Override
	public void update() 
	{
		if(this.getTameCooldown() > 0)
		{
			this.setTameCooldown(this.getTameCooldown() - 1);
		}
		new ArrayList<>(this.pets).forEach(t -> 
		{
			Entity entity = SuperDuperUtil.getEntityByUUID(this.entity.level, t);
			if(entity instanceof Mob mob)
			{
				if(mob.touchingUnloadedChunk() && SuperDuperUtil.isFollow(mob))
				{
					this.teleportToOwner(mob);
				}
			}
		});
		this.pets.removeIf(t ->
		{
			Entity entity = SuperDuperUtil.getEntityByUUID(this.entity.level, t);
			return entity == null;
		});
	}
	
	private void teleportToOwner(Mob mob)
	{
		BlockPos blockpos = this.entity.blockPosition();
		for(int i = 0; i < 10; ++i) 
		{
			int j = this.randomIntInclusive(-3, 3);
			int k = this.randomIntInclusive(-1, 1);
			int l = this.randomIntInclusive(-3, 3);
			boolean flag = this.maybeTeleportTo(mob, blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
			if(flag)
			{
				return;
			}
		}
	}

	private boolean maybeTeleportTo(Mob mob, int p_25304_, int p_25305_, int p_25306_)
	{
		if(Math.abs((double) p_25304_ - this.entity.getX()) < 2.0D && Math.abs((double) p_25306_ - this.entity.getZ()) < 2.0D) 
		{
			return false;
		} 
		else if(!this.canTeleportTo(mob, new BlockPos(p_25304_, p_25305_, p_25306_)))
		{
			return false;
		} 
		else
		{
			mob.moveTo((double) p_25304_ + 0.5D, (double) p_25305_, (double) p_25306_ + 0.5D, mob.getYRot(), mob.getXRot());
			mob.getNavigation().stop();
			return true;
		}
	}

	private boolean canTeleportTo(Mob mob, BlockPos p_25308_) 
	{
		BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.entity.level, p_25308_.mutable());
		if(blockpathtypes != BlockPathTypes.WALKABLE) 
		{
			return false;
		}
		else 
		{
			BlockState blockstate = this.entity.level.getBlockState(p_25308_.below());
			if(blockstate.getBlock() instanceof LeavesBlock) 
			{
				return false;
			} 
			else
			{
				BlockPos blockpos = p_25308_.subtract(mob.blockPosition());
				return this.entity.level.noCollision(mob, mob.getBoundingBox().move(blockpos));
			}
		}
	}

	private int randomIntInclusive(int p_25301_, int p_25302_)
	{
		return this.entity.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
	}

	@Override
	public void setOwner(UUID uuid) 
	{
		this.ownerUUID = uuid;
		this.sendUpdatePacket();
	}

	@Override
	public UUID getOwner() 
	{
		return this.ownerUUID;
	}

	@Override
	public void setLastHurtByMob(UUID uuid) 
	{
		this.lastHurtByMobUUID = uuid;
		this.sendUpdatePacket();
	}

	@Override
	public UUID getLastHurtByMob()
	{
		return this.lastHurtByMobUUID;
	}

	@Override
	public void setLastHurtMob(UUID uuid) 
	{
		this.lastHurtMobUUID = uuid;
		this.sendUpdatePacket();
	}

	@Override
	public UUID getLastHurtMob() 
	{
		return this.lastHurtMobUUID;
	}

	@Override
	public void setCommand(int command) 
	{
		this.command = command;
		this.sendUpdatePacket();
	}

	@Override
	public int getCommand() 
	{
		return this.command;
	}

	@Override
	public void setTameCooldown(int cooldown) 
	{
		this.cooldown = cooldown;
		this.sendUpdatePacket();
	}

	@Override
	public int getTameCooldown()
	{
		return this.cooldown;
	}
	
	@Override
	public void addPet(UUID uuid) 
	{
		this.pets.add(uuid);
		this.sendUpdatePacket();
	}
	
	@Override
	public List<UUID> getPets()
	{
		return this.pets;
	}
	
	public void sendUpdatePacket()
	{
		if(!this.entity.level.isClientSide)
		{
			SuperDuperNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateTamerCapabilityPacket(this.entity, this));
		}
	}
}
