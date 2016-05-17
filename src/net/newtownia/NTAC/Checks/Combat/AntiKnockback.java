package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientKeepAlive;
import com.comphenix.packetwrapper.WrapperPlayServerKeepAlive;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.MaterialUtils;
import net.newtownia.NTAC.Utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AntiKnockback extends AbstractCombatCheck
{
    private double minVelocityY = 0.001;
    private int graceTime = 3;

    private Map<UUID, Integer> playerKeepAliveID;
    private Map<UUID, Double> playerHitY;
    private Random rnd;

    private PacketAdapter keepAlivePacketEvent;

    public AntiKnockback(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Anti-Knockback");

        playerKeepAliveID = new HashMap<>();
        playerHitY = new HashMap<>();
        rnd = new Random(System.currentTimeMillis());
        keepAlivePacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.KEEP_ALIVE)
        {
            @Override
            public void onPacketReceiving(PacketEvent event)
            {
                handleKeepAlivePacket(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(keepAlivePacketEvent);
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event)
    {
        double x = event.getVelocity().getX();
        double y = event.getVelocity().getY();
        double z = event.getVelocity().getZ();
        if (y < minVelocityY)
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        playerHitY.put(pUUID, p.getLocation().getY());

        int keepAliveId = rnd.nextInt(20000) + 50;
        playerKeepAliveID.put(pUUID, keepAliveId);

        WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive();
        packet.setKeepAliveId(keepAliveId);
        packet.sendPacket(event.getPlayer());
    }

    private void handleKeepAlivePacket(PacketEvent event)
    {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if (event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE)
            return;

        WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event.getPacket());

        if (playerKeepAliveID.getOrDefault(pUUID, -1) != packet.getKeepAliveId())
            return;
        event.setCancelled(true); // Don't affect tablist ping

        if (!isKnockable(p))
            return;

        Bukkit.getScheduler().runTaskLater(pl, new Runnable()
        {
            @Override
            public void run()
            {
                if (!isKnockable(p))
                    return;

                if (playerKeepAliveID.containsKey(pUUID))
                {
                    p.sendMessage("Anti-Knockback?");
                }
            }
        }, graceTime);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        double yDiff = event.getTo().getY() - event.getFrom().getY();
        if (yDiff > 0 && playerKeepAliveID.containsKey(pUUID))
        {
            playerKeepAliveID.remove(pUUID);
            playerHitY.remove(pUUID);
        }
    }

    private boolean isKnockable(Player p)
    {
        if (p.isDead())
            return false;
        if (PlayerUtils.isInWeb(p.getLocation()))
            return false;
        Block blockAbove = p.getEyeLocation().getBlock().getRelative(BlockFace.UP);
        if (blockAbove != null && !MaterialUtils.isUnsolid(blockAbove))
            return false;
        return true;
    }

    @Override
    public void loadConfig()
    {

    }
}
