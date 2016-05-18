package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

/**
 * Created by HorizonCode on 17.05.2016.
 */
public class Criticals extends AbstractCombatCheck
{
    private ActionData actionData;

    private HashMap<Player, Integer> time;
    private ViolationManager vlManager;

    public Criticals(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Criticals");
        time = new HashMap<>();
        vlManager = new ViolationManager();

        loadConfig();
    }

    @Override
    public void onUpdate(Player p)
    {
        if (!isEnabled())
            return;
        if (time.containsKey(p))
        {
            if (time.get(p) <= 1)
                time.remove(p);
            else
                time.put(p, time.get(p) - 1);
        }
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

        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid() ||
            p.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid())
            return;

        if (!p.isOnGround() && !p.isFlying())
        {
            if (p.getLocation().getY() % 1.0D == 0.0D &&
                    p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
            {
                vlManager.addViolation(p, 1);
                int vl = vlManager.getViolation(p);
                if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
                    event.setCancelled(true);
                PunishUtils.runViolationAction(p, vl, vl, actionData);
            }
        }
    }

    @Override
    public void loadConfig()
    {
        actionData = new ActionData(pl.getConfiguration(), "Criticals.Actions");
    }
}
