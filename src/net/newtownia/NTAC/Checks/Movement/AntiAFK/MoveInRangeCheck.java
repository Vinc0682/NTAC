package net.newtownia.NTAC.Checks.Movement.AntiAFK;

import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.NTAC;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveInRangeCheck extends AbstractAntiAFKCheck
{
    public MoveInRangeCheck(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "MoveInRange");
    }

    double threshold = 2;
    double squaredThreshold = threshold * threshold;

    @Override
    public boolean isValidMovement(PlayerMoveEvent e)
    {
        Location startLoc = movementBase.getPlayerMoveStartLocation(e.getPlayer());

        if(startLoc == null)
            return true;

        Location currentLoc = e.getTo();

        double diffX = startLoc.getX() - currentLoc.getX();
        //double diffY = startLoc.getY() - currentLoc.getY();
        double diffZ = startLoc.getZ() - currentLoc.getZ();

        return diffX * diffX + diffZ * diffZ > squaredThreshold;
    }

    @Override
    public void loadConfig()
    {
        threshold = Double.valueOf(pl.getConfiguration().getString("Anti-AFK.Move-In-Range.Range-Threshold"));
        squaredThreshold = threshold * threshold;
    }
}
