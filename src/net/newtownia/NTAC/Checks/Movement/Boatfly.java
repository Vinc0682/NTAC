package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoatFly extends AbstractMovementCheck
{
    private ActionData actionData;
    private ViolationManager vlManager;
    private boolean cancelAllUpMotions = true;
    private int minOffgroundMoves = 3;
    private double minDownSpeed = 0.12;
    private int invalidateThreshold = 1000;

    private Map<UUID, Integer> playerOffGroundMoves;

    public BoatFly(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Boat-Fly");
        vlManager = new ViolationManager();
        playerOffGroundMoves = new HashMap<>();

        loadConfig();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 20L, 20L);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!isEnabled())
            return;
        Player p = event.getPlayer();
        if (p.hasPermission("ntac.bypass.boatfly"))
            return;

        if (p.isInsideVehicle() && p.getVehicle() instanceof Boat)
        {
            double yDiff = event.getTo().getY() - event.getFrom().getY();
            Boat boat = (Boat)p.getVehicle();

            if (yDiff > -minDownSpeed && isOffWater(boat))
            {
                if (cancelAllUpMotions && yDiff > 0)
                {
                    Location resetLoc = vlManager.getFirstViolationLocation(p);
                    if(resetLoc != null)
                        p.teleport(resetLoc.add(0, 1, 0));
                }

                if (playerOffGroundMoves.containsKey(p.getUniqueId()))
                    playerOffGroundMoves.put(p.getUniqueId(), playerOffGroundMoves.get(p.getUniqueId()) + 1);
                else
                    playerOffGroundMoves.put(p.getUniqueId(), 0);

                if (playerOffGroundMoves.get(p.getUniqueId()) > minOffgroundMoves)
                {
                    vlManager.addViolation(p, 1);
                    int vl = vlManager.getViolation(p);
                    if((cancelAllUpMotions && yDiff > 0) ||
                            actionData.doesLastViolationCommandsContains(vl, "cancel"))
                    {
                        Location resetLoc = vlManager.getFirstViolationLocation(p);
                        if(resetLoc != null)
                            p.teleport(resetLoc.add(0, 1, 0));
                    }
                    PunishUtils.runViolationAction(p, vl, vl, actionData);
                }
            }
        }
        else
        {
            playerOffGroundMoves.put(p.getUniqueId(), 0);
        }
    }

    private boolean isOffWater(Boat boat)
    {
        Material m = boat.getLocation().getBlock().getType();
        return m != Material.STATIONARY_WATER && m != Material.WATER && !boat.isOnGround();
    }

    @Override
    public void loadConfig()
    {
        actionData = new ActionData(pl.getConfiguration(), "Boat-Fly.Actions");
        cancelAllUpMotions = Boolean.valueOf(pl.getConfiguration().getString("Boat-Fly.Cancel-All-Up-Movements"));
        minOffgroundMoves = Integer.valueOf(pl.getConfiguration().getString("Boat-Fly.Min-Offground-Moves"));
        minDownSpeed = Double.valueOf(pl.getConfiguration().getString("Boat-Fly.Min-Down-Speed"));
        invalidateThreshold = Integer.valueOf(pl.getConfiguration().getString("Boat-Fly.Invalidate-Threshold"));
    }
}
