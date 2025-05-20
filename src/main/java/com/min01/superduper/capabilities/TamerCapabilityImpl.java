package com.min01.superduper.capabilities;

import java.util.UUID;

import com.min01.superduper.network.SuperDuperNetwork;
import com.min01.superduper.network.UpdateTamerCapabilityPacket;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;

public class TamerCapabilityImpl implements ITamerCapability
{
	private LivingEntity entity;
	private UUID ownerUUID;
	private UUID lastHurtByMobUUID;
	private UUID lastHurtMobUUID;
	private int command;
	private int cooldown;
	
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
	
	public void sendUpdatePacket()
	{
		if(!this.entity.level.isClientSide)
		{
			SuperDuperNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateTamerCapabilityPacket(this.entity, this));
		}
	}
}
