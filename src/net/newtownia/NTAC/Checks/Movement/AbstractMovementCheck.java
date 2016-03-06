package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import org.bukkit.event.Listener;

public abstract class AbstractMovementCheck extends AbstractCheck implements Listener
{
    protected MovementBase movementBase;

    public AbstractMovementCheck(NTAC pl, MovementBase movementBase, String name)
    {
        super(pl, name);

        this.movementBase = movementBase;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (movementBase != null);
    }
}
