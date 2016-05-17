package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class AbstractMovementCheck extends AbstractCheck implements Listener
{
    protected MovementBase movementBase;

    public AbstractMovementCheck(NTAC pl, MovementBase movementBase, String name)
    {
        super(pl, name);

        this.movementBase = movementBase;
        movementBase.registerMovementCheck(this);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (movementBase != null);
    }

    public abstract void onPlayerMove(PlayerMoveEvent event);

    @Override
    public void onUpdate(Player p) {}
}
