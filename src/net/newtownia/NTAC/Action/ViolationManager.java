package net.newtownia.NTAC.Action;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class ViolationManager
{
    private Map<UUID, Double> playerViolations;
    private Map<UUID, Location> playerFirstViolationLocations;
    private Map<UUID, Long> playerLastViolationTimes;

    public ViolationManager() {
        playerViolations = new HashMap<>();
        playerFirstViolationLocations = new HashMap<>();
        playerLastViolationTimes = new HashMap<>();
    }

    public double getViolation(UUID pUUID)
    {
        if (!hasViolation(pUUID))
            return 0;
        return playerViolations.get(pUUID);
    }

    public double getViolation(Player p)
    {
        return getViolation(p.getUniqueId());
    }

    public int getViolationInt(Player p)
    {
        return (int)getViolation(p);
    }

    public boolean hasViolation(UUID pUUID)
    {
        return playerViolations.containsKey(pUUID);
    }

    public void setViolation(Player p, double newViolation)
    {
        UUID pUUID = p.getUniqueId();
        if(newViolation <= 0 && playerViolations.containsKey(pUUID))
        {
            resetPlayerViolation(pUUID);
            return;
        }
        else if(!playerFirstViolationLocations.containsKey(pUUID))
            playerFirstViolationLocations.put(pUUID, p.getLocation());

        setViolationWithoutSetbackPos(p, newViolation);
    }

    public void setViolationWithoutSetbackPos(Player p, double newViolation)
    {
        setViolationWithoutSetbackPos(p.getUniqueId(), newViolation);
    }

    public void setViolationWithoutSetbackPos(UUID pUUID, double newViolation)
    {
        playerViolations.put(pUUID, newViolation);
        playerLastViolationTimes.put(pUUID, System.currentTimeMillis());
    }

    public void addViolation(Player p, double amount) {
        setViolation(p, getViolation(p) + amount);
    }

    public void subtractViolation(Player p, double amount)
    {
        setViolation(p, getViolation(p) - amount);
    }

    public void resetPlayerViolation(Player p)
    {
        resetPlayerViolation(p.getUniqueId());
    }

    public void resetPlayerViolation(UUID pUUID)
    {
        playerViolations.remove(pUUID);
        playerFirstViolationLocations.remove(pUUID);
        playerLastViolationTimes.remove(pUUID);
    }

    public void addViolationToAll(double amount)
    {
        for(UUID pUUID : playerViolations.keySet())
        {
            Player p = Bukkit.getPlayer(pUUID);
            if(p != null)
                addViolation(p, amount);
        }
    }

    public void subtractViolationToAll(double amount)
    {
        addViolationToAll(-amount);
    }

    public void resetAllViolations()
    {
        List<UUID> toReset = new ArrayList<>();
        for(UUID pUUID : playerViolations.keySet())
            toReset.add(pUUID);
        for(UUID pUUID : toReset)
            resetPlayerViolation(pUUID);
    }

    public Location getFirstViolationLocation(Player p)
    {
        UUID pUUID = p.getUniqueId();

        if(!playerFirstViolationLocations.containsKey(pUUID))
            return null;

        return playerFirstViolationLocations.get(pUUID);
    }

    public void resetAllOldViolation(int threshold)
    {
        List<UUID> toReset = new ArrayList<>();
        for(UUID pUUID : playerLastViolationTimes.keySet())
        {
            long lastViolationTime = playerLastViolationTimes.get(pUUID);
            if(System.currentTimeMillis() >= lastViolationTime + threshold)
                toReset.add(pUUID);
        }
        for(UUID pUUID : toReset)
            resetPlayerViolation(pUUID);
    }

    public void resetOldPlayerViolation(Player p, int threshold)
    {
        UUID pUUID = p.getUniqueId();
        if (playerLastViolationTimes.containsKey(pUUID) &&
                System.currentTimeMillis() >= playerLastViolationTimes.get(pUUID) + threshold)
        {
            resetPlayerViolation(p);
        }
    }

    public long getLastViolationTime(UUID pUUID)
    {
        if (!playerLastViolationTimes.containsKey(pUUID))
            return -1;
        return playerLastViolationTimes.get(pUUID);
    }

    public long getLastViolationTime(Player p)
    {
        return getLastViolationTime(p.getUniqueId());
    }

    public Map<UUID, Double> getAllViolations()
    {
        return playerViolations;
    }
}
