package com.min01.superduper.item;

import com.min01.superduper.SuperDuperUltraHyperTamer;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SuperDuperItems 
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SuperDuperUltraHyperTamer.MODID);
	
	public static final RegistryObject<Item> SUPER_DUPER_ULTRA_HYPER_LEAD = ITEMS.register("super_duper_ultra_hyper_lead", () -> new SuperDuperUltraHyperLeadItem());
}
