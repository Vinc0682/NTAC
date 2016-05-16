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
import java.util.UUID;

public class Jesus extends AbstractMovementCheck
{
    private ViolationManager vlManager;
    private ActionData actionData;

    private boolean jumpJesus = true;

    private HashMap<UUID, Integer> playerOnWaterCount = new HashMap<>();
    private HashMap<UUID, Integer> playerJumpCount = new HashMap<>();

    public Jesus(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Jesus");
        loadConfig();
        vlManager = new ViolationManager();
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if(!isEnabled())
            return;

        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(p.hasPermission("ntac.bypass.jesus"))
            return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (p.isInsideVehicle() || p.isFlying())
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

        Material inType = p.getLocation().add(0, 0.5, 0).getBlock().getType();
        if (inType == Material.WATER || inType == Material.STATIONARY_WATER)
        {
            playerJumpCount.remove(pUUID);
            playerOnWaterCount.remove(pUUID);
            vlManager.resetPlayerViolation(p);
            return;
        }

        if (PlayerUtils.isInBlock(p, Material.STATIONARY_WATER) || PlayerUtils.isInBlock(p, Material.WATER)) {
            return;
        }

        if (PlayerUtils.isOnWater(p) || PlayerUtils.isInWater(p))
        {
            if(jumpJesus)
            {
                if (from.getY() < to.getY())
                {
                    if (playerJumpCount.containsKey(pUUID))
                    {
                        if (playerJumpCount.get(pUUID) > 3)
                        {
                            handleViolation(p);
                            playerJumpCount.remove(pUUID);
                            return;
                        }
                        int old = playerJumpCount.get(pUUID);
                        playerJumpCount.put(pUUID, old + 1);
                    } else
                    {
                        playerJumpCount.put(pUUID, 1);
                    }
                }
            }
            if (playerOnWaterCount.containsKey(pUUID))
            {
                int old = playerOnWaterCount.get(pUUID);
                playerOnWaterCount.put(pUUID, old + 1);
                if (playerOnWaterCount.get(pUUID) > 8)
                {
                    //flagging
                    handleViolation(p);
                    playerOnWaterCount.remove(pUUID);
                }
            } else {
                playerOnWaterCount.put(pUUID, 1);
            }
        }
        else
        {
            vlManager.resetPlayerViolation(p);
        }
    }

    private void handleViolation(Player p)
    {
        vlManager.addViolation(p, 1);

        int vl = vlManager.getViolation(p);
        if(actionData.doesLastViolationCommandsContains(vl, "cancel"))
        {
            Location resetLoc = vlManager.getFirstViolationLocation(p);
            if(resetLoc != null)
                p.teleport(resetLoc);
        }
        PunishUtils.runViolationAction(p, vl, vl, actionData);
    }

    @Override
    public void loadConfig()
    {
        actionData = new ActionData(pl.getConfiguration(), "Jesus.Actions");
        jumpJesus = Boolean.valueOf(pl.getConfiguration().getString("Jesus.Jump-Jesus"));
    }
}
