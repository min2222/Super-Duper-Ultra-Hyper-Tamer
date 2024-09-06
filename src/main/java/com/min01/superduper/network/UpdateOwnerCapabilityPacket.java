package com.min01.superduper.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.superduper.capabilities.SuperDuperCapabilities;
import com.min01.superduper.util.SuperDuperClientUtil;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public class UpdateOwnerCapabilityPacket 
{
	private final int entityID;
	private final UUID ownerUUID;
	
	public UpdateOwnerCapabilityPacket(int id, UUID ownerUUID) 
	{
		this.entityID = id;
		this.ownerUUID = ownerUUID;
	}

	public UpdateOwnerCapabilityPacket(FriendlyByteBuf buf)
	{
		this.entityID = buf.readInt();
		this.ownerUUID = buf.readUUID();
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.entityID);
		buf.writeUUID(this.ownerUUID);
	}
	
	public static class Handler 
	{
		public static boolean onMessage(UpdateOwnerCapabilityPacket message, Supplier<NetworkEvent.Context> ctx) 
		{
			ctx.get().enqueueWork(() ->
			{
				Entity entity = SuperDuperClientUtil.MC.level.getEntity(message.entityID);
				if(entity instanceof LivingEntity living) 
				{
					living.getCapability(SuperDuperCapabilities.OWNER).ifPresent(cap -> 
					{
						Entity owner = SuperDuperUtil.getEntityByUUID(living.level, message.ownerUUID);
						if(owner instanceof LivingEntity livingOwner)
						{
							cap.setOwner(livingOwner);
						}
					});
				}
			});

			ctx.get().setPacketHandled(true);
			return true;
		}
	}
}
