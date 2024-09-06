package com.min01.superduper;

import com.min01.superduper.capabilities.SuperDuperCapabilities;
import com.min01.superduper.config.SuperDuperConfig;
import com.min01.superduper.item.SuperDuperItems;
import com.min01.superduper.network.SuperDuperNetwork;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
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

		SuperDuperNetwork.registerMessages();
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, SuperDuperCapabilities::attachEntityCapability);
		SuperDuperConfig.loadConfig(SuperDuperConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("super-duper-tamer.toml").toString());
	}
}
