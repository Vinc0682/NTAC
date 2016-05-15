package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.NTAC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Vinc0682 on 15.05.2016.
 */
public class Aimbot extends AbstractCombatCheck
{
    public Aimbot(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Aimbot");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {

    }

    @Override
    public void loadConfig()
    {

    }
}
