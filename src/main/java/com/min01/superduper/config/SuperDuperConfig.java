package com.min01.superduper.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class SuperDuperConfig 
{
    private static ForgeConfigSpec.Builder BUILDER;
    public static ForgeConfigSpec CONFIG;

	public static ConfigValue<List<? extends String>> blacklist;
	public static ConfigValue<List<? extends String>> tameChance;
	public static ForgeConfigSpec.BooleanValue forceTame;
	public static ConfigValue<List<? extends String>> particleWhenTamed;
	public static ConfigValue<List<? extends String>> rideOffset;
	public static ConfigValue<List<? extends String>> tamingItems;
	public static ConfigValue<List<? extends String>> movementSpeed;
	public static ConfigValue<List<? extends String>> ridingBlacklist;
    
    public static void loadConfig(ForgeConfigSpec config, String path) 
    {
        CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }
    
    static 
    {
    	BUILDER = new ForgeConfigSpec.Builder();
    	SuperDuperConfig.init(SuperDuperConfig.BUILDER);
    	CONFIG = SuperDuperConfig.BUILDER.build();
    }
	
    public static void init(ForgeConfigSpec.Builder config) 
    {
    	config.push("Balance Settings");
    	SuperDuperConfig.blacklist = config.comment("blacklisted mobs can't be tamed. example : minecraft:warden").define("blacklist", Arrays.asList(new String[] {
    			"minecraft:warden"
    	}), String.class::isInstance);
    	SuperDuperConfig.tameChance = config.comment("tame success chance for mobs. example : minecraft:husk=80.0").define("tameChance", Arrays.asList(new String[] {
    			"minecraft:husk=80.0"
    	}), String.class::isInstance);
    	SuperDuperConfig.forceTame = config.comment("enable force tame, which is able to tame any mob even if it's already tamable with other method").define("forceTame", true);
        config.pop();
    	
    	config.push("Client Settings");
    	SuperDuperConfig.particleWhenTamed = config.comment("particle for when successfully tamed mobs. example : minecraft:husk=minecraft:damage_indicator").define("particleWhenTamed", Arrays.asList(new String[] {
    			"minecraft:husk=minecraft:damage_indicator"
    	}), String.class::isInstance);
    	SuperDuperConfig.rideOffset = config.comment("y offset of passenger when ride tamed mob. example : minecraft:husk=0.5").define("mountOffset", Arrays.asList(new String[] {}), String.class::isInstance);
        config.pop();
        
    	config.push("Common Settings");
    	SuperDuperConfig.tamingItems = config.comment("item needed for tame mobs. example : minecraft:husk=minecraft:rotten_flesh").define("tamingItems", Arrays.asList(new String[] {
    			"minecraft:husk=minecraft:rotten_flesh"
    	}), String.class::isInstance);
    	SuperDuperConfig.movementSpeed = config.comment("movementSpeed for when pets following owner or controlled by owner. example : minecraft:husk=1.3").define("movementSpeed", Arrays.asList(new String[] {
    			"minecraft:husk=1.3"
    	}), String.class::isInstance);
    	SuperDuperConfig.ridingBlacklist = config.comment("disable riding of specific mob. example : minecraft:husk").define("ridingBlacklist", Arrays.asList(new String[] {}), String.class::isInstance);
        config.pop();
    }
}
