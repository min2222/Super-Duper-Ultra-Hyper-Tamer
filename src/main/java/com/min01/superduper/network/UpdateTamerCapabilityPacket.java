package com.min01.superduper.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.superduper.capabilities.ITamerCapability;
import com.min01.superduper.capabilities.TamerCapabilityImpl;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

public class UpdateTamerCapabilityPacket 
{
	private final UUID entityUUID;
	private final ITamerCapability cap;
	
	public UpdateTamerCapabilityPacket(Entity entity, ITamerCapability cap) 
	{
		this.entityUUID = entity.getUUID();
		this.cap = cap;
	}

	public UpdateTamerCapabilityPacket(FriendlyByteBuf buf)
	{
		this.entityUUID = buf.readUUID();
		TamerCapabilityImpl cap = new TamerCapabilityImpl();
		cap.deserializeNBT(buf.readNbt());
		this.cap = cap;
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
		buf.writeNbt(this.cap.serializeNBT());
	}

	public static class Handler 
	{
		public static boolean onMessage(UpdateTamerCapabilityPacket message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() ->
			{
				if(ctx.get().getDirection().getReceptionSide().isClient())
				{
					LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).filter(ClientLevel.class::isInstance).ifPresent(level -> 
					{
						Entity entity = SuperDuperUtil.getEntityByUUID(level, message.entityUUID);
						if(entity instanceof LivingEntity living)
						{
							ITamerCapability cap = message.cap;
							if(cap.getOwner() != null)
							{
								SuperDuperUtil.setOwner(living, cap.getOwner());
							}
							if(cap.getLastHurtByMob() != null)
							{
								SuperDuperUtil.setLastHurtByMob(living, cap.getLastHurtByMob());
							}
							if(cap.getLastHurtMob() != null)
							{
								SuperDuperUtil.setLastHurtMob(living, cap.getLastHurtMob());
							}
							SuperDuperUtil.setCommand(living, cap.getCommand());
							SuperDuperUtil.setTameCooldown(living, cap.getTameCooldown());
							SuperDuperUtil.getPets(living).clear();
							SuperDuperUtil.getPets(living).addAll(cap.getPets());
						}
					});
				}
			});
			ctx.get().setPacketHandled(true);
			return true;
		}
	}
}
