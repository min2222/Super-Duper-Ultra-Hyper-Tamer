package com.min01.superduper.capabilities;

import java.util.List;
import java.util.UUID;

import com.min01.superduper.SuperDuperUltraHyperTamer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface ITamerCapability extends INBTSerializable<CompoundTag>
{
	ResourceLocation ID = new ResourceLocation(SuperDuperUltraHyperTamer.MODID, "tamer");

	void setEntity(LivingEntity entity);
	
	void update();

	void setOwner(UUID uuid);
	
	UUID getOwner();
	
	void setLastHurtByMob(UUID uuid);
	
	UUID getLastHurtByMob();
	
	void setLastHurtMob(UUID uuid);
	
	UUID getLastHurtMob();

	void setCommand(int command);
	
	int getCommand();
	
	void setTameCooldown(int cooldown);
	
	int getTameCooldown();
	
	void addPet(UUID uuid);
	
	List<UUID> getPets();
}
