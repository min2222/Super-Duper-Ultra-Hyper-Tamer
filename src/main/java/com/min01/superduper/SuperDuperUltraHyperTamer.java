package com.min01.superduper;

import com.min01.superduper.capabilities.TamerCapabilities;
import com.min01.superduper.config.SuperDuperConfig;
import com.min01.superduper.item.SuperDuperItems;
import com.min01.superduper.network.SuperDuperNetwork;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
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
		
		SuperDuperNetwork.registerMessages();
		ctx.registerConfig(Type.COMMON, SuperDuperConfig.CONFIG_SPEC, "super-duper-tamer.toml");
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, TamerCapabilities::attachEntityCapability);
	}
}
