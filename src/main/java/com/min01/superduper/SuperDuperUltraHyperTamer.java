package com.min01.superduper;

import com.min01.superduper.config.SuperDuperConfig;
import com.min01.superduper.item.SuperDuperItems;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SuperDuperUltraHyperTamer.MODID)
public class SuperDuperUltraHyperTamer 
{
	public static final String MODID = "superdupertamer";
	
	public SuperDuperUltraHyperTamer() 
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext ctx = ModLoadingContext.get();
		SuperDuperItems.ITEMS.register(bus);
		
		ctx.registerConfig(Type.COMMON, SuperDuperConfig.CONFIG_SPEC, "super-duper-tamer.toml");
	}
}
