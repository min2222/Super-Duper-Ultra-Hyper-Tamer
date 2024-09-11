package com.min01.superduper.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.min01.superduper.util.SuperDuperClientUtil;
import com.min01.superduper.util.SuperDuperUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public class UpdateOwnerCapabilityPacket 
{
	private final UUID entityUUID;
	private final UUID ownerUUID;
	private final UUID lastHurtByMobUUID;
	private final UUID lastHurtMobUUID;
	private final int command;
	private final UpdateType type;
	
	public static enum UpdateType
	{
		OWNER,
		LAST_HURT_BY_MOB,
		LAST_HURT_MOB,
		COMMAND
	}
	
	public UpdateOwnerCapabilityPacket(Entity entity, UpdateType type, UUID ownerUUID, UUID lastHurtByMobUUID, UUID lastHurtMobUUID, int command) 
	{
		this.entityUUID = entity.getUUID();
		this.ownerUUID = ownerUUID;
		this.lastHurtByMobUUID = lastHurtByMobUUID;
		this.lastHurtMobUUID = lastHurtMobUUID;
		this.command = command;
		this.type = type;
	}

	public UpdateOwnerCapabilityPacket(FriendlyByteBuf buf)
	{
		this.entityUUID = buf.readUUID();
		this.ownerUUID = buf.readUUID();
		this.lastHurtByMobUUID = buf.readUUID();
		this.lastHurtMobUUID = buf.readUUID();
		this.command = buf.readInt();
		this.type = UpdateType.values()[buf.readInt()];
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeUUID(this.entityUUID);
		buf.writeUUID(this.ownerUUID);
		buf.writeUUID(this.lastHurtByMobUUID);
		buf.writeUUID(this.lastHurtMobUUID);
		buf.writeInt(this.command);
		buf.writeInt(this.type.ordinal());
	}
	
	public static class Handler 
	{
		public static boolean onMessage(UpdateOwnerCapabilityPacket message, Supplier<NetworkEvent.Context> ctx) 
		{
			ctx.get().enqueueWork(() ->
			{
				if(ctx.get().getDirection().getReceptionSide().isClient())
				{
					SuperDuperClientUtil.MC.doRunTask(() -> 
					{
						Entity entity = SuperDuperUtil.getEntityByUUID(SuperDuperClientUtil.MC.level, message.entityUUID);
						if(entity instanceof LivingEntity living) 
						{
							switch(message.type)
							{
							case OWNER:
								Entity entity1 = SuperDuperUtil.getEntityByUUID(living.level, message.ownerUUID);
								if(entity1 instanceof LivingEntity owner)
								{
									SuperDuperUtil.setOwner(living, owner);
								}
								break;
							case LAST_HURT_BY_MOB:
								Entity entity2 = SuperDuperUtil.getEntityByUUID(living.level, message.lastHurtByMobUUID);
								if(entity2 instanceof LivingEntity lastHurtByMob)
								{
									SuperDuperUtil.setLastHurtByMob(living, lastHurtByMob);
								}
								break;
							case LAST_HURT_MOB:
								Entity entity3 = SuperDuperUtil.getEntityByUUID(living.level, message.lastHurtMobUUID);
								if(entity3 instanceof LivingEntity lastHurtMob)
								{
									SuperDuperUtil.setLastHurtMob(living, lastHurtMob);
								}
								break;
							case COMMAND:
								SuperDuperUtil.setCommand(living, message.command);
								break;
							default:
								break;
							}
						}
					});
				}
			});

			ctx.get().setPacketHandled(true);
			return true;
		}
	}
}