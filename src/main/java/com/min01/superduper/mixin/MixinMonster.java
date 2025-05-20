package com.min01.superduper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

@Mixin(Monster.class)
public class MixinMonster
{
	@Inject(at = @At("RETURN"), method = "isPreventingPlayerRest", cancellable = true)
    private void isPreventingPlayerRest(Player player, CallbackInfoReturnable<Boolean> cir)
    {
		if(cir.getReturnValueZ() && SuperDuperUtil.isTame(Monster.class.cast(this)))
		{
			cir.setReturnValue(false);
		}
    }
}
