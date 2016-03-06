package net.newtownia.NTAC.Checks.Movement;

import com.comphenix.packetwrapper.WrapperPlayClientEntityAction;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Sneak extends AbstractMovementCheck
{
    int sneakThreshold = 40;
    int invalidateThreshold = 1000;
    int invalidateFreq = 5;

    ViolationManager vlManger;
    ActionData actionData;

    PacketAdapter sneakPacketEvent;
    Map<UUID, Long> lastSneakToggleTime;

    public Sneak(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Sneak");

        vlManger = new ViolationManager();
        lastSneakToggleTime = new HashMap<>();

        sneakPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.ENTITY_ACTION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleSneakChangePacket(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(sneakPacketEvent);

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManger.resetAllOldViolation(invalidateThreshold);
            }
        }, invalidateFreq, invalidateFreq);

        loadConfig();
    }

    private void handleSneakChangePacket(PacketEvent event)
    {
        if(!isEnabled())
            return;
        if(event.getPacketType() != PacketType.Play.Client.ENTITY_ACTION)
            return;
        if(event.getPlayer().hasPermission("ntac.bypass.sneak"))
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event.getPacket());

        if(packet.getAction() == EnumWrappers.PlayerAction.START_SNEAKING ||
                packet.getAction() == EnumWrappers.PlayerAction.STOP_SNEAKING)
        {
            if(!lastSneakToggleTime.containsKey(pUUID))
            {
                lastSneakToggleTime.put(pUUID, System.currentTimeMillis());
                return;
            }

            if(lastSneakToggleTime.get(pUUID) + sneakThreshold > System.currentTimeMillis())
            {
                vlManger.addViolation(p, 1);

                int vl = vlManger.getViolation(p);
                if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
                {
                    Location resetLoc = vlManger.getFirstViolationLocation(p);
                    if(resetLoc != null)
                        p.teleport(resetLoc);
                }
                PunishUtils.runViolationAction(p, vl, vl, actionData);
            }

            lastSneakToggleTime.put(pUUID, System.currentTimeMillis());
        }
    }

    @Override
    public void loadConfig()
    {
        sneakThreshold = Integer.parseInt(pl.getConfiguration().getString("Sneak.Sneak-Threshold"));
        invalidateThreshold = Integer.parseInt(pl.getConfiguration().getString("Sneak.Invalidate-Threshold"));
        invalidateFreq = Integer.parseInt(pl.getConfiguration().getString("Sneak.Invalidate-Freq"));

        actionData = new ActionData(pl.getConfiguration(), "Sneak.Actions");
    }
}
