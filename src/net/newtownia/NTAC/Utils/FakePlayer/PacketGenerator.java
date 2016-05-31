package net.newtownia.NTAC.Utils.FakePlayer;

import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketGenerator 
{
	public static WrapperPlayServerNamedEntitySpawn getIdentityPlayerSpawnPacket(Identity id, int entityId, Location loc)
    {
		WrapperPlayServerNamedEntitySpawn spawnPacket = new WrapperPlayServerNamedEntitySpawn();

        spawnPacket.setEntityID(entityId);
        spawnPacket.setPlayerUUID(id.uuid);

        spawnPacket.setPosition(new Vector(loc.getX(), loc.getY(), loc.getZ()));
        spawnPacket.setYaw(loc.getYaw());
        spawnPacket.setPitch(loc.getPitch());

        WrappedDataWatcher meta = new WrappedDataWatcher();
        spawnPacket.setMetadata(meta);

        return spawnPacket;
    }

    public static WrapperPlayServerSpawnEntityLiving getIdentityNotPlayerSpawnPacket(Identity id, int entityId, Location loc)
    {
        WrapperPlayServerSpawnEntityLiving spawnPacket = new WrapperPlayServerSpawnEntityLiving();

        spawnPacket.setEntityID(entityId);
        spawnPacket.setUniqueId(id.uuid);
        spawnPacket.setType(id.type);

        spawnPacket.setX(loc.getX());
        spawnPacket.setY(loc.getY());
        spawnPacket.setZ(loc.getZ());
        spawnPacket.setYaw(loc.getYaw());
        spawnPacket.setPitch(loc.getPitch());
        spawnPacket.setHeadPitch(loc.getPitch());

        WrappedDataWatcher meta = new WrappedDataWatcher();
        //if(!id.visible)
        //    meta.setObject(0, (byte) 0x20);
        spawnPacket.setMetadata(meta);

        return spawnPacket;
    }

	public static void sendSpawnPacket(Player p, Identity id, int entityId, Location loc)
	{
		if (id.type == EntityType.PLAYER)
        {
            getIdentityPlayerSpawnPacket(id, entityId, loc).sendPacket(p);
        }
        else
        {
            getIdentityNotPlayerSpawnPacket(id, entityId, loc).sendPacket(p);
        }
	}
	
	public static WrapperPlayServerEntityTeleport getTeleportPacket(int entityId, Location loc)
	{
		WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport();
		packet.setEntityID(entityId);
		packet.setX(loc.getX());
		packet.setY(loc.getY());
		packet.setZ(loc.getZ());
		packet.setPitch(loc.getPitch());
		packet.setYaw(loc.getYaw());

		return packet;
	}

	public static WrapperPlayServerRelEntityMoveLook getRelativeMovementPacket(int entityId, Vector movement)
    {
        WrapperPlayServerRelEntityMoveLook packet = new WrapperPlayServerRelEntityMoveLook();
        packet.setEntityID(entityId);
        packet.setDx(movement.getX());
        packet.setDy(movement.getY());
        packet.setDz(movement.getZ() * 32);
        packet.setOnGround(true);
        packet.setYaw(0);
        packet.setPitch(0);
        return packet;
    }
	
	public static WrapperPlayServerPlayerInfo getInfoAddPacket(UUID playerUUID, String playerName)
	{
		WrapperPlayServerPlayerInfo infoPacket = new WrapperPlayServerPlayerInfo();
		infoPacket.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
		
		WrappedGameProfile profile = new WrappedGameProfile(playerUUID, playerName);
		PlayerInfoData data = new PlayerInfoData(profile, 23, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(playerName));
		List<PlayerInfoData> dataList = new ArrayList<>();
		dataList.add(data);
		
		infoPacket.setData(dataList);	
		return infoPacket;
	}

	public static WrapperPlayServerPlayerInfo getInfoRemovePacket(UUID playerUUID, String playerName)
	{
		WrapperPlayServerPlayerInfo infoPacket = new WrapperPlayServerPlayerInfo();
		infoPacket.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

		WrappedGameProfile profile = new WrappedGameProfile(playerUUID, playerName);
		PlayerInfoData data = new PlayerInfoData(profile, 23, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(playerName));
		List<PlayerInfoData> dataList = new ArrayList<>();
		dataList.add(data);

		infoPacket.setData(dataList);
		return infoPacket;
	}
	
	public static WrapperPlayServerEntityDestroy getDestroyPacket(int entityId)
	{
		WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
		
		destroyPacket.setEntityIds(new int[]{entityId});
		
		return destroyPacket;
	}
}
