package com.min01.superduper.item;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SuperDuperUltraHyperLeadItem extends Item
{
	public SuperDuperUltraHyperLeadItem()
	{
		super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC));
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack p_41398_, Player p_41399_, LivingEntity p_41400_, InteractionHand p_41401_)
	{
		if(!(p_41400_ instanceof TamableAnimal) && !SuperDuperUtil.isTame(p_41400_) && !SuperDuperUtil.isBlacklisted(p_41400_))
		{
			SuperDuperUtil.tame(p_41400_, p_41399_);
			return InteractionResult.SUCCESS;
		}
		return super.interactLivingEntity(p_41398_, p_41399_, p_41400_, p_41401_);
	}
}
