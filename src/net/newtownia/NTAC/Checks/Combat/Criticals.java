package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Criticals extends AbstractCombatCheck
{
    private ActionData actionData;
    private int invalidateThreshold = 60000;

    private ViolationManager vlManager;

    public Criticals(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Criticals");
        loadConfig();

        vlManager = new ViolationManager();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 20L, 20L);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        if (!isEnabled())
            return;
        if (!(event.getDamager() instanceof Player && event.getDamager().getType() == EntityType.PLAYER))
            return;
        Player p = (Player) event.getDamager();

        if (p.hasPermission("ntac.bypass.criticals"))
            return;
        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid())
            return;
        if (p.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid())
            return;
        if (p.isOnGround() || p.isFlying())
            return;
        if (p.getLocation().getY() % 1.0 != 0)
            return;
        if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
            return;

        vlManager.addViolation(p, 1);
        PunishUtils.runViolationAction(p, vlManager, actionData);
    }

    @Override
    public void loadConfig()
    {
        invalidateThreshold = Integer.parseInt(pl.getConfiguration().getString("Criticals.Invalidate-Threshold"));
        actionData = new ActionData(pl.getConfiguration(), "Criticals.Actions");
    }
}
