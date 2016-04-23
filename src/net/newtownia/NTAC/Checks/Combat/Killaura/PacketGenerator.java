package net.newtownia.NTAC.Checks.Combat.Killaura;

import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityTeleport;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.*;
import net.newtownia.NTAC.Utils.Identity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketGenerator 
{
	public static WrapperPlayServerNamedEntitySpawn getInvisiblePlayerSpawnPacket(UUID playerUUID, int entityId, Location loc)
	{
		WrapperPlayServerNamedEntitySpawn spawnPacket = new WrapperPlayServerNamedEntitySpawn();
		
		spawnPacket.setEntityID(entityId);
		spawnPacket.setPlayerUUID(playerUUID);
		
		spawnPacket.setPosition(new Vector(loc.getX(), loc.getY(), loc.getZ()));
        spawnPacket.setYaw(loc.getYaw());
        spawnPacket.setPitch(loc.getPitch());
		
		WrappedDataWatcher meta = new WrappedDataWatcher();
		meta.setObject(0, (byte) 0x20);
		spawnPacket.setMetadata(meta);
		
		return spawnPacket;
	}
	
	public static WrapperPlayServerNamedEntitySpawn getVisiblePlayerSpawnPacket(UUID playerUUID, int entityId, Location loc)
	{
		WrapperPlayServerNamedEntitySpawn spawnPacket = new WrapperPlayServerNamedEntitySpawn();
		
		spawnPacket.setEntityID(entityId);
		spawnPacket.setPlayerUUID(playerUUID);
		
		spawnPacket.setPosition(new Vector(loc.getX(), loc.getY(), loc.getZ()));
        spawnPacket.setYaw(loc.getYaw());
        spawnPacket.setPitch(loc.getPitch());
		
		WrappedDataWatcher meta = new WrappedDataWatcher();
		spawnPacket.setMetadata(meta);
		
		return spawnPacket;
	}

	public static WrapperPlayServerNamedEntitySpawn getIdentitySpawnPacket(Identity id, int entityId, Location loc)
    {
		WrapperPlayServerNamedEntitySpawn spawnPacket = new WrapperPlayServerNamedEntitySpawn();

        spawnPacket.setEntityID(entityId);
        spawnPacket.setPlayerUUID(id.uuid);

        spawnPacket.setPosition(new Vector(loc.getX(), loc.getY(), loc.getZ()));
        spawnPacket.setYaw(loc.getYaw());
        spawnPacket.setPitch(loc.getPitch());

        WrappedDataWatcher meta = new WrappedDataWatcher();

        if(!id.visible)
            meta.setObject(0, (byte) 0x20);

        spawnPacket.setMetadata(meta);

        return spawnPacket;
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

	public static WrapperPlayServerPlayerInfo getInfoAddPacket(UUID playerUUID, String playerName, String displayName, EnumWrappers.PlayerInfoAction action)
	{
		WrapperPlayServerPlayerInfo infoPacket = new WrapperPlayServerPlayerInfo();
		infoPacket.setAction(action);

		WrappedGameProfile profile = new WrappedGameProfile(playerUUID, playerName);
		PlayerInfoData data = new PlayerInfoData(profile, 23, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(displayName));
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
