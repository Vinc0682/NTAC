package net.newtownia.NTAC.Checks.Player;

import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Headless extends AbstractCheck implements Listener
{
    ViolationManager vlManager;
    ActionData actionData;

    public Headless(NTAC pl)
    {
        super(pl, "Headless");
        vlManager = new ViolationManager();
        loadConfig();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        if(!isEnabled())
            return;

        Player p = event.getPlayer();
        float pitch = p.getLocation().getPitch();

        if(pitch < -90 || pitch > 90)
        {
            if(p.hasPermission("ntac.bypass.headless"))
                return;

            vlManager.addViolation(p, 1);
            PunishUtils.runViolationAction(p, vlManager, actionData);
        }
    }

    @Override
    public void loadConfig()
    {
        actionData = new ActionData(pl.getConfiguration() ,"Headless.Actions");
    }
}
