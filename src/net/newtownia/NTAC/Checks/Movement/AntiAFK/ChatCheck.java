package net.newtownia.NTAC.Checks.Movement.AntiAFK;

import net.newtownia.NTAC.Checks.Movement.MovementBase;
import net.newtownia.NTAC.NTAC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatCheck extends AbstractAntiAFKCheck implements Listener
{
    private Map<UUID, Long> lastPlayerChatTime;

    private String expectChatMessage = "afeaewwafeawegaefaiöaehfaefuahaef";

    int askThreshold = 10000;

    public ChatCheck(NTAC pl, MovementBase movementBase) {
        super(pl, movementBase, "ChatCheck");

        lastPlayerChatTime = new HashMap<>();

        pl.getServer().getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        lastPlayerChatTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if(event.getMessage().matches(expectChatMessage))
            event.setCancelled(true);

        lastPlayerChatTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public boolean isValidMovement(PlayerMoveEvent e)
    {
        Player p = e.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(!lastPlayerChatTime.containsKey(pUUID))
            lastPlayerChatTime.put(pUUID, System.currentTimeMillis());

        if(hasChatTimePassed(pUUID, askThreshold))
        {
            p.sendMessage("Please Type \"" + expectChatMessage + "\" into the chat");
            return false;
        }

        return true;
    }

    private boolean hasChatTimePassed(UUID pUUID, int milliseconds)
    {
        return System.currentTimeMillis() >= lastPlayerChatTime.get(pUUID) + milliseconds;
    }

    @Override
    public void loadConfig() {

    }
}
