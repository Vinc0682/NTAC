package net.newtownia.NTAC.Checks.Movement;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Vinc0682 on 30.05.2016.
 */
public class BadPackets extends AbstractMovementCheck
{
    private ActionData actionData;

    private ViolationManager vlManager;

    public BadPackets(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Bad-Packets");

        vlManager = new ViolationManager();

        loadConfig();

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(pl, ListenerPriority.HIGHEST,
                PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handlePacket(event);
            }
        });
        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run()
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    int vl = vlManager.getViolationInt(p);
                    if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
                    {
                        Location resetLoc = vlManager.getFirstViolationLocation(p);
                        if(resetLoc != null)
                            p.teleport(resetLoc);
                    }
                }
                vlManager.resetAllViolations();
            }
        }, 3, 3);
    }

    private void handlePacket(PacketEvent event)
    {
        if (!isEnabled())
            return;

        Player p = event.getPlayer();

        if (p.hasPermission("ntac.bypass.badpackets"))
            return;

        vlManager.addViolation(p, 1);
        int vl = vlManager.getViolationInt(p);
        if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
            event.setCancelled(true);
        PunishUtils.runViolationAction(p, vl, vl, actionData);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {

    }

    @Override
    public void loadConfig()
    {
        actionData = new ActionData(pl.getConfiguration(), "Bad-Packets.Actions");
    }
}
