package com.min01.superduper.capabilities;

import com.min01.superduper.SuperDuperUltraHyperTamer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IOwnerCapability extends INBTSerializable<CompoundTag>
{
	ResourceLocation ID = new ResourceLocation(SuperDuperUltraHyperTamer.MODID, "owner");

	void setEntity(LivingEntity entity);
	
	void setOwner(LivingEntity entity);
	
	Entity getOwner();
	
	void setLastHurtByMob(LivingEntity entity);

	LivingEntity getLastHurtByMob();

	void setLastHurtMob(LivingEntity entity);

	LivingEntity getLastHurtMob();
	
	void setCommand(int value);
	
	int getCommand();
}
