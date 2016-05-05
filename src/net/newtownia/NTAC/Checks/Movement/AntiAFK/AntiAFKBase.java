package net.newtownia.NTAC.Checks.Movement.AntiAFK;

import net.newtownia.NTAC.Checks.Movement.AbstractMovementCheck;
import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class AntiAFKBase extends AbstractMovementCheck implements Listener
{
    private Map<UUID, Long> lastPlayerNonAFKMoveTimes;

    int kickCheckFreq = 100;
    int kickTimeThreshold = 300000;

    String kickMessage = "You are afk";

    List<AbstractAntiAFKCheck> checks;

    public AntiAFKBase(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Anti-AFK");

        lastPlayerNonAFKMoveTimes = new HashMap<>();

        loadConfig();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run()
            {
                if(!isEnabled())
                    return;

                for(Player p : Bukkit.getOnlinePlayers())
                {
                    if(!lastPlayerNonAFKMoveTimes.containsKey(p.getUniqueId()))
                        continue;

                    if(p.hasPermission("ntac.bypass.antiafk"))
                        return;

                    long lastRealMove = lastPlayerNonAFKMoveTimes.get(p.getUniqueId());

                    if(System.currentTimeMillis() >= lastRealMove + kickTimeThreshold)
                        PunishUtils.kickPlayer(p, kickMessage);
                }
            }
        }, kickCheckFreq, kickCheckFreq);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        if (!isEnabled())
            return;

        lastPlayerNonAFKMoveTimes.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();

        kickTimeThreshold = Integer.parseInt(config.getString("Anti-AFK.Kick-Threshold"));
        kickTimeThreshold *= 1000;

        kickCheckFreq = Integer.parseInt(config.getString("Anti-AFK.Kick-Check-Frequency"));

        kickMessage = config.getString("Anti-AFK.Kick-Message");
        kickMessage = pl.getMessageUtils().formatMessage(kickMessage);

        checks = new ArrayList<>();
        if(Boolean.valueOf(config.getString("Anti-AFK.Move-In-Range.Enabled")))
            checks.add(new MoveInRangeCheck(pl, movementBase));

        if(Boolean.valueOf(config.getString("Anti-AFK.Push-Device-Check.Enabled")))
            checks.add(new PushDeviceCheck(pl, movementBase));
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if(!isEnabled())
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(p.hasPermission("ntac.bypass.antiafk"))
        {
            lastPlayerNonAFKMoveTimes.put(pUUID, System.currentTimeMillis());
            return;
        }

        if(!lastPlayerNonAFKMoveTimes.containsKey(pUUID))
            lastPlayerNonAFKMoveTimes.put(pUUID, System.currentTimeMillis());

        boolean isValidMove = true;

        for(AbstractAntiAFKCheck check : checks)
            isValidMove &= check.isValidMovement(event);

        if(isValidMove)
            lastPlayerNonAFKMoveTimes.put(pUUID, System.currentTimeMillis());
    }
}
