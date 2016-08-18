package net.newtownia.NTAC.Checks.Movement.AntiAFK;

import net.newtownia.NTAC.Checks.Movement.AbstractMovementCheck;
import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.NTAC;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractAntiAFKCheck extends AbstractMovementCheck
{
    public AbstractAntiAFKCheck(NTAC pl, MovementBase movementBase, String name) {
        super(pl, movementBase, name);

        loadConfig();
    }

    public abstract boolean isValidMovement(PlayerMoveEvent e);

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        //Do nothing
    }

    // Required for the Smoke obfuscator
    private void a()
    {
        JavaPlugin pl2 = pl;
    }
}
