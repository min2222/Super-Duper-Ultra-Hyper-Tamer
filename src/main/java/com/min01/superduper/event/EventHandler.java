package com.min01.superduper.event;

import com.min01.superduper.SuperDuperUltraHyperTamer;
import com.min01.superduper.item.SuperDuperItems;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SuperDuperUltraHyperTamer.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event)
    {
    	if(event.getTabKey() == CreativeModeTabs.INGREDIENTS)
    	{
    		event.accept(SuperDuperItems.SUPER_DUPER_ULTRA_HYPER_LEAD.get());
    	}
    }
}
