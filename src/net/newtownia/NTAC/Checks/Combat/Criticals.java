package net.newtownia.NTAC.Checks.Combat;

import net.newtownia.NTAC.NTAC;
import org.bukkit.block.BlockFace;
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

    public Criticals(NTAC pl, CombatBase combatBase) {
        super(pl, combatBase, "Criticals");
    }

    @Override
    public void onUpdate(Player p) {
        if (!isEnabled())
            return;
        if (time.containsKey(p)) {
            if (time.get(p) <= 1) {
                time.remove(p);
            } else {
                time.put(p, time.get(p) - 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(EntityDamageByEntityEvent event) {
        if (!isEnabled())
            return;
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();

        if (player.isOp()) {
            return;
        }
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()) {
            return;
        }
        if (player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid()) {
            return;
        }
        if ((!player.isOnGround()) && (!player.isFlying())) {
            if (player.getLocation().getY() % 1.0D == 0.0D) {
                if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                    event.setCancelled(true);
                    //flag
                }
            }
        }
    }

    @Override
    public void loadConfig() {

    }
}
