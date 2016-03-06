package net.newtownia.NTAC.Checks.Player;

import com.comphenix.packetwrapper.WrapperPlayClientSettings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Action.ActionData;
import net.newtownia.NTAC.Action.ViolationManager;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import net.newtownia.NTAC.Utils.PunishUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinDerp extends AbstractCheck implements Listener
{
    private Map<UUID, Long> playerLastSkinChangeTime;
    private Map<UUID, Byte> playerLastSkinSettings;
    PacketAdapter clientSettingsEvent;

    int threshold = 100;

    ViolationManager vlManager;
    ActionData actionData;

    public SkinDerp(NTAC pl)
    {
        super(pl, "SkinDerp");

        playerLastSkinChangeTime = new HashMap<>();
        playerLastSkinSettings = new HashMap<>();
        vlManager = new ViolationManager();

        clientSettingsEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.SETTINGS) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                handleClientSettingsEvent(event);
            }
        };

        ProtocolLibrary.getProtocolManager().addPacketListener(clientSettingsEvent);

        loadConfig();
    }

    private void handleClientSettingsEvent(PacketEvent e)
    {
        if(!isEnabled())
            return;

        Player p = e.getPlayer();
        UUID pUUID = p.getUniqueId();

        if(p.hasPermission("ntac.bypass.swingderp"))
            return;

        WrapperPlayClientSettings settingsPacket = new WrapperPlayClientSettings(e.getPacket());

        if(!playerLastSkinChangeTime.containsKey(pUUID))
        {
            playerLastSkinChangeTime.put(pUUID, System.currentTimeMillis());
            return;
        }

        long lastChangeTime = playerLastSkinChangeTime.get(pUUID);
        long currentTime = System.currentTimeMillis();

        if(lastChangeTime + threshold >= currentTime)
        {
            if(!playerLastSkinSettings.containsKey(pUUID))
            {
                playerLastSkinSettings.put(pUUID, (byte)settingsPacket.getDisplayedSkinParts());
                return;
            }

            byte lastSkinSettings = playerLastSkinSettings.get(pUUID);
            byte newSkinSettings = (byte)settingsPacket.getDisplayedSkinParts();

            if(lastSkinSettings != newSkinSettings)
            {
                vlManager.addViolation(p, 1);
                PunishUtils.runViolationAction(p, vlManager, actionData);
            }
        }

        playerLastSkinChangeTime.put(pUUID, currentTime);
        playerLastSkinSettings.put(pUUID, (byte)settingsPacket.getDisplayedSkinParts());
    }

    public void loadConfig()
    {
        threshold = Integer.valueOf(pl.getConfiguration().getString("SkinDerp.Time-Threshold"));
        actionData = new ActionData(pl.getConfiguration(), "SkinDerp.Actions");
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        if(!isEnabled())
            return;

        UUID pUUID = event.getPlayer().getUniqueId();
        vlManager.resetPlayerViolation(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if(!isEnabled())
            return;

        UUID pUUID = event.getPlayer().getUniqueId();
        vlManager.resetPlayerViolation(event.getPlayer());
    }
}
