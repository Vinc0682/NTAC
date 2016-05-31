package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientKeepAlive;
import com.comphenix.packetwrapper.WrapperPlayServerKeepAlive;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.LogUtils;
import net.newtownia.NTAC.Utils.MathUtils;
import net.newtownia.NTAC.Utils.PlayerUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
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
    private MovementBase movementBase;
    private double minVelocityY = 0.05;
    private int adjustment = 3;
    private int invalidateThreshold = 60000;
    private ActionData actionData;

    private Map<UUID, Integer> playerKeepAliveID;
    private ViolationManager vlManager;
    private Random rnd;

    private PacketAdapter keepAlivePacketEvent;

    public AntiKnockback(NTAC pl, CombatBase combatBase, MovementBase movementBase)
    {
        super(pl, combatBase, "Anti-Knockback");
        this.movementBase = movementBase;

        playerKeepAliveID = new HashMap<>();
        vlManager = new ViolationManager();
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

        loadConfig();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 60L, 60L);
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event)
    {
        if (!isEnabled())
            return;

        double vel = event.getVelocity().getY();
        if (vel < 0)
            vel *= -1;
        if (vel < minVelocityY)
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if (MathUtils.getYDiff(movementBase.getLastMovement(pUUID)) < 0)
            return;

        if (p.hasPermission("ntac.bypass.antiknockback"))
            return;
        if (!PlayerUtils.isPlayerOnGround(p))
            return;

        int keepAliveId = rnd.nextInt(20000) + 50;
        playerKeepAliveID.put(pUUID, keepAliveId);

        WrapperPlayServerKeepAlive packet = new WrapperPlayServerKeepAlive();
        packet.setKeepAliveId(keepAliveId);
        packet.sendPacket(event.getPlayer());
    }

    private void handleKeepAlivePacket(PacketEvent event)
    {
        if (!isEnabled())
            return;

        final Player p = event.getPlayer();
        final UUID pUUID = p.getUniqueId();

        if (p.hasPermission("ntac.bypass.antiknockback"))
            return;

        if (event.getPacketType() != PacketType.Play.Client.KEEP_ALIVE)
            return;

        WrapperPlayClientKeepAlive packet = new WrapperPlayClientKeepAlive(event.getPacket());
        if (!playerKeepAliveID.containsKey(pUUID) || playerKeepAliveID.get(pUUID) != packet.getKeepAliveId())
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
                    LogUtils.debug(p, "Suspected for antiknockback");
                    vlManager.addViolation(p, 1);
                    PunishUtils.runViolationAction(p, vlManager, actionData);
                }
            }
        }, adjustment);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!isEnabled())
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if (p.hasPermission("ntac.bypass.antiknockback"))
            return;

        double yDiff = event.getTo().getY() - event.getFrom().getY();
        if (yDiff > 0 && playerKeepAliveID.containsKey(pUUID) ||
                p.getFallDistance() > 1.5) // Ignore falling players
            playerKeepAliveID.remove(pUUID);
    }

    private boolean isKnockable(Player p)
    {
        if (p.isDead())
            return false;
        if (PlayerUtils.isInWeb(p.getLocation()))
            return false;
        if (PlayerUtils.isUnderBlock(p))
            return false;
        if (p.isInsideVehicle() || PlayerUtils.isGlidingWithElytra(p))
            return false;
        if (p.getPassenger() != null)
            return false;
        return true;
    }

    @Override
    public void loadConfig()
    {
        adjustment = Integer.valueOf(pl.getConfiguration().getString("Anti-Knockback.Adjustment"));
        minVelocityY = Double.valueOf(pl.getConfiguration().getString("Anti-Knockback.Minimum-Y-Velocity"));
        invalidateThreshold = Integer.valueOf(pl.getConfiguration().getString("Anti-Knockback.Invalidate-Threshold"));
        actionData = new ActionData(pl.getConfiguration(), "Anti-Knockback.Actions");
    }
}
