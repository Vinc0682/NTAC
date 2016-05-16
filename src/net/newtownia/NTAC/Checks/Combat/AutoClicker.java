package net.newtownia.NTAC.Checks.Combat;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.MathUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class AutoClicker extends AbstractCombatCheck
{
    private Map<UUID, Long> playerLastAttackTimes;
    private Map<UUID, List<Integer>> playerDelays;
    private ViolationManager vlManager;
    private ActionData actionData;

    private int delayCount = 5;
    private int timePuffer = 5;
    private int combatTime = 5000;
    private int invalidateThreshold = 5000;

    public AutoClicker(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Auto-Clicker");
        loadConfig();

        playerLastAttackTimes = new HashMap<>();
        playerDelays = new HashMap<>();
        vlManager = new ViolationManager();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 20, 20);
    }

    @Override
    protected void onAttackPacketReceive(PacketEvent event, WrapperPlayClientUseEntity packet)
    {
        if (!isEnabled())
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

        if (delays.size() == 0 || currentDelay < combatTime)
            delays.add(0, currentDelay);
        else
            return;

        //Limit count of delays saved
        while (delays.size() > delayCount)
            delays.remove(delayCount);

        //Checking for autoclicker
        if (delays.size() == delayCount)
        {
            double average = MathUtils.getAverageInteger(delays);
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
