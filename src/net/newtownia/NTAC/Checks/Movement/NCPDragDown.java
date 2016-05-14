package net.newtownia.NTAC.Checks.Movement;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.NCPUtils;
import net.newtownia.NTAC.Utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Vinc0682 on 13.05.2016.
 */
public class NCPDragDown extends AbstractMovementCheck implements NCPHook
{
    private int invalidateThreshold = 250;
    private double downSpeed = 0.5;

    private ViolationManager vlManager;
    private boolean hooked = false;

    public NCPDragDown(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "NCP-Drag-Down");
        vlManager = new ViolationManager();

        loadConfig();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 2L, 2L);
    }

    @Override
    public boolean onCheckFailure(CheckType checkType, Player p, IViolationInfo vlInfo)
    {
        if (!isEnabled())
            return true;
        if (checkType != CheckType.MOVING_SURVIVALFLY)
            return true;
        if (p.hasPermission("ntac.bypass.ncp-drag-down"))
            return true;

        if (vlInfo.willCancel() && !PlayerUtils.isPlayerOnGround(p))
        {
            vlManager.setViolation(p, 1);
            Location loc = vlManager.getFirstViolationLocation(p);
            loc.setY(p.getLocation().getY() - downSpeed);
            Block b = loc.getWorld().getBlockAt(loc);

            if (loc.getY() - loc.getBlockY() < downSpeed)
            {
                Block bDown = b.getRelative(BlockFace.DOWN);
                if (bDown.getType() != Material.AIR)
                    loc.setY(loc.getBlockY());
            }

            if (b == null || b.getType() == Material.AIR)
                p.teleport(loc, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            return false;
        }
        return true;
    }

    @Override
    public void loadConfig()
    {
        invalidateThreshold = Integer.parseInt(pl.getConfiguration().getString("NCP-Drag-Down.Invalidate-Threshold"));
        downSpeed = Double.parseDouble(pl.getConfiguration().getString("NCP-Drag-Down.Down-Speed"));

        hook();
    }


    private void hook()
    {
        if (!hooked && NCPUtils.hasNoCheatPlus())
        {
            NCPHookManager.addHook(CheckType.MOVING_SURVIVALFLY, this);
            hooked = true;
        }
        else
        {
            Bukkit.getLogger().info("NoCheatPlus is required for NCP-Drag-Down.");
        }
    }


    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {

    }

    @Override
    public String getHookName() {
        return "NTAC-Fly-DragDown";
    }

    @Override
    public String getHookVersion() {
        return "1.0";
    }
}
