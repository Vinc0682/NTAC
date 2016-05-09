package net.newtownia.NTAC.Utils.FakePlayer;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakePlayer
{
    private int entityID;
    private Map<UUID, Location> prevPlayerBotLocations;

    public FakePlayer(int entityID)
    {
        this.entityID = entityID;
        prevPlayerBotLocations = new HashMap<>();
    }

    public void setEntityID(int entityID)
    {
        this.entityID = entityID;
    }

    public int getEntityID()
    {
        return entityID;
    }


    public void spawnForPlayerWithIdentity(final Player p, final Location location, final Identity identity)
    {
        if(!identity.isAlreadyOnline && identity.type == EntityType.PLAYER)
            sendBotInfo(p, identity);
        //PacketGenerator.getIdentityPlayerSpawnPacket(identity, entityID, location).sendPacket(p);
        PacketGenerator.sendSpawnPacket(p, identity, entityID, location);
    }

    public void sendBotInfo(Player p, Identity id)
    {
        PacketGenerator.getInfoAddPacket(id.uuid, id.name).sendPacket(p);
    }

    public void destroyForPlayer(Player p)
    {
        PacketGenerator.getDestroyPacket(entityID).sendPacket(p);
    }

    public void despawnTablistForPlayer(Player p, Identity identity)
    {
        if (identity.type == EntityType.PLAYER)
            PacketGenerator.getInfoRemovePacket(identity.uuid, identity.name).sendPacket(p);
    }

    public void moveTo(Player p, Location loc)
    {
        UUID pUUID = p.getUniqueId();
        if (prevPlayerBotLocations.containsKey(pUUID))
        {
            Location prevLoc = prevPlayerBotLocations.get(pUUID);

            double deltaX = loc.getX() - prevLoc.getX();
            double deltaY = loc.getY() - prevLoc.getY();
            double deltaZ = loc.getZ() - prevLoc.getZ();
            double dist = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

            if (dist > 4)
                PacketGenerator.getTeleportPacket(entityID, loc).sendPacket(p);
            else
                PacketGenerator.getTeleportPacket(entityID, loc).sendPacket(p);
        }
        else
            PacketGenerator.getTeleportPacket(entityID, loc).sendPacket(p);

        prevPlayerBotLocations.put(p.getUniqueId(), loc);

    }

    public void moveAround(Player p, double angle, double distance)
    {
        moveTo(p, getAroundPos(p, angle, distance));
    }

    public static Location getAroundPos(Player p, double angle, double distance)
    {
        Location loc = p.getLocation().clone();
        double realAngle = angle + 90;

        float deltaX = (float)(distance * Math.cos(Math.toRadians(loc.getYaw() + realAngle)));
        float deltaZ = (float)(distance * Math.sin(Math.toRadians(loc.getYaw() + realAngle)));

        loc.add(deltaX, -0.1, deltaZ);

        return loc;
    }
}
