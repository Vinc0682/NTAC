package net.newtownia.NTAC.Checks.Misc;

import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Vinc0682 on 23.04.2016.
 */
public class AntiChorus extends AbstractCheck
{
    public AntiChorus(NTAC pl)
    {
        super(pl, "Anti-Chorus-Fruit");
    }

    @EventHandler
    public void onPlayerTeleportation(PlayerTeleportEvent event)
    {
        if (isEnabled() && event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)
            event.setCancelled(true);
    }

    @Override
    public void loadConfig() {

    }
}
