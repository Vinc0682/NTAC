package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class AutoClicker extends AbstractCheck implements Listener
{
    private Map<UUID, Long> playerLastAttackTimes;
    private Map<UUID, List<Integer>> playerDelays;
    private ViolationManager vlManager;
    private ActionData actionData;

    PacketAdapter attackPacketEvent;

    int delayCount = 5;
    int timePuffer = 5;
    int combatTime = 5000;
    int invalidateThreshold = 5000;

    public AutoClicker(NTAC pl)
    {
        super(pl, "Auto-Clicker");
        loadConfig();

        playerLastAttackTimes = new HashMap<>();
        playerDelays = new HashMap<>();
        vlManager = new ViolationManager();

        attackPacketEvent = new PacketAdapter(pl, ListenerPriority.LOW, PacketType.Play.Client.USE_ENTITY)
        {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleAttackPacketEvent(event);

            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(attackPacketEvent);

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 20, 20);
    }

    private void handleAttackPacketEvent(PacketEvent event)
    {
        if (!isEnabled())
            return;
        if(event.getPacketType() != PacketType.Play.Client.USE_ENTITY)
            return;

        WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());

        if(packet.getType() != EnumWrappers.EntityUseAction.ATTACK)
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if (p.hasPermission("ntac.bypass.autoclicker"))
            return;

        if (!playerLastAttackTimes.containsKey(pUUID) ||
                !playerDelays.containsKey(pUUID))
        {
            playerLastAttackTimes.put(pUUID, System.currentTimeMillis());
            playerDelays.put(pUUID, new ArrayList<Integer>());
            return;
        }

        int currentDelay = (int)(System.currentTimeMillis() - playerLastAttackTimes.get(pUUID));
        List<Integer> delays = playerDelays.get(pUUID);

        playerLastAttackTimes.put(pUUID, System.currentTimeMillis());

        if (delays.size() == 0 || currentDelay < 5000)
            delays.add(0, currentDelay);
        else
            return;

        //Limit count of delays saved
        while (delays.size() > delayCount)
            delays.remove(delayCount);

        //Checking for autoclicker
        if (delays.size() == delayCount)
        {
            double average = getAverage(delays);
            boolean delaysSame = true;
            for (int delay : delays)
            {
                if (delay < average - timePuffer || delay > average + timePuffer)
                {
                    delaysSame = false;
                    break;
                }
            }

            if (delaysSame)
            {
                vlManager.addViolation(p, 1);
                int vl = vlManager.getViolation(p);
                PunishUtils.runViolationAction(p, vl, vl, actionData);
            }
        }
        playerDelays.put(pUUID, delays);
    }

    private double getAverage(List<Integer> numbers)
    {
        double result = 0;
        for (int number : numbers)
            result += number;
        return result / numbers.size();
    }

    @Override
    protected void onPlayerDisconnect(Player p) {
        UUID pUUID = p.getUniqueId();
        vlManager.resetPlayerViolation(pUUID);
        playerLastAttackTimes.remove(pUUID);
        playerDelays.remove(pUUID);
    }

    @Override
    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();
        timePuffer = Integer.parseInt(config.getString("Auto-Clicker.Timer-Puffer"));
        delayCount = Integer.parseInt(config.getString("Auto-Clicker.Delay-Count"));
        combatTime = Integer.parseInt(config.getString("Auto-Clicker.Delay-Count"));
        invalidateThreshold = Integer.parseInt(config.getString("Auto-Clicker.Invalidate-Threshold"));
        actionData = new ActionData(config, "Auto-Clicker.Actions");
    }
}
