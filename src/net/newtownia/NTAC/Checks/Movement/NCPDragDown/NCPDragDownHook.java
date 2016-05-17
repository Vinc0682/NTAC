package net.newtownia.NTAC.Checks.Movement.NCPDragDown;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.access.IViolationInfo;
import fr.neatmonster.nocheatplus.hooks.NCPHook;
import fr.neatmonster.nocheatplus.hooks.NCPHookManager;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.MaterialUtils;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class NCPDragDownHook implements NCPHook
{
    private NTAC pl;
    private NCPDragDown ncpDragDown;
    private double downSpeed = 0.5;

    private ViolationManager vlManager;
    private Map<UUID, Long> playerResetTimes;
    private boolean hooked = false;

    public NCPDragDownHook(NTAC pl, NCPDragDown ncpDragDown)
    {
        this.pl = pl;
        this.ncpDragDown = ncpDragDown;

        vlManager = new ViolationManager();
        playerResetTimes = new HashMap<>();
        loadConfig();
    }

    @Override
    public boolean onCheckFailure(CheckType checkType, Player p, IViolationInfo vlInfo)
    {
        if (!ncpDragDown.isEnabled())
            return true;
        if (checkType != CheckType.MOVING_SURVIVALFLY)
            return true;
        if (p.hasPermission("ntac.bypass.ncp-drag-down"))
            return true;

        if (vlInfo.willCancel() && !PlayerUtils.isPlayerOnGround(p))
        {
            vlManager.setViolation(p, 1);
            Location loc = vlManager.getFirstViolationLocation(p).clone();
            loc.setY(p.getLocation().getY() - downSpeed);
            loc.setYaw(p.getLocation().getYaw());
            loc.setPitch(p.getLocation().getPitch());
            Block b = loc.getWorld().getBlockAt(loc);

            if (loc.getY() - loc.getBlockY() < downSpeed)
            {
                Block bDown = b.getRelative(BlockFace.DOWN);
                if (!isUnsolid(bDown))
                    loc.setY(loc.getBlockY());
            }

            if (b == null || isUnsolid(b))
                p.teleport(loc, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            return false;
        }
        else
            vlManager.resetPlayerViolation(p);
        return true;
    }

    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();
        UUID pUUID = p.getUniqueId();
        boolean allowReset = true;
        if (playerResetTimes.containsKey(pUUID) &&
                System.currentTimeMillis() - playerResetTimes.get(pUUID) < 1000)
            allowReset = false;
        if (PlayerUtils.isPlayerOnGround(p) && allowReset)
        {
            vlManager.resetPlayerViolation(p);
            playerResetTimes.put(pUUID, System.currentTimeMillis());
        }
    }

    private boolean isUnsolid(Block b)
    {
        return MaterialUtils.isUnsolid(b.getType()) || b.getType() == Material.WEB;
    }

    public void loadConfig()
    {
        downSpeed = Double.parseDouble(pl.getConfiguration().getString("NCP-Drag-Down.Down-Speed"));
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
            Bukkit.getLogger().info("NoCheatPlus is required for NCP-Drag-Down.");
        }
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
