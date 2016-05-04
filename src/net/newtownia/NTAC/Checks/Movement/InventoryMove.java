package net.newtownia.NTAC.Checks.Movement;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vinc0682 on 04.05.2016.
 */
public class InventoryMove extends AbstractMovementCheck
{
    private boolean cancelInventoryAction = true;
    private int graceTime = 1000;
    private int invalidateThreshold = 1000;
    private ActionData actionData;

    private Map<UUID, Long> playerLastInvOpenTime;
    private ViolationManager vlManager;

    public InventoryMove(NTAC pl, MovementBase movementBase)
    {
        super(pl, movementBase, "Inventory-Move");

        vlManager = new ViolationManager();
        playerLastInvOpenTime = new HashMap<>();

        Bukkit.getScheduler().runTaskTimer(pl, new Runnable() {
            @Override
            public void run() {
                vlManager.resetAllOldViolation(invalidateThreshold);
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onPlayerInventoryAction(InventoryClickEvent event)
    {
        if (!isEnabled())
            return;

        if (event.getWhoClicked() instanceof Player)
        {
            Player p = (Player) event.getWhoClicked();

            boolean isHotbar = false;

            if (event.getSlotType() == InventoryType.SlotType.QUICKBAR)
                isHotbar = true;

            if (!playerLastInvOpenTime.containsKey(p.getUniqueId()) && !isHotbar)
            {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player p = event.getPlayer();

        //p.sendMessage("Ya inväntöry: " + playerLastInvOpenTime.containsKey(p.getUniqueId()));

        if (hasInventoryOpenWithGrace(p))
        {
            vlManager.addViolation(p, 1);
            int vl = vlManager.getViolation(p);
            if (actionData.doesLastViolationCommandsContains(vl, "cancel"))
            {
                Location resetLoc = vlManager.getFirstViolationLocation(p);
                if(resetLoc != null)
                    p.teleport(resetLoc.add(0, 1, 0));
            }
            PunishUtils.runViolationAction(p, vl, vl, actionData);
        }
    }

    private boolean hasInventoryOpenWithGrace(Player p)
    {
        if (!playerLastInvOpenTime.containsKey(p))
            return false;
        else
        {
            int deltaTime = (int) (System.currentTimeMillis() - playerLastInvOpenTime.get(p.getUniqueId()));
            return deltaTime > graceTime;
        }
    }

    @EventHandler
    public void onPlayerOpensInv(InventoryOpenEvent event)
    {
        if (event.getPlayer() instanceof Player)
        {
            playerLastInvOpenTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            event.getPlayer().sendMessage("Öpening");
        }
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event)
    {
        if (playerLastInvOpenTime.containsKey(event.getPlayer().getUniqueId()))
            playerLastInvOpenTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerCloseInv(InventoryCloseEvent event)
    {
        closeInv(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        closeInv(event.getPlayer());
    }

    private void closeInv(HumanEntity p)
    {
        if (p instanceof Player && playerLastInvOpenTime.containsKey(p.getUniqueId()))
        {
            playerLastInvOpenTime.remove(p.getUniqueId());
            p.sendMessage("Clösäng");
        }
    }

    @Override
    public void loadConfig()
    {
        actionData = new ActionData(pl.getConfiguration(), "Inventory-Move.Actions");
        cancelInventoryAction = Boolean.valueOf(pl.getConfiguration().getString("Inventory-Move.Cancel-Item-Actions"));
        graceTime = Integer.valueOf(pl.getConfiguration().getString("Inventory-Move.Grace-Time"));
        invalidateThreshold = Integer.valueOf(pl.getConfiguration().getString("Inventory-Move.Invalidate-Threshold"));
    }
}
