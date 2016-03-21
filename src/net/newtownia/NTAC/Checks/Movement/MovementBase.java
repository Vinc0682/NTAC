package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementBase implements Listener
{
    private Map<UUID, Long> playerStartMoveTimes;
    private Map<UUID, Location> playerStartMoveLocations;
    private Map<UUID, Boolean> playerOnGround;

    int newMoveTimeThreshold = 500;

    private ArrayList<AbstractMovementCheck> movementChecks;

    public MovementBase()
    {
        playerStartMoveLocations = new HashMap<>();
        playerStartMoveTimes = new HashMap<>();
        playerOnGround = new HashMap<>();

        movementChecks = new ArrayList<>();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        updateCache(event);
        raiseChecks(event);
    }

    public void updateCache(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(!playerStartMoveTimes.containsKey(pUUID))
        {
            playerStartMoveTimes.put(pUUID, System.currentTimeMillis());
            playerStartMoveLocations.put(pUUID, event.getFrom());
        }
        else
        {
            if(hasPlayerMoveTimePassed(pUUID, newMoveTimeThreshold))
            {
                playerStartMoveTimes.put(pUUID, System.currentTimeMillis());
                playerStartMoveLocations.put(pUUID, event.getFrom());
            }
        }

        playerOnGround.put(pUUID, PlayerUtils.isPlayerOnGround(p));
    }

    public void raiseChecks(PlayerMoveEvent event)
    {
        for (AbstractMovementCheck check : movementChecks)
            check.onPlayerMove(event);
    }

    //region Getters for the caching

    public boolean hasPlayerMoveTimePassed(Player p, int milliseconds)
    {
        return System.currentTimeMillis() >= playerStartMoveTimes.get(p.getUniqueId()) + milliseconds;
    }

    public boolean hasPlayerMoveTimePassed(UUID pUUID, int milliseconds)
    {
        return System.currentTimeMillis() >= playerStartMoveTimes.get(pUUID) + milliseconds;
    }

    public Location getPlayerMoveStartLocation(Player p)
    {
        if(!playerStartMoveLocations.containsKey(p.getUniqueId()))
            return null;

        return playerStartMoveLocations.get(p.getUniqueId());
    }

    public boolean isPlayerOnGround(Player p)
    {
        UUID pUUID = p.getUniqueId();
        if (!playerOnGround.containsKey(pUUID))
        {
            playerOnGround.put(pUUID, PlayerUtils.isPlayerOnGround(p));
        }
        return playerOnGround.get(pUUID);
    }
    //endregion

    //region Observator functions
    public void registerMovementCheck(AbstractMovementCheck movementCheck)
    {
        movementChecks.add(movementCheck);
    }

    public void unregisterMovementCheck(AbstractMovementCheck movementCheck)
    {
        movementChecks.remove(movementCheck);
    }
    //endregion
}
