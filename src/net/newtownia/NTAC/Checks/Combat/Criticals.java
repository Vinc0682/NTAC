package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PlayerUtils;
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
public class Criticals extends AbstractCombatCheck {
    private static HashMap<Player, Integer> time = new HashMap<>();

    public Criticals(NTAC pl, CombatBase combatBase)
    {
        super(pl, combatBase, "Criticals");
    }

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
        if (!PlayerUtils.isPlayerOnGround(p) && !p.isFlying())
        {
            if (p.getLocation().getY() % 1.0D == 0.0D) {
                if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
                {
                    event.setCancelled(true);


                }
            }
        }
    }

    @Override
    public void loadConfig() {

    }
}
