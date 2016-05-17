package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HorizonCode on 17.05.2016.
 */
public class Speed extends AbstractMovementCheck {
    public Speed(NTAC pl, MovementBase movementBase) {
        super(pl, movementBase, "Speed");
    }

    public Map<Player, Integer> setBacks = new HashMap<Player, Integer>();
    public List<Player> cancel = new ArrayList<Player>();

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isEnabled())
            return;
        Player p = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        double dX = from.getX() - to.getX();
        double dZ = from.getZ() - to.getZ();
        if (p.hasPermission("ntac.bypass.speed") || p.isFlying() || p.isInsideVehicle())
            return;

        if (PlayerUtils.isUnderBlock(p))
            return;

        double speed = 0.633;

        if (dX > speed || dZ > speed || dX < -speed || dZ < -speed)
        {
            boolean check = p.getLocation().add(0, 2, 0).getBlock().getType() != Material.AIR || p.getLocation().add(0, 1.8, 0).getBlock().getType() != Material.AIR || p.getLocation().add(1, 2, 0).getBlock().getType() != Material.AIR || p.getLocation().add(1, 1.8, 0).getBlock().getType() != Material.AIR || p.getLocation().add(1, 2, 1).getBlock().getType() != Material.AIR || p.getLocation().add(1, 1.8, 1).getBlock().getType() != Material.AIR || p.getLocation().add(0, 2, 1).getBlock().getType() != Material.AIR || p.getLocation().add(0, 1.8, 1).getBlock().getType() != Material.AIR || p.getLocation().add(-1, 2, 0).getBlock().getType() != Material.AIR || p.getLocation().add(-1, 1.8, 0).getBlock().getType() != Material.AIR || p.getLocation().add(-1, 2, -1).getBlock().getType() != Material.AIR || p.getLocation().add(-1, 1.8, -1).getBlock().getType() != Material.AIR || p.getLocation().add(0, 2, -1).getBlock().getType() != Material.AIR || p.getLocation().add(0, 1.8, -1).getBlock().getType() != Material.AIR || p.getLocation().add(1, 2, -1).getBlock().getType() != Material.AIR || p.getLocation().add(1, 1.8, -1).getBlock().getType() != Material.AIR || p.getLocation().add(-1, 2, 1).getBlock().getType() != Material.AIR || p.getLocation().add(-1, 1.8, 1).getBlock().getType() != Material.AIR;
            if (check)
                return;
            if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.PACKED_ICE || p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.ICE) {
                if (p.hasPotionEffect(PotionEffectType.SLOW))
                    return;
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 51));
                p.damage(19);
            }
            p.teleport(from);
            checkAndSetBack(p);
        }
    }

    public void checkAndSetBack(Player p) {
        setBacks.remove(p);

    }


    @Override
    public void loadConfig() {

    }
}
