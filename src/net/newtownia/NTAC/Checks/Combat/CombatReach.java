package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.MathUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatReach extends AbstractCombatCheck
{
    private double maxDistance = 3.6;
    private int invalidateThreshold = 60000;
    private ActionData actionData;

    private ViolationManager vlManager;

    public CombatReach(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Combat-Reach");
        vlManager = new ViolationManager();

        loadConfig();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 60L, 60L);
    }

    @Override
    protected void onAttack(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player && event.getDamager().getType() == EntityType.PLAYER)
        {
            Player p = (Player)event.getDamager();
            double distSq = MathUtils.getXZDistanceSq(p.getLocation(), event.getEntity().getLocation());
            if (distSq > maxDistance * maxDistance)
            {
                vlManager.addViolation(p, 1);
                PunishUtils.runViolationAction(p, vlManager, actionData);
            }
        }
    }

    @Override
    public void loadConfig()
    {
        maxDistance = Double.valueOf(pl.getConfiguration().getString("Combat-Reach.Max-Reach"));
        invalidateThreshold = Integer.valueOf(pl.getConfiguration().getString("Combat-Reach.Invalidate-Threshold"));
        actionData = new ActionData(pl.getConfiguration(), "Combat-Reach.Actions");
    }
}
