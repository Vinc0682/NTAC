package net.newtownia.NTAC.Checks.Combat.Killaura;

import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.Identity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FakePlayer
{
    private int entityID;

    private boolean visible = true;

    public FakePlayer(int entityID)
    {
        this.entityID = entityID;
        this.visible = true;
    }

    public FakePlayer(int entityID, boolean visible)
    {
        this.entityID = entityID;
        this.visible = visible;
    }

    public void setEntityID(int entityID)
    {
        this.entityID = entityID;
    }

    public int getEntityID()
    {
        return entityID;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public void spawnForPlayerWithIdentity(final Player p, final Location location, final Identity identity)
    {
        if(!identity.isAlreadyOnline)
            sendBotInfo(p, identity);
        PacketGenerator.getIdentitySpawnPacket(identity, entityID, location).sendPacket(p);
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
        PacketGenerator.getInfoRemovePacket(identity.uuid, identity.name).sendPacket(p);
    }

    public void moveTo(Player p, Location loc)
    {
        PacketGenerator.getTeleportPacket(entityID, loc).sendPacket(p);
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
