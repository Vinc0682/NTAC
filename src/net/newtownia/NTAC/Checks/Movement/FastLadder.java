package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class FastLadder extends AbstractMovementCheck implements Listener
{
    private double speed = 0.15;

    private ViolationManager vlManager;

    public FastLadder(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Fast-Ladder");

        vlManager = new ViolationManager();
        loadConfig();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (!isEnabled())
            return;

        Player p = event.getPlayer();
        Material m = p.getLocation().getBlock().getType();
        if (m == Material.LADDER || m == Material.VINE)
        {
            //TODO: Make a fastladder check
            String doSomething = "Work in progress...";
            doSomething += "b";
        }
    }

    @Override
    public void loadConfig()
    {

    }
}
