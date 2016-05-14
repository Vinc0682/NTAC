package net.newtownia.NTAC.Action;

import net.newtownia.NTAC.NTAC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

/**
 * Created by Vinc0682 on 14.05.2016.
 */
public class BanListener implements Listener
{
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event)
    {
        BanManger banManger = NTAC.getInstance().getBanManger();
        UUID pUUID = event.getUniqueId();
        if (banManger.isBanned(pUUID))
        {
            String reason = banManger.getReason(pUUID);
            reason = NTAC.getInstance().getMessageUtils().formatMessage(reason);
            event.setKickMessage(reason);
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
        }
    }
}
