package net.newtownia.NTAC.Checks;

import net.newtownia.NTAC.NTAC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class AbstractCheck implements Listener
{
    private boolean enabled = true;
    private String name = "";

    protected final NTAC pl;

    public AbstractCheck(NTAC pl, String name)
    {
        this.pl = pl;
        this.name = name;
    }

    public abstract void loadConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    protected void onPlayerDisconnect(Player p) {}

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        onPlayerDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        onPlayerDisconnect(event.getPlayer());
    }
}
