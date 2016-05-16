package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PlayerUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

/**
 * Created by HorizonCode on 16.05.2016.
 */
public class Jesus extends AbstractMovementCheck {

    public Jesus(NTAC pl, MovementBase movementBase) {
        super(pl, movementBase, "Jesus");
        loadConfig();
        vlManager = new ViolationManager();
    }

    public ViolationManager vlManager;
    public ActionData actionData;

    public boolean jumpjesus;

    public HashMap<Player, Integer> count = new HashMap<Player, Integer>();
    public HashMap<Player, Integer> jumpcount = new HashMap<Player, Integer>();

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!isEnabled())
            return;
        Player p = event.getPlayer();
        if(p.hasPermission("ntac.bypass.jesus"))
            return;
        Location from = event.getFrom();
        Location to = event.getTo();
        if (p.isInsideVehicle() || p.getAllowFlight())
            return;

        if (p.isOp())
            return;

        Material l = from.getBlock().getType();
        Material l1 = from.getBlock().getRelative(1, 0, 0).getType();
        Material l2 = from.getBlock().getRelative(-1, 0, 0).getType();
        Material l3 = from.getBlock().getRelative(0, 0, 1).getType();
        Material l4 = from.getBlock().getRelative(0, 0, -1).getType();
        Material l5 = from.getBlock().getRelative(1, 0, 1).getType();
        Material l6 = from.getBlock().getRelative(-1, 0, -1).getType();
        Material l7 = from.getBlock().getRelative(-1, 0, 1).getType();
        Material l8 = from.getBlock().getRelative(1, 0, -1).getType();
        if ((l == Material.WATER_LILY) || (l1 == Material.WATER_LILY) || (l2 == Material.WATER_LILY) || (l3 == Material.WATER_LILY) || (l4 == Material.WATER_LILY) || (l5 == Material.WATER_LILY) || (l6 == Material.WATER_LILY) || (l7 == Material.WATER_LILY) || (l8 == Material.WATER_LILY)) {
            return;
        }

        if (p.getLocation().add(0, 0.5, 0).getBlock().getType() == Material.WATER || p.getLocation().add(0, 0.5, 0).getBlock().getType() == Material.STATIONARY_WATER) {
            jumpcount.remove(p);
            count.remove(p);
            return;
        }

        if (PlayerUtils.isInBlock(p, Material.STATIONARY_WATER) || PlayerUtils.isInBlock(p, Material.WATER)) {
            return;
        }

        if (PlayerUtils.isOnWater(p)) {
            if(jumpjesus) {
                if (from.getY() < to.getY()) {
                    if (jumpcount.containsKey(p)) {
                        if (jumpcount.get(p) > 3) {
                            vlManager.addViolation(p, 1);

                            int vl = vlManager.getViolation(p);
                            if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
                            {
                                Location resetLoc = vlManager.getFirstViolationLocation(p);
                                if(resetLoc != null)
                                    p.teleport(resetLoc);
                            }
                            PunishUtils.runViolationAction(p, vl, vl, actionData);
                            jumpcount.remove(p);
                            return;
                        }
                        int old = jumpcount.get(p);
                        jumpcount.put(p, old + 1);
                    } else {
                        jumpcount.put(p, 1);
                    }
                }
            }
            if (count.containsKey(p)) {
                int old = count.get(p);
                count.put(p, old + 1);
                if (count.get(p) > 8) {
                    //flagging
                    count.remove(p);
                }
            } else {
                count.put(p, 1);
            }
        }
    }

    @Override
    public void loadConfig() {
        actionData = new ActionData(pl.getConfiguration(), "Jesus.Actions");
        jumpjesus = Boolean.valueOf(pl.getConfiguration().getString("Jesus.Jump-Jesus"));
    }
}
