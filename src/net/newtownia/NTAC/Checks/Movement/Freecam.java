package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.NTAC;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class Freecam extends AbstractMovementCheck
{
    public Freecam(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Freecam");
    }

    @Override
    public void onUpdate(Player p)
    {
        long lastMoveTime = movementBase.getLastMoveTime(p.getUniqueId());
        if (lastMoveTime > 0 && System.currentTimeMillis() > lastMoveTime + 1000)
        {
            p.teleport(p.getLocation().add(0, 0.001, 0));
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {

    }
    @Override
    public void loadConfig() {

    }
}
