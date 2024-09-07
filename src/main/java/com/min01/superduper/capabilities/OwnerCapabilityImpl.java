package com.min01.superduper.capabilities;

import java.util.UUID;

import com.min01.superduper.network.SuperDuperNetwork;
import com.min01.superduper.network.UpdateOwnerCapabilityPacket;
import com.min01.superduper.network.UpdateOwnerCapabilityPacket.UpdateType;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

public class OwnerCapabilityImpl implements IOwnerCapability
{
	private LivingEntity entity;
	private LivingEntity owner;
	private LivingEntity lastHurtByMob;
	private LivingEntity lastHurtMob;
	private int command;
	
	@Override
	public CompoundTag serializeNBT() 
	{
		CompoundTag tag = new CompoundTag();
		if(this.owner != null)
		{
			tag.putUUID("OwnerUUID", this.owner.getUUID());
		}
		if(this.lastHurtByMob != null)
		{
			tag.putUUID("LastHurtByMobUUID", this.lastHurtByMob.getUUID());
		}
		if(this.lastHurtMob != null)
		{
			tag.putUUID("LastHurtMobUUID", this.lastHurtMob.getUUID());
		}
		tag.putInt("Command", this.command);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) 
	{
		if(nbt.contains("OwnerUUID"))
		{
			this.owner = (LivingEntity) SuperDuperUtil.getEntityByUUID(this.entity.level, nbt.getUUID("OwnerUUID"));
		}
		if(nbt.contains("LastHurtByMobUUID"))
		{
			this.lastHurtByMob = (LivingEntity) SuperDuperUtil.getEntityByUUID(this.entity.level, nbt.getUUID("LastHurtByMobUUID"));
		}
		if(nbt.contains("LastHurtMobUUID"))
		{
			this.lastHurtMob = (LivingEntity) SuperDuperUtil.getEntityByUUID(this.entity.level, nbt.getUUID("LastHurtMobUUID"));
		}
		if(nbt.contains("Command"))
		{
			this.command = nbt.getInt("Command");
		}
	}

	@Override
	public void setEntity(LivingEntity entity) 
	{
		this.entity = entity;
	}
	
	@Override
	public void setOwner(LivingEntity entity)
	{
		this.owner = entity;
		this.sendUpdatePacket(UpdateType.OWNER);
	}
	
	@Override
	public Entity getOwner() 
	{
		return this.owner;
	}
	
	@Override
	public void setLastHurtByMob(LivingEntity entity) 
	{
		this.lastHurtByMob = entity;
		this.sendUpdatePacket(UpdateType.LAST_HURT_BY_MOB);
	}
	
	@Override
	public LivingEntity getLastHurtByMob()
	{
		return this.lastHurtByMob;
	}
	
	@Override
	public void setLastHurtMob(LivingEntity entity)
	{
		this.lastHurtMob = entity;
		this.sendUpdatePacket(UpdateType.LAST_HURT_MOB);
	}
	
	@Override
	public LivingEntity getLastHurtMob() 
	{
		return this.lastHurtMob;
	}
	
	@Override
	public void setCommand(int value)
	{
		this.command = value;
		this.sendUpdatePacket(UpdateType.COMMAND);
	}
	
	@Override
	public int getCommand() 
	{
		return this.command;
	}
	
	private void sendUpdatePacket(UpdateType type) 
	{
		if(!this.entity.level.isClientSide)
		{
			switch(type)
			{
			case OWNER:
				SuperDuperNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateOwnerCapabilityPacket(this.entity, type, this.getOwner().getUUID(), UUID.randomUUID(), UUID.randomUUID(), 0));
				break;
			case LAST_HURT_BY_MOB:
				SuperDuperNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateOwnerCapabilityPacket(this.entity, type, UUID.randomUUID(), this.getLastHurtByMob().getUUID(), UUID.randomUUID(), 0));
				break;
			case LAST_HURT_MOB:
				SuperDuperNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateOwnerCapabilityPacket(this.entity, type, UUID.randomUUID(), UUID.randomUUID(), this.getLastHurtMob().getUUID(), 0));
				break;
			case COMMAND:
				SuperDuperNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateOwnerCapabilityPacket(this.entity, type, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), this.getCommand()));
				break;
			default:
				break;
			}
		}
	}
}