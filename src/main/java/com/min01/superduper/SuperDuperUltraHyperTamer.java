package com.min01.superduper;

import com.min01.superduper.config.SuperDuperConfig;
import com.min01.superduper.item.SuperDuperItems;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(SuperDuperUltraHyperTamer.MODID)
public class SuperDuperUltraHyperTamer 
{
	public static final String MODID = "superdupertamer";
	
	public SuperDuperUltraHyperTamer() 
	{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		SuperDuperItems.ITEMS.register(bus);
		
		SuperDuperConfig.loadConfig(SuperDuperConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("super-duper-tamer.toml").toString());
	}
}
