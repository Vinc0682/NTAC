package net.newtownia.NTAC.Checks.Movement.AntiAFK;

import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.NTAC;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PushDeviceCheck extends AbstractAntiAFKCheck
{
    boolean markInWater = true;
    boolean markInLava = true;
    boolean markInVehicle = true;

    int unmarkDelay = 3000;

    Map<UUID, Long> lastPlayerMarkedTime;

    public PushDeviceCheck(NTAC pl, MovementBase movementBase) {
        super(pl, movementBase, "PushDevice");

        lastPlayerMarkedTime = new HashMap<>();
    }

    @Override
    public boolean isValidMovement(PlayerMoveEvent e)
    {
        Player p = e.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(!lastPlayerMarkedTime.containsKey(pUUID))
            lastPlayerMarkedTime.put(pUUID, System.currentTimeMillis());

        boolean inWater = markInWater;
        boolean inLava = markInLava;

        Material posBlockMaterial = p.getLocation().getBlock().getType();
        inWater &= posBlockMaterial == Material.WATER || posBlockMaterial == Material.STATIONARY_WATER;
        inLava &= posBlockMaterial == Material.LAVA || posBlockMaterial == Material.STATIONARY_LAVA;

        boolean isInVehicle = markInVehicle;
        markInVehicle &= p.isInsideVehicle();

        boolean marked = inWater || inLava || isInVehicle;
        boolean hasMarkDelayPassed = System.currentTimeMillis() >= lastPlayerMarkedTime.get(pUUID) + unmarkDelay;

        if(marked)
            lastPlayerMarkedTime.put(pUUID, System.currentTimeMillis());

        return (!marked && hasMarkDelayPassed);
    }

    @Override
    public void loadConfig()
    {
        YamlConfiguration config = pl.getConfiguration();

        markInWater = Boolean.parseBoolean(config.getString("Anti-AFK.Push-Device-Check.Mark-In-Water"));
        markInLava = Boolean.parseBoolean(config.getString("Anti-AFK.Push-Device-Check.Mark-In-Lava"));
        markInVehicle = Boolean.parseBoolean(config.getString("Anti-AFK.Push-Device-Check.Mark-In-Vehicle"));

        unmarkDelay = Integer.parseInt(config.getString("Anti-AFK.Push-Device-Check.Unmark-Delay"));
    }
}
