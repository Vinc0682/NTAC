package net.newtownia.NTAC.Checks.Movement.NCPDragDown;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.LogUtils;
import net.newtownia.NTAC.Utils.MaterialUtils;
import net.newtownia.NTAC.Utils.NCPUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class NCPDragDownHook implements NCPHook
{
    private NTAC pl;
    private NCPDragDown ncpDragDown;

    private ViolationManager vlManager;
    private boolean hooked = false;

    public NCPDragDownHook(NTAC pl, NCPDragDown ncpDragDown)
    {
        this.pl = pl;
        this.ncpDragDown = ncpDragDown;

        vlManager = new ViolationManager();
        loadConfig();
    }

    @Override
    public boolean onCheckFailure(CheckType checkType, Player p, IViolationInfo vlInfo)
    {
        if (!ncpDragDown.isEnabled())
            return false;
        if (checkType != CheckType.MOVING_SURVIVALFLY)
            return false;
        if (p.hasPermission("ntac.bypass.ncp-drag-down"))
            return false;

        if (vlInfo.willCancel() && !ncpDragDown.getMovementBase().isPlayerOnGround(p))
        {
            vlManager.setViolation(p, 1);
            Location ploc = vlManager.getFirstViolationLocation(p).clone();
            Block bDown = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            ploc.setX(p.getLocation().getX());
            if (isUnsolid(bDown))
                ploc.setY(bDown.getLocation().getBlockY());

            ploc.setZ(p.getLocation().getZ());
            ploc.setPitch(p.getLocation().getPitch());
            ploc.setYaw(p.getLocation().getYaw());

            p.teleport(ploc, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            return true;
        }
        else
            vlManager.resetPlayerViolation(p);
        return false;
    }

    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        if (vlManager.getViolation(p) > 0 && ncpDragDown.getMovementBase().isPlayerOnGround(p))
            vlManager.resetPlayerViolation(p);
    }

    private boolean isUnsolid(Block b)
    {
        return MaterialUtils.isUnsolid(b.getType()) || b.getType() == Material.WEB;
    }

    public void loadConfig()
    {
    }

    public void hook()
    {
        if (!hooked && NCPUtils.hasNoCheatPlus())
        {
            NCPHookManager.addHook(CheckType.MOVING_SURVIVALFLY, this);
            hooked = true;
        }
        if (!NCPUtils.hasNoCheatPlus())
        {
            LogUtils.info("NoCheatPlus is required for NCP-Drag-Down.");
        }
    }

    @Override
    public String getHookName() {
        return "NTAC-Fly-DragDown";
    }

    @Override
    public String getHookVersion() {
        return "1.2";
    }
}
