package net.newtownia.NTAC.Checks.Player;

import com.comphenix.packetwrapper.WrapperPlayClientAbilities;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.newtownia.NTAC.Checks.AbstractCheck;
import net.newtownia.NTAC.NTAC;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Vinc0682 on 20.05.2016.
 */
public class Abilities extends AbstractCheck
{
    private boolean fly = true;

    public Abilities(NTAC pl)
    {
        super(pl, "Abilities");

        loadConfig();

        PacketAdapter abilitiesPacketEvent = new PacketAdapter(pl, ListenerPriority.HIGH, PacketType.Play.Client.ABILITIES) {
            @Override
            public void onPacketReceiving(PacketEvent event)
            {
                handleClientAbilitiesEvent(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(abilitiesPacketEvent);
    }

    public void handleClientAbilitiesEvent(PacketEvent event)
    {
        if (!isEnabled())
            return;
        WrapperPlayClientAbilities packet = new WrapperPlayClientAbilities(event.getPacket());
        final Player p = event.getPlayer();
        if (p.hasPermission("ntac.bypass.abilities"))
            return;
        if (fly)
        {
            if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)
                return;
            if (packet.canFly() || packet.isFlying() && !p.getAllowFlight() || !p.isFlying()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.setFlying(false);
                    }
                }.runTask(pl);
            }
        }
    }

    @Override
    public void loadConfig()
    {

    }
}
