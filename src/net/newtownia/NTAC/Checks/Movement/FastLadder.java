package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class FastLadder extends AbstractMovementCheck implements Listener
{
    private double climbingSpeed = 0.13;
    private Double startSpeed = 1.5;

    private ViolationManager vlManager;
    private Map<UUID, Double> playerOnLadderY;
    private ActionData actionData;
    int invalidateThreshold = 5000;

    public FastLadder(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Fast-Ladder");

        vlManager = new ViolationManager();
        playerOnLadderY = new HashMap<>();
        loadConfig();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 20, 20);
    }

    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!isEnabled())
            return;

        Player p = event.getPlayer();

        if (p.hasPermission("ntac.bypass.fastladder") || p.isFlying())
            return;

        UUID pUUID = p.getUniqueId();

        Material m = p.getLocation().getBlock().getType();
        if (m == Material.LADDER || m == Material.VINE)
        {
            if (!playerOnLadderY.containsKey(pUUID) || playerOnLadderY.get(pUUID) == null)
            {
                playerOnLadderY.put(pUUID, event.getFrom().getY());
                return;
            }

            double diffY = event.getTo().getY() - event.getFrom().getY();

            if (diffY < 0)
                return;

            if ((event.getTo().getY() - playerOnLadderY.get(pUUID)) > startSpeed)
            {
                if (diffY > climbingSpeed)
                {
                    vlManager.addViolation(p, 1);

                    int vl = vlManager.getViolation(p);
                    if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
                    {
                        Location resetLoc = vlManager.getFirstViolationLocation(p);
                        if(resetLoc != null)
                            p.teleport(resetLoc);
                    }
                    PunishUtils.runViolationAction(p, vl, vl, actionData);
                }
            }
        }
        else if (playerOnLadderY.containsKey(pUUID))
        {
            playerOnLadderY.remove(pUUID);
        }
    }

    @Override
    protected void onPlayerDisconnect(Player p) {
        vlManager.resetPlayerViolation(p);
        playerOnLadderY.remove(p.getUniqueId());
    }

    @Override
    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();
        climbingSpeed = Double.parseDouble(config.getString("Fast-Ladder.Climbing-Speed"));
        startSpeed = Double.parseDouble(config.getString("Fast-Ladder.Start-Speed"));
        invalidateThreshold = Integer.parseInt(config.getString("Auto-Clicker.Invalidate-Threshold"));

        actionData = new ActionData(config, "Fast-Ladder.Actions");
    }
}
