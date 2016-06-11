package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PlayerUtils;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        if (p.isInsideVehicle() || p.isFlying() || PlayerUtils.isGlidingWithElytra(p))
            return;

        if (PlayerUtils.isInWeb(from) || PlayerUtils.isInWeb(to))
            return;
        if (PlayerUtils.isOnEntity(p, EntityType.BOAT))
            return;

        Block fromBlock = from.getBlock();
        List<Material> arround = new ArrayList<>();
        arround.add(fromBlock.getType());
        arround.add(fromBlock.getRelative(1, 0, 0).getType());
        arround.add(fromBlock.getRelative(-1, 0, 0).getType());
        arround.add(fromBlock.getRelative(0, 0, 1).getType());
        arround.add(fromBlock.getRelative(0, 0, -1).getType());
        arround.add(fromBlock.getRelative(1, 0, 1).getType());
        arround.add(fromBlock.getRelative(-1, 0, -1).getType());
        arround.add(fromBlock.getRelative(-1, 0, 1).getType());
        arround.add(fromBlock.getRelative(1, 0, -1).getType());
        if (arround.contains(Material.WATER_LILY))
            return;

        if (PlayerUtils.isOnSteps(p))
            return;

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

        if (PlayerUtils.isOnWater(p) && !PlayerUtils.isPlayerOnGroundNTACOld(p))
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

        int vl = vlManager.getViolationInt(p);
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
