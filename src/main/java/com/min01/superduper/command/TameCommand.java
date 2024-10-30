package com.min01.superduper.command;

import java.util.Collection;

import com.min01.superduper.util.SuperDuperUtil;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
public class TameCommand 
{
	public static void register(CommandDispatcher<CommandSourceStack> p_214446_)
	{
		p_214446_.register(Commands.literal("tame").requires((p_137777_) -> 
		{
			return p_137777_.hasPermission(2);
		}).then(Commands.argument("pet", EntityArgument.entities()).then(Commands.argument("owner", EntityArgument.player()).executes((p_137810_) ->
		{
			return tame(p_137810_.getSource(), EntityArgument.getEntities(p_137810_, "pet"), EntityArgument.getPlayer(p_137810_, "owner"));
		}))));
	}
	
	private static int tame(CommandSourceStack stack, Collection<? extends Entity> pets, Player owner) 
	{
		for(Entity pet : pets)
		{
			if(pet instanceof LivingEntity living)
			{
				//stack.sendFailure(() -> Component.literal("Unable to tame " + pet.getDisplayName().getString() + ", it's already tamed."));
				SuperDuperUtil.tame(living, owner);
				stack.sendSuccess(() -> Component.literal("Tamed " + living.getDisplayName().getString() + " as owner of " + owner.getDisplayName().getString()), true);
			}
			else
			{
				stack.sendFailure(() -> Component.literal("Unable to tame " + pet.getDisplayName().getString() + ", it's not a living entity."));
			}
		}
		return pets.size();
	}
}
