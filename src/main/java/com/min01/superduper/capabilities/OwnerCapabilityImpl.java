package com.min01.superduper.capabilities;

import java.util.UUID;

import com.min01.superduper.network.SuperDuperNetwork;
import com.min01.superduper.network.UpdateOwnerCapabilityPacket;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

public class OwnerCapabilityImpl implements IOwnerCapability
{
	private LivingEntity entity;
	private UUID ownerUUID;
	private int command;
	
	@Override
	public CompoundTag serializeNBT() 
	{
		CompoundTag tag = new CompoundTag();
		if(this.ownerUUID != null)
		{
			tag.putUUID("OwnerUUID", this.ownerUUID);
		}
		tag.putInt("Command", this.command);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) 
	{
		if(nbt.contains("OwnerUUID"))
		{
			this.ownerUUID = nbt.getUUID("OwnerUUID");
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
		this.ownerUUID = entity.getUUID();
		this.sendUpdatePacket();
	}
	
	@Override
	public Entity getOwner() 
	{
		return SuperDuperUtil.getEntityByUUID(this.entity.level, this.ownerUUID);
	}
	
	@Override
	public void setCommand(int value)
	{
		this.command = value;
		this.sendUpdatePacket();
	}
	
	@Override
	public int getCommand() 
	{
		return this.command;
	}
	
	private void sendUpdatePacket() 
	{
		if(this.entity instanceof ServerPlayer)
		{
			SuperDuperNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateOwnerCapabilityPacket(this.entity.getId(), this.ownerUUID));
		}
	}
}
